package com.geektime.crawl;

public class ContinuousCrawlerJobResponse {

	public enum State { SUCCESS, FAIL }
	
	private State state;
	private String msg;
	private int code;

	public static ContinuousCrawlerJobResponse newSuccessResponse(final String msg) {
		final ContinuousCrawlerJobResponse res = new ContinuousCrawlerJobResponse();
		res.setState(State.SUCCESS);
		res.setCode(0);
		res.setMsg(msg);
		return res;
	}
	
	public static ContinuousCrawlerJobResponse newFailResponse(final String msg) {
		final ContinuousCrawlerJobResponse res = new ContinuousCrawlerJobResponse();
		res.setState(State.FAIL);
		res.setCode(1);
		res.setMsg(msg);
		return res;
	}
	
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
