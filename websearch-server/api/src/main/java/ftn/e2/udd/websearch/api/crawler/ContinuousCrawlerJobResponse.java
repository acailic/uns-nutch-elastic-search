package ftn.e2.udd.websearch.api.crawler;

public class ContinuousCrawlerJobResponse {

	public enum State { SUCCESS, FAIL }
	
	private State state;
	private String msg;
	private int code;

	public State getState() {
		return state;
	}
	public void setState(State state) {
		this.state = state;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
}
