package com.geektime.crawl;

public class ContinuousCrawlerJobInfo {

	public enum State { NONE, RUNNING, CANCELLED, FAILED, SUCCESS }
	
	private State state;
	private Long currentCycle;
	private String currentStage;
	private Long timeStarted;
	private Long timeEnded;
	private Long timeCurrentStageStarted;
	private Integer resultCode;
	private String message;
	
	public State getState() {
		return state;
	}
	public void setState(State state) {
		this.state = state;
	}
	public Long getCurrentCycle() {
		return currentCycle;
	}
	public void setCurrentCycle(Long currentCycle) {
		this.currentCycle = currentCycle;
	}
	public String getCurrentStage() {
		return currentStage;
	}
	public void setCurrentStage(String currentStage) {
		this.currentStage = currentStage;
	}
	public Long getTimeStarted() {
		return timeStarted;
	}
	public void setTimeStarted(Long timeStarted) {
		this.timeStarted = timeStarted;
	}
	public Long getTimeEnded() {
		return timeEnded;
	}
	public void setTimeEnded(Long timeEnded) {
		this.timeEnded = timeEnded;
	}
	public Long getTimeCurrentStageStarted() {
		return timeCurrentStageStarted;
	}
	public void setTimeCurrentStageStarted(Long timeCurrentStageStarted) {
		this.timeCurrentStageStarted = timeCurrentStageStarted;
	}
	public Integer getResultCode() {
		return resultCode;
	}
	public void setResultCode(Integer resultCode) {
		this.resultCode = resultCode;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}	
}
