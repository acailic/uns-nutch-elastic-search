package org.apache.nutch.indexer.history;

import java.util.Map;
import java.util.UUID;

public class EventMessage {

	private String msgType;
	private String source;
	private UUID msgUuid;
	private Map<String, Object> params;
	
	public String getMsgType() {
		return msgType;
	}
	
	public void setMsgType(String msgType) {
		this.msgType = msgType;
	}
	
	public String getSource() {
		return source;
	}
	
	public void setSource(String source) {
		this.source = source;
	}
	
	public UUID getMsgUuid() {
		return msgUuid;
	}
	
	public void setMsgUuid(UUID msgUuid) {
		this.msgUuid = msgUuid;
	}
	
	public Map<String, Object> getParams() {
		return params;
	}
	
	public void setParams(Map<String, Object> params) {
		this.params = params;
	}
}
