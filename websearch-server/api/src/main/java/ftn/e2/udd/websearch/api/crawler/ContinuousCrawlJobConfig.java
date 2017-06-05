package ftn.e2.udd.websearch.api.crawler;

import java.io.Serializable;
import java.util.Map;

public class ContinuousCrawlJobConfig  implements Serializable {

	private Map<String, Object> args;

	public Map<String, Object> getArgs() {
		return args;
	}

	public void setArgs(Map<String, Object> args) {
		this.args = args;
	}
}
