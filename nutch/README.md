# Nutch-Patch : Enhanced features for [Nutch Crawler](http://nutch.apache.org/)

This is a fork of [Apache Nutch (branch 2.3)](https://github.com/apache/nutch/tree/branch-2.3), enhanced to fulfil requirements of projects I performed.

## Features:

### FtnContinousCrawl
One Job to rule them all.

- FtnContinousCrawl wraps up all Nutch stages (generate, fetch, parse, updateDB and index) into a single job.
- Set number of crawl cycles per run (`cycles` arg)
- Set at which crawl stage to start the first cycle (`stage` arg)
- Inject pre-seeded urls (`inject` arg)
- Dynamically seed new urls and auto-inject them (`seedUrls` arg)
- Runs independently or on Nutch Server

### REST API (HTTP API for the least ;) )
When ContinousCrawlJob runs on Nutch Server, it exposes common HTTP API:

- Create new continous crawl
- Stop current crawl
- get current crawl status

### Crawled-Content History / Versions
Whenever a page's contect is re-fetched, new content is compared to previous content. If it was changed, the old content is saved to a different MongoDB collection, eventually creating a list of versioned content with fetch-dates.

There are [ready-made Eclipse launchers](https://github.com/yaireclipse/nutch-patch/tree/master/eclipse_run_configurations), for each crawl stage, as well as for the continous crawl job and the Nutch Server. They can be used for development with Eclipse, but can also be converted to IntelliJ launcher via [Eclipser](https://github.com/kukido/eclipser).

### Notes
It is pre-configured to work with MongoDB as Nutch's storage and Elasticsearch as the index storage.

## Disclaimer:
Nutch-patch is, at most, in beta. I'd be happy if it'll be useful for others, but it's here mainly because I need it.
