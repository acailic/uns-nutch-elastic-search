package com.geektime.crawl;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.nutch.api.model.request.SeedList;
import org.apache.nutch.api.model.request.SeedUrl;
import org.apache.nutch.crawl.DbUpdaterJob;
import org.apache.nutch.crawl.GeneratorJob;
import org.apache.nutch.crawl.InjectorJob;
import org.apache.nutch.fetcher.FetcherJob;
import org.apache.nutch.indexer.IndexingJob;
import org.apache.nutch.parse.ParserJob;
import org.apache.nutch.util.NutchConfiguration;
import org.apache.nutch.util.NutchTool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static com.google.common.base.Preconditions.checkArgument;

public class ContinuousCrawlerJob {

	private static final String SKIP_CYCLES_RULE = "-.*(/[^/]+)/[^/]+\\1/[^/]+\\1/";
	private static final String SKIP_QUERIES_RULE = "-[?*!@=]";
	private static final String SKIP_FILE_TYPES_RULE = "-\\.(gif|GIF|jpg|JPG|png|PNG|ico|ICO|css|CSS|sit|SIT|eps|EPS|wmf|WMF|zip|ZIP|ppt|PPT|mpg|MPG|xls|XLS|gz|GZ|rpm|RPM|tgz|TGZ|mov|MOV|exe|EXE|jpeg|JPEG|bmp|BMP|js|JS)$";
	private static final String SKIP_SCHEMES_RULE = "-^(file|ftp|mailto):";

	private static final Logger LOG = LoggerFactory.getLogger(ContinuousCrawlerJob.class);
	
	private static final String BATCH_ID = "-batchId";
	private static final String TOP_N = "-topN";
	private static final String CYCLES = "-cycles";
	
	private static List<Class<? extends NutchTool>> crawlerFlowJobClasses = buildJobsflow();
	private static Map<String, Object> defaultArgs = buildDefaultArgs();

	private static ContinuousCrawlerJob currentJob;
	private static Future<Integer> currentJobFuture;
	
	private static ReadWriteLock singleRunningJobLock = new ReentrantReadWriteLock();
	
	private static ExecutorService executor = buildExecutor();
	
	private Map<Class<? extends NutchTool>, List<String>> crawlerFlowJobArgs;

	private AtomicBoolean stopRequested = new AtomicBoolean(false);

	private AtomicLong currentCycle = new AtomicLong(0);
	private AtomicReference<String> jobClassName = new AtomicReference<String>("No stage yet");
	private AtomicLong timeStarted = new AtomicLong(-1);
	private AtomicLong timeEnded = new AtomicLong(-1);
	private AtomicLong timeCurrentStageStarted = new AtomicLong(-1);

	private final Configuration conf = NutchConfiguration.create();
	
	
	private ContinuousCrawlerJob() {
		crawlerFlowJobArgs = Maps.newHashMap();
		crawlerFlowJobArgs.put(InjectorJob.class, Lists.newArrayList("urls"));
		crawlerFlowJobArgs.put(GeneratorJob.class, Lists.newArrayList(TOP_N, "80"));
		crawlerFlowJobArgs.put(FetcherJob.class, Lists.newArrayList());
		crawlerFlowJobArgs.put(ParserJob.class, Lists.newArrayList());
		crawlerFlowJobArgs.put(DbUpdaterJob.class, Lists.newArrayList());
		crawlerFlowJobArgs.put(IndexingJob.class, Lists.newArrayList());
	}

	@SuppressWarnings("unchecked")
	private static List<Class<? extends NutchTool>> buildJobsflow() {
		return Lists.<Class<? extends NutchTool>>newArrayList(
				GeneratorJob.class,
				FetcherJob.class,
				ParserJob.class,
				DbUpdaterJob.class,
				IndexingJob.class);
		
	}
	
	private static Map<String, Object> buildDefaultArgs() {
		final HashMap<String, Object> args = Maps.newHashMap();
		args.put("inject", false);
		args.put("stage", 0);
		args.put(TOP_N, "80");
		args.put("cycles", Integer.MAX_VALUE);
		return Collections.unmodifiableMap(args);
	}

