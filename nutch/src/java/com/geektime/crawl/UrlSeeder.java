package com.geektime.crawl;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.nutch.api.model.request.SeedList;
import org.apache.nutch.api.model.request.SeedUrl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

import static javax.ws.rs.core.Response.status;

public class UrlSeeder {

	private static final String SEED_FILE_NAME = "seed.txt";
	private static final Logger log = LoggerFactory.getLogger(UrlSeeder.class);
	
	private final Configuration conf;
	private final SeedList seedList;
	private final Path seedFile;
	
	public UrlSeeder(SeedList seedList, Configuration conf) {
		this.seedList = seedList;
		this.conf = conf;
		this.seedFile = createSeedFile();
	}

	/**
	 * Method creates seed list file and returns temporary directory path
	 * 
	 * @throws IOException
	 */
	public void seed() throws IOException {
		if (seedList == null) {
			throw new WebApplicationException(Response
					.status(Status.BAD_REQUEST)
					.entity("Seed list cannot be empty!").build());
		}
		
        try ( final BufferedWriter writer = getWriter(seedFile) ) {
			final Collection<SeedUrl> seedUrls = seedList.getSeedUrls();
			if (CollectionUtils.isNotEmpty(seedUrls)) {
				for (SeedUrl seedUrl : seedUrls) {
					writeUrl(writer, seedUrl);
				}
			}
        }
	}

	public String getSeedDir() {
		final Path parent = seedFile.getParent();
		return parent.getParent().getName() + "/" + parent.getName();
	}
	
	public void close() {
		try {
			final FileSystem fs = FileSystem.get(conf);
			fs.delete(seedFile.getParent(), true /*recursive*/);
		} catch (IOException e) {
			throw handleException(e);
		}
	}
	
	/**
	 * @return a copy of the SeedList.
	 */
	public SeedList getSeedList() {
		final SeedList copy = new SeedList();
		copy.setId(seedList.getId());
		copy.setName(seedList.getName());
		final List<SeedUrl> urlsCopy = new ArrayList<SeedUrl>();
		for (final SeedUrl seedUrl : seedList.getSeedUrls()) {
			final SeedUrl seedUrlCopy = new SeedUrl();
			seedUrlCopy.setId(seedUrl.getId());
			seedUrlCopy.setSeedList(seedUrl.getSeedList());
			seedUrlCopy.setUrl(seedUrl.getUrl());
			urlsCopy.add(seedUrlCopy);
		}
		copy.setSeedUrls(urlsCopy);
		return copy;
	}
	
	private void writeUrl(final BufferedWriter writer, final SeedUrl seedUrl) {
		try {
			writer.write(seedUrl.getUrl());
			writer.newLine();
			writer.flush();
		} catch (IOException e) {
			throw handleException(e);
		}
	}

	private BufferedWriter getWriter(final Path seedFile) {
		try {
			final FileSystem fs = FileSystem.get(conf);
			final FSDataOutputStream seedFileOutputStream = fs.create(seedFile);
			return new BufferedWriter( new OutputStreamWriter(seedFileOutputStream) );
		} catch (IOException e) {
			throw handleException(e);
		}
	}

	private Path createSeedFile() {
		/* randomize seedDir's name, since InjectorJob accepts seed files directory
		   and not a single seed file. So need to verify there's no mixing of seed files
		   from one crawl request to another */
		final String urlsDir = conf.get("continous.urls.dir");
		final Path seedDir = new Path(urlsDir, UUID.randomUUID().toString());
		final Path seedFile = new Path(seedDir , SEED_FILE_NAME);
		return seedFile;
	}

	private RuntimeException handleException(final Exception e) {
		log.error("Cannot create seed file!", e);
		return new WebApplicationException(status(Status.INTERNAL_SERVER_ERROR)
				.entity("Cannot create seed file!").build());
	}
}
