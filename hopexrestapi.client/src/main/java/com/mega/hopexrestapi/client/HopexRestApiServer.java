package com.mega.hopexrestapi.client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Iterator;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.HttpClients;

import com.mega.hopexrestapi.client.RestApiServer.RestApiCall.Authentication;
import com.mega.hopexrestapi.client.RestApiServer.RestApiCall.Method;

public class HopexRestApiServer implements RestApiServer {
	private String _webAppUrl;

	public HopexRestApiServer(String webAppUrl) {
		_webAppUrl = webAppUrl;
	}

	static public class HopexRestApiException extends RestApiException {
		private static final long serialVersionUID = -7310108783992495248L;

		public HopexRestApiException(String message) {
			super(message);
		}

		public HopexRestApiException(String message, Throwable cause) {
			super(message, cause);
		}

		public HopexRestApiException(Throwable cause) {
			super(cause);
		}
	}

	public RestApiResult callEndPoint(RestApiCall call) throws HopexRestApiException {
		int responseStatusCode;
		String responseStatusText;
		InputStream responseContent;
		try {
			HttpClient httpClient = HttpClients.createDefault();
			String completeEndPointUrl = _webAppUrl + "/restapi/v1/" + call.getEndPointUrl();
			HttpUriRequest httpRequest;
			if (call.getMethod() == Method.POST) {
				HttpPost httpPost = new HttpPost(completeEndPointUrl);
				setRequestUploadedFiles(httpPost, call.getUploadedFiles());
				httpRequest = httpPost;
			} else {
				httpRequest = new HttpGet(completeEndPointUrl);
			}
			setRequestAuthentication(httpRequest, call.getAuthentication());
			HttpResponse response = httpClient.execute(httpRequest);
			responseStatusCode = response.getStatusLine().getStatusCode();
			responseStatusText = response.getStatusLine().getReasonPhrase();
			responseContent = response.getEntity().getContent();
		} catch (Exception e) {
			throw new HopexRestApiException(e);
		}
		if (responseStatusCode != HttpStatus.SC_ACCEPTED && responseStatusCode != HttpStatus.SC_OK) {
			String returnedStatus = responseStatusCode + " (" + responseStatusText + ")";
			throw new HopexRestApiException("Hopex rest api call returned unexpected http status " + returnedStatus);
		}
		return new RestApiResult(responseContent);
	}

	private void setRequestAuthentication(HttpUriRequest httpRequest, Authentication authentication) {
		if (authentication != null) {
			String user = authentication.getUser();
			if (user != null) {
				String password = authentication.getPassword();
				if (password == null)
					password = "";
				httpRequest.addHeader("Authorization", "HOPEXBASIC " + user + ":" + password);
			} else {
				String authenticationToken = authentication.getAuthenticationToken();
				if (authenticationToken != null)
					httpRequest.addHeader("Authorization", "HOPEXTOKEN " + authenticationToken);
			}
		}
	}

	private void setRequestUploadedFiles(HttpPost httpPost, Iterator<String> uploadedFilesIterator)
			throws FileNotFoundException {
		if (uploadedFilesIterator.hasNext()) {
			MultipartEntityBuilder builder = MultipartEntityBuilder.create();
			while (uploadedFilesIterator.hasNext()) {
				String filePath = uploadedFilesIterator.next();
				File f = new File(filePath);
				builder.addBinaryBody("file1", new FileInputStream(f), ContentType.APPLICATION_OCTET_STREAM,
						f.getName());
			}
			HttpEntity multipart = builder.build();
			httpPost.setEntity(multipart);
		}
	}

}