	private static ExecutorService buildExecutor() {
		return new ThreadPoolExecutor(1, 1,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(),
                new ContinuousCrawlerThreadFactory(LOG),
                (r, executor) -> LOG.error("submitting runnable to executor was rejected") );
	}
	
	public static void main(String[] args) throws Exception {
		run(args);
	}
	
	public static int run(String[] args) throws Exception {
		final Map<String, Object> ccJobArgs = new HashMap<String, Object>();
		ccJobArgs.put("inject", false);
		for (int i = 0; i < args.length; i++) {
			if ("-inject".equals(args[i])) {
				ccJobArgs.put("inject", true);
			} else if ("-stage".equals(args[i])) {
				checkArgument(args.length > i + 1, "stage argument specified without value");
				int stage = 1;
				try {
					stage = Integer.parseInt( args[i + 1] ) - 1;
					ccJobArgs.put("stage", stage);
				} catch (NumberFormatException e) {
					throw new IllegalArgumentException("argument '-stage' must be a number. Actual value: " + args[i + 1]);
				}
				if (stage < 0 || stage >= crawlerFlowJobClasses.size() ) {
					throw new IllegalArgumentException("argument '-stage' must be a number between 1 and " + crawlerFlowJobClasses.size()
														+ ". Actual value: " + args[i + 1]);
				}
			} else if (BATCH_ID.equals(args[i])) {
				ccJobArgs.put("externalBatchId", args[++i]);
			} else if (TOP_N.equals(args[i])) {
				ccJobArgs.put(TOP_N,args[++i]);
			} else if (CYCLES.equals(args[i])) {
				ccJobArgs.put("cycles", Integer.parseInt(args[++i]));
			} else if ("-seedList".equals(args[i])) {
				ccJobArgs.put("seedList", args[++i]);
			}
		}
		
		return run(ccJobArgs).getCode();
	}

	public static boolean stop(final boolean force) {
		singleRunningJobLock.writeLock().lock();
		try {
			if ( isRunning(currentJobFuture) ) {
				currentJob.setStopRequested(true);
				if (force) {
					currentJobFuture.cancel(true);
				}
			}
			return true; // TODO test that stop actually succeeded
		} finally {
			singleRunningJobLock.writeLock().unlock();
		}
	}
	
	public static ContinuousCrawlerJobInfo getCurrentJobInfo() {
		singleRunningJobLock.readLock().lock();
		try {
			final ContinuousCrawlerJobInfo info = new ContinuousCrawlerJobInfo();
			if ( currentJobFuture == null ) {
				info.setState(ContinuousCrawlerJobInfo.State.NONE);
				info.setMessage("no continuous crawler job was run yet");
				return info;
			}
			
			if ( ! currentJobFuture.isDone() ) {
				info.setState(ContinuousCrawlerJobInfo.State.RUNNING);
				info.setMessage("continuous crawler job is currently running");
			} else {
				if (currentJobFuture.isCancelled() ) {
					info.setState(ContinuousCrawlerJobInfo.State.CANCELLED);
				} else { // done and not cancelled
					try {
						final Integer resultCode = currentJobFuture.get();
						if (resultCode == 0) {
							info.setState(ContinuousCrawlerJobInfo.State.SUCCESS);
							info.setMessage("continuous crawler job completed successfully");
						} else {
							info.setState(ContinuousCrawlerJobInfo.State.FAILED);
							info.setResultCode(resultCode);
						}
					} catch (InterruptedException e) {
						LOG.error("not supposed to reach here! Interrupted while waiting for result of DONE Future task", e);
					} catch (ExecutionException e) {
						info.setState(ContinuousCrawlerJobInfo.State.FAILED);
						info.setMessage(ExceptionUtils.getStackTrace(e));
					}
				}
				info.setTimeEnded(currentJob.getTimeEnded());
			}
			info.setCurrentCycle(currentJob.getCurrentCycle());
			info.setCurrentStage(currentJob.getCurrentStage());
			info.setTimeStarted(currentJob.getTimeStarted());
			info.setTimeCurrentStageStarted(currentJob.getTimeCurrentStageStarted());
			return info;
		} finally {
			singleRunningJobLock.readLock().unlock();
		}
	}
	
	private Long getTimeEnded() {
		return this.timeEnded.get();
	}

	private Long getTimeCurrentStageStarted() {
		return this.timeCurrentStageStarted.get();
	}

