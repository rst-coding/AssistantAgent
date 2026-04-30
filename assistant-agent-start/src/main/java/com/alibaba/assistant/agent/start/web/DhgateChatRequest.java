package com.alibaba.assistant.agent.start.web;

import java.util.List;

public class DhgateChatRequest {

	private String message;
	private String sessionId;
	private String lang;
	private String docVersion;
	private Integer topK;
	private Boolean allowWeb;
	private List<DhgateChatMessage> history;

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getSessionId() {
		return sessionId;
	}

	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getLang() {
		return lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public String getDocVersion() {
		return docVersion;
	}

	public void setDocVersion(String docVersion) {
		this.docVersion = docVersion;
	}

	public Integer getTopK() {
		return topK;
	}

	public void setTopK(Integer topK) {
		this.topK = topK;
	}

	public Boolean getAllowWeb() {
		return allowWeb;
	}

	public void setAllowWeb(Boolean allowWeb) {
		this.allowWeb = allowWeb;
	}

	public List<DhgateChatMessage> getHistory() {
		return history;
	}

	public void setHistory(List<DhgateChatMessage> history) {
		this.history = history;
	}
}

