package com.geektime.crawl;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.geektime.crawl.ContinuousCrawlerJobResponse.State;

@Produces({ MediaType.APPLICATION_JSON })
@Path(value = "/ccjob")
public class ContinuousCrawlerJobResource {

	private static final Logger LOG = LoggerFactory.getLogger(ContinuousCrawlerJobResource.class);
	
	  @GET
	  @Path(value = "/")
	  public ContinuousCrawlerJobInfo getJobs(@QueryParam("crawlId") String crawlId) {
	    return ContinuousCrawlerJob.getCurrentJobInfo();
	  }
	
	  @GET
	  @Path(value = "/stop")
	  public boolean stop(@QueryParam("force") boolean force) {
	    return ContinuousCrawlerJob.stop(force);
	  }

	  @POST
	  @Path(value = "/create")
	  @Consumes(MediaType.APPLICATION_JSON)
	  public ContinuousCrawlerJobResponse create(final ContinuousCrawlJobConfig ccJobConfig) {
	    try {
			return ContinuousCrawlerJob.run(ccJobConfig.getArgs());
		} catch (Exception e) {
			LOG.error("error during " + ContinuousCrawlerJob.class.getSimpleName() + " creation", e );
			final ContinuousCrawlerJobResponse response = new ContinuousCrawlerJobResponse();
			response.setCode(1);
			response.setState(State.FAIL);
			response.setMsg(ExceptionUtils.getStackTrace(e));
			return response;
		}
	  }
}