	private Long getTimeStarted() {
		return this.timeStarted.get();
	}

	private String getCurrentStage() {
		return this.jobClassName.get();
	}

	private Long getCurrentCycle() {
		return this.currentCycle.get();
	}

	public static boolean isRunning( final Future<?> f ) {
		return f != null && ! f.isDone();
	}
	
	public static ContinuousCrawlerJobResponse run(final Map<String, Object> args) throws Exception {
		singleRunningJobLock.writeLock().lock();
		final boolean succeeded;
		try {
			if ( isRunning(currentJobFuture) ) {
				succeeded = false;
			} else {
				currentJob = new ContinuousCrawlerJob();
				currentJobFuture = executor.submit( () -> currentJob.runJobInstance(args) );
				succeeded = true;
			}
		} finally {
			singleRunningJobLock.writeLock().unlock();
		}
		
		final ContinuousCrawlerJobResponse response;
		if (succeeded) {
			response = ContinuousCrawlerJobResponse.newSuccessResponse("continuous crawler job created successfully");
		} else {
			final String msg = "continuous crawler job already running. It has to be stopped before running a new one.";
			LOG.error(msg);
			response = ContinuousCrawlerJobResponse.newFailResponse(msg);
		}
		return response;
	}
	
	private int runJobInstance(final Map<String, Object> args) throws Exception {
		this.timeStarted.set(System.currentTimeMillis());
		int res = 0;
		final String seedListJsonStr = (String) getArgValue("seedList", args);
		if ( seedListJsonStr != null ) { // seed is requested - so seed and inject
			res = runSeedAndInject(seedListJsonStr);
		} else if ( (boolean) getArgValue("inject", args) ) { // only inject requested
			res = runInject();
		}
		int jobClassIndex = (int) getArgValue("stage", args);		
		final String externalBatchId = (String) args.get("externalBatchId");
		setNamedArgument(TOP_N, (String) getArgValue(TOP_N, args), GeneratorJob.class);
		int maxCycles = (int) getArgValue("cycles", args);
		String batchId = null;
		currentCycle.set(1);
		while (res == 0 && currentCycle.get() <= maxCycles ) {
			if (stopRequested.get() == true) {
				LOG.info("stop requested. Not proceeding to next stage. Exitting.");
				break;
			}
			final Class<? extends NutchTool> jobClass = crawlerFlowJobClasses.get(jobClassIndex);
			batchId = determineBatchId(jobClassIndex, batchId, externalBatchId);
			LOG.info(String.format("starting crawl stage: %s, crawl cycle: %d", jobClass.getSimpleName(), currentCycle.get()));
			res = runJob(jobClass);
			jobClassName.set("between stages");
			if (res == 0) {
				jobClassIndex++;
				jobClassIndex = jobClassIndex % crawlerFlowJobClasses.size();
				if (jobClassIndex == 0 ) {
					currentCycle.incrementAndGet();
				}
			} else { // retry in 5 sec.
				LOG.error("job " + jobClass.getSimpleName() + " returned error code result: " + res + ". Retrying in 5 seconds...");
				Thread.sleep(5000);
				res = 0;
			}
		}
		
		this.timeEnded.set(System.currentTimeMillis());
		return res;
	}

	private int runSeedAndInject(final String seedListJsonStr) throws Exception {
		int res;
		final UrlSeeder seeder = runSeed(seedListJsonStr);
		final String seedDirPath = seeder.getSeedDir();
		// direct inject job to new directory that contains seed file
		final List<String> injectArgs = crawlerFlowJobArgs.get(InjectorJob.class);
		injectArgs.clear();
		injectArgs.add(seedDirPath);
		
		final SeedList seedList = seeder.getSeedList();
		configureRegexUrlFilterRules(seedList);
		
		res = runInject();
		seeder.close();
		return res;
	}

