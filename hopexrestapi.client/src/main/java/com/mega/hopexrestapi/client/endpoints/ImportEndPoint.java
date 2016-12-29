package com.mega.hopexrestapi.client.endpoints;

import java.io.InputStream;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

import com.mega.hopexrestapi.client.RestApiServer;
import com.mega.hopexrestapi.client.HopexRestApiServer.HopexRestApiException;
import com.mega.hopexrestapi.client.RestApiServer.RestApiCall;
import com.mega.hopexrestapi.client.RestApiServer.RestApiException;
import com.mega.hopexrestapi.client.RestApiServer.RestApiResult;
import com.mega.hopexrestapi.client.endpoints.types.ImportResult;
import com.mega.hopexrestapi.client.RestApiServer.RestApiCall.Authentication;
import com.mega.hopexrestapi.client.RestApiServer.RestApiCall.Method;

public class ImportEndPoint {
	private RestApiServer _restApiServer;

	public ImportEndPoint(RestApiServer restApiServer) {
		_restApiServer = restApiServer;
	}

	public class HopexRestApiImportException extends HopexRestApiException {
		private static final long serialVersionUID = 1374557042927451801L;

		public HopexRestApiImportException(String message) {
			super(message);
		}

		public HopexRestApiImportException(String message, Throwable cause) {
			super(message, cause);
		}

		public HopexRestApiImportException(Throwable cause) {
			super(cause);
		}
	}

	public ImportResult importFile(Authentication authentication, String environmentId, String repositoryId,
			String profileId, String importedFilePath) throws RestApiException {
		String importJobId = startImportFileJob(authentication, environmentId, repositoryId, profileId,
				importedFilePath);
		ImportJobProgressInformation importJobProgress = null;
		do {
			importJobProgress = getImportJobProgress(authentication, environmentId, repositoryId, profileId,
					importJobId);
		} while (importJobProgress.Status.compareTo("RUNNING")==0);
		Boolean importHasRejects = importJobProgress.HasRejects;
		String rejectsFileName = null;
		InputStream rejectsFileContent = null;
		if (importHasRejects)
		{
			InputStream importRejectsFileContent = getImportRejectsFileContent(authentication, environmentId,
					repositoryId, profileId, importJobId);
			rejectsFileName = importJobProgress.RejectsFileName;
			rejectsFileContent = importRejectsFileContent;
		}
		return new ImportResult(importJobProgress.Status, importJobProgress.Reason, importJobProgress.HasRejects, rejectsFileName, rejectsFileContent);
	}

	private static class ImportJobProgressInformation {
		public String Status = "";
		public String Reason = "";
		public Boolean HasRejects = false;
		public String RejectsFileName = "";

		static ImportJobProgressInformation createFromJson(InputStream in) throws HopexRestApiException {
			ImportJobProgressInformation jobProgress = null;
			try {
				JsonReader reader = Json.createReader(in);
				JsonObject jsonJobProgress = reader.readObject();
				JsonObject jsonJobInfo = jsonJobProgress.isNull("job_info") ? null
						: jsonJobProgress.getJsonObject("job_info");
				jobProgress = new ImportJobProgressInformation();
				jobProgress.Status = jsonJobProgress.getString("job_status","");
				if (jsonJobInfo != null) {
					jobProgress.Reason = jsonJobInfo.getString("reason","");
					jobProgress.HasRejects = jsonJobInfo.getBoolean("rejects",false);
					jobProgress.RejectsFileName = jsonJobInfo.getString("rejects_file_name","");
					if (jobProgress.RejectsFileName.isEmpty()) {
						jobProgress.HasRejects = false;
					}
				}
			} catch (Exception e) {
				throw new HopexRestApiException("Import job status returned a invalid result.", e);
			}
			return jobProgress;
		}
	}

	private ImportJobProgressInformation getImportJobProgress(Authentication authentication, String environmentId,
			String repositoryId, String profileId, String importJobId) throws RestApiException {
		RestApiCall call = new RestApiCall("environments/" + environmentId + "/repositories/" + repositoryId
				+ "/profiles/" + profileId + "/import/job/" + importJobId);
		call.setAuthentication(authentication);
		RestApiResult response = _restApiServer.callEndPoint(call);
		return ImportJobProgressInformation.createFromJson(response.getContent());
	}

	private InputStream getImportRejectsFileContent(Authentication authentication, String environmentId,
			String repositoryId, String profileId, String importJobId) throws RestApiException {
		RestApiCall call = new RestApiCall("environments/" + environmentId + "/repositories/" + repositoryId
				+ "/profiles/" + profileId + "/import/job/" + importJobId + "/rejects");
		call.setAuthentication(authentication);
		RestApiResult response = _restApiServer.callEndPoint(call);
		return response.getContent();
	}

	private String startImportFileJob(Authentication authentication, String environmentId, String repositoryId,
			String profileId, String importedFilePath) throws RestApiException {
		RestApiCall call = new RestApiCall(Method.POST, "environments/" + environmentId + "/repositories/"
				+ repositoryId + "/profiles/" + profileId + "/import");
		call.setAuthentication(authentication);
		call.addUploadedFile(importedFilePath);
		RestApiResult response = _restApiServer.callEndPoint(call);
		String jobId = "";
		try {
			JsonReader reader = Json.createReader(response.getContent());
			JsonObject jsonObject = reader.readObject();
			jobId = jsonObject.getJsonString("job_id").getString();
		} catch (Exception e) {
			throw new HopexRestApiImportException(e);
		}
		return jobId;
	}

}
