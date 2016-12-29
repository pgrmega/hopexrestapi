package com.mega.hopexrestapi.client.endpoints;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.mega.hopexrestapi.client.RestApiServer;
import com.mega.hopexrestapi.client.RestApiServer.RestApiCall;
import com.mega.hopexrestapi.client.RestApiServer.RestApiException;
import com.mega.hopexrestapi.client.RestApiServer.RestApiResult;
import com.mega.hopexrestapi.client.RestApiServer.RestApiCall.Authentication;

public class AuthenticationTokenEndPoint {
	private RestApiServer _restApiServer;

	public AuthenticationTokenEndPoint(RestApiServer restApiServer) {
		_restApiServer = restApiServer;
	}
	
	public String getAuthenticationToken(Authentication authentication, String environmentId, String repositoryId,
			String profileId) throws RestApiException, IOException {
		RestApiCall call = new RestApiCall("environments/" + environmentId + "/repositories/" + repositoryId
				+ "/profiles/" + profileId + "/authenticationToken");
		call.setAuthentication(authentication);
		RestApiResult response = _restApiServer.callEndPoint(call);
		BufferedReader reader = new BufferedReader(new InputStreamReader(response.getContent()));
		StringBuffer result = new StringBuffer();
		String line;
		while ((line = reader.readLine()) != null) {
			result.append(line);
		}
		return result.toString();
	}
}
