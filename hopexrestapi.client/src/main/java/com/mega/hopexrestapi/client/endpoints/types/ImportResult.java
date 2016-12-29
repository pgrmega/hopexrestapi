package com.mega.hopexrestapi.client.endpoints.types;

import java.io.InputStream;

public class ImportResult {
	private String _status;
	private String _reason;
	private Boolean _hasRejects;
	private String _rejectsFileName;
	private InputStream _rejectsFileContent;

	public ImportResult(String status, String reason, Boolean hasRejects, String rejectsFileName,
			InputStream rejectsFileContent) {
		_status = status;
		_reason = reason;
		_hasRejects = hasRejects;
		_rejectsFileName = rejectsFileName;
		_rejectsFileContent = rejectsFileContent;
	}

	public String getStatus() {
		return _status;
	}

	public String getReason() {
		return _reason;
	}

	public Boolean getHasRejects() {
		return _hasRejects;
	}

	public String getRejectsFileName() {
		return _rejectsFileName;
	}

	public InputStream getRejectsFileContent() {
		return _rejectsFileContent;
	}
}
