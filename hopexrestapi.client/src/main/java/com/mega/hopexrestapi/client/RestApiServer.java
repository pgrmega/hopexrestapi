package com.mega.hopexrestapi.client;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public interface RestApiServer {
	public class RestApiCall {
		public enum Method {
			GET, POST
		}

		static public class Authentication {
			private String _user;
			private String _password;
			private String _authenticationToken;

			public Authentication(String user, String password) {
				_user = user;
				_password = password;
			}

			public Authentication(String authenticationToken) {
				_authenticationToken = authenticationToken;
			}

			public String getUser() {
				return _user;
			}

			public String getPassword() {
				return _password;
			}

			public String getAuthenticationToken() {
				return _authenticationToken;
			}
		}

		private String _endPointUrl;
		private Method _method = Method.GET;
		private Authentication _authentication;
		private List<String> _uploadedFilePathsList;

		public RestApiCall(String endPointUrl) {
			_endPointUrl = endPointUrl;
		}

		public RestApiCall(Method method, String endPointUrl) {
			_method = method;
			_endPointUrl = endPointUrl;
		}

		public void setAuthentication(Authentication authentication) {
			_authentication = authentication;
		}

		public Authentication getAuthentication() {
			return _authentication;
		}

		public Method getMethod() {
			return _method;
		}

		public String getEndPointUrl() {
			return _endPointUrl;
		}

		private List<String> getUploadedFilesPathsList() {
			if (_uploadedFilePathsList == null) {
				_uploadedFilePathsList = new ArrayList<String>();
			}
			return _uploadedFilePathsList;
		}

		public void addUploadedFile(String importedFilePath) {
			getUploadedFilesPathsList().add(importedFilePath);
		}

		public Iterator<String> getUploadedFiles() {
			return getUploadedFilesPathsList().iterator();
		}

	}

	public class RestApiResult {
		private InputStream _content;

		public RestApiResult(InputStream content) {
			_content = content;
		}

		public InputStream getContent() {
			return _content;
		}
	}

	static public class RestApiException extends Exception {
		private static final long serialVersionUID = -4527219659526288818L;

		public RestApiException(String message) {
			super(message);
		}

		public RestApiException(String message, Throwable cause) {
			super(message, cause);
		}

		public RestApiException(Throwable cause) {
			super(cause);
		}
	}

	public RestApiResult callEndPoint(RestApiCall call) throws RestApiException;
}