	private void configureRegexUrlFilterRules(final SeedList seedList) throws URISyntaxException {
		final StringBuilder sb = new StringBuilder();
		sb.append(SKIP_SCHEMES_RULE).append("\n");
		sb.append(SKIP_FILE_TYPES_RULE).append("\n");
		sb.append(SKIP_QUERIES_RULE).append("\n");
		sb.append(SKIP_CYCLES_RULE).append("\n");
		for (final SeedUrl seedUrl : seedList.getSeedUrls()) {
			final String url = seedUrl.getUrl();
			// +^http://([a-z0-9]*\.)*tianya.cn/
			final URI uri = new URI(url);
			final String scheme = uri.getScheme();
			final String authority = uri.getAuthority();
			sb.append(String.format("+^%s://([a-z0-9]*\\.)*%s/", scheme, authority)).append("\n");
			System.out.println("****** adding: " + String.format("+^%s://([a-z0-9]*\\.)*%s/", scheme, authority));
		}
		LOG.debug(String.format("Using following regex-urlfilter rules: %s", sb.toString()));
		System.out.println(String.format("Using following regex-urlfilter rules: %s", sb.toString()));
		conf.set("urlfilter.regex.rules", sb.toString());
	}

	private int runInject() throws InstantiationException,
			IllegalAccessException, Exception {
		int res;
		res = runJob(InjectorJob.class);
		if (res != 0) {
			System.exit(res);
		}
		return res;
	}

	private UrlSeeder runSeed(final String seedListJsonStr) throws IOException {
		final SeedList seedList = new Gson().fromJson(seedListJsonStr, SeedList.class);
		final UrlSeeder urlSeeder = new UrlSeeder(seedList, conf);
		urlSeeder.seed();
		return urlSeeder;
	}

	private Object getArgValue(final String name, final Map<String, Object> args) {
		if (args.containsKey(name)) {
			return args.get(name);
		}
		return defaultArgs.get(name);
	}
	
	private String determineBatchId(final int jobClassIndex, String batchId, final String externalBatchId) {
		final Class<? extends NutchTool> jobClass = crawlerFlowJobClasses.get(jobClassIndex);
		if (GeneratorJob.class == jobClass) {
			// batchId == null actually indicates that this is the first cycle of this crawl invocation
			if (batchId == null && externalBatchId != null) {
				batchId = externalBatchId;
			} else {
				// generate batchId
				int randomSeed = Math.abs(new Random().nextInt());
				batchId = (System.currentTimeMillis() / 1000) + "-" + randomSeed;
			}
		} else {
			if (batchId == null) {
				checkArgument(externalBatchId != null, "batchId must be specified when invoking crawl from " + jobClass.getSimpleName() + "(" + jobClassIndex + ") stage.");
				batchId = externalBatchId;
			}
		}
		setBatchId(batchId, jobClass);
		return batchId;
	}

	private void setBatchId(String batchId, final Class<? extends NutchTool> jobClass) {
		List<String> jobArgsList = crawlerFlowJobArgs.get(jobClass);		
		boolean generateStage = ( GeneratorJob.class == jobClass );
		if ( ! generateStage) {
			// batchId should always be the first argument (and it's specified without preceding arg name)
			if (jobArgsList.isEmpty()) {
				jobArgsList.add(0, batchId);
			} else {
				jobArgsList.set(0, batchId); // replace batchId
			}
			return;
		}
		setNamedArgument(BATCH_ID, batchId, jobClass);
	}
	
	private void setNamedArgument(String name, String value, final Class<? extends NutchTool> jobClass) {
		List<String> jobArgsList = crawlerFlowJobArgs.get(jobClass);		
		for (int i = 0; i < jobArgsList.size(); i++) {
			String arg = jobArgsList.get(i);
			if (arg.equals(name)) {
				jobArgsList.set(i + 1, value);
				return;
			}
		}
		// if got here: value doesn't exist yet
		jobArgsList.add(name);
		jobArgsList.add(value);
	}

	public void setStopRequested(final boolean stopRequested) {
		this.stopRequested.set( stopRequested );
	}
	
	private int runJob(final Class<? extends NutchTool> jobClass)
			throws InstantiationException, IllegalAccessException, Exception {
		this.jobClassName.set( jobClass.getName() );
		this.timeCurrentStageStarted.set(System.currentTimeMillis());
		List<String> jobArgsList = crawlerFlowJobArgs.get(jobClass);
		String[] jobArgs = new String[jobArgsList.size()];
		final NutchTool jobInstance = jobClass.newInstance();
		int res = ToolRunner.run(this.conf , (Tool) jobInstance, jobArgsList.toArray(jobArgs));
		return res;
	}
}
