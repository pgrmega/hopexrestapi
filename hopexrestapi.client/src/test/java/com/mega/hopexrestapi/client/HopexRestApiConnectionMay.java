package com.mega.hopexrestapi.client;

import com.mega.hopexrestapi.client.RestApiServer;
import com.mega.hopexrestapi.client.RestApiServer.RestApiCall.Authentication;
import com.mega.hopexrestapi.client.endpoints.types.Repository;
import com.mega.hopexrestapi.client.endpoints.AuthenticationTokenEndPoint;
import com.mega.hopexrestapi.client.endpoints.EnvironmentsEndPoint;
import com.mega.hopexrestapi.client.endpoints.ImportEndPoint;
import com.mega.hopexrestapi.client.endpoints.RepositoriesEndPoint;
import com.mega.hopexrestapi.client.endpoints.types.Environment;
import com.mega.hopexrestapi.client.endpoints.types.ImportResult;
import com.mega.hopexrestapi.client.RestApiServer.RestApiException;

import static org.assertj.core.api.Assertions.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * Integration tests
 */
public class HopexRestApiConnectionMay {

	static String s_hopexUser;
	static String s_hopexPassword;
	static String s_webAppUrl;
	static String s_environmentId;
	static String s_repositoryId;
	static String s_profileId;
	static String s_importedFilePath;
	static String s_importedFileWithRejectsPath;

	@BeforeClass
	public static void loadConfiguration() throws IOException {
		Properties confProps = new Properties();
		String confPropsFileName = "configuration.properties";

		InputStream inputStream = ClassLoader.getSystemResourceAsStream(confPropsFileName);

		if (inputStream != null) {
			confProps.load(inputStream);
		} else {
			throw new FileNotFoundException("Property file '" + confPropsFileName + "' not found in the classpath");
		}

		s_hopexUser = confProps.getProperty("hopexUser", "mega");
		s_hopexPassword = confProps.getProperty("hopexPassword", "");
		s_webAppUrl = confProps.getProperty("webAppUrl", "http://localhost/HOPEXAPI");
		s_environmentId = confProps.getProperty("environmentId", "eqklkxBHO9mC");
		s_repositoryId = confProps.getProperty("repositoryId", "5tklYIDHO9Zr");
		s_profileId = confProps.getProperty("profileId", "757wuc(SGjpJ");
		try {
			s_importedFilePath = confProps.getProperty("importedFilePath");
			if (s_importedFilePath == null)
			{
				String importedFileName = "library.xmg";
				s_importedFilePath = ClassLoader.getSystemResource(importedFileName).getPath();
			}
			s_importedFileWithRejectsPath = confProps.getProperty("importedFileWithRejectsPath");
			if (s_importedFileWithRejectsPath == null)
			{
				String importedFileName = "library_with_rejects.xmg";
				s_importedFileWithRejectsPath = ClassLoader.getSystemResource(importedFileName).getPath();
			}
		}
		catch(Exception e)
		{
			s_importedFilePath = "";
		}
	}

	@Test
	public final void return_environments_list_from_hopex_service_call()
			throws RestApiException, ParserConfigurationException, SAXException, IOException {
		HopexRestApiServer apiServer = new HopexRestApiServer(s_webAppUrl);
		EnvironmentsEndPoint endPoint = new EnvironmentsEndPoint(apiServer);
		List<Environment> actual = endPoint.getEnvironments();
		assertThat(actual).hasAtLeastOneElementOfType(Environment.class);
	}

	@Test
	public final void return_repositories_list_from_hopex_service_call()
			throws RestApiException, ParserConfigurationException, SAXException, IOException {
		HopexRestApiServer apiServer = new HopexRestApiServer(s_webAppUrl);
		RepositoriesEndPoint endPoint = new RepositoriesEndPoint(apiServer);
		List<Repository> actual = endPoint.getRepositories(s_environmentId);
		assertThat(actual).hasAtLeastOneElementOfType(Repository.class);
	}

	@Test
	public final void return_authentication_token_from_hopex_service_call() throws RestApiException, IOException {
		HopexRestApiServer apiServer = new HopexRestApiServer(s_webAppUrl);
		AuthenticationTokenEndPoint endPoint = new AuthenticationTokenEndPoint(apiServer);
		RestApiServer.RestApiCall.Authentication authentication = new Authentication(s_hopexUser, s_hopexPassword);
		String token = endPoint.getAuthenticationToken(authentication, s_environmentId, s_repositoryId,
				s_profileId);
		assertThat(token).startsWith("¦¢+-");
	}

	@Test
	public final void return_import_file_from_hopex_service_call() throws RestApiException, IOException {
		HopexRestApiServer apiServer = new HopexRestApiServer(s_webAppUrl);
		ImportEndPoint endPoint = new ImportEndPoint(apiServer);
		RestApiServer.RestApiCall.Authentication authentication = new Authentication(s_hopexUser, s_hopexPassword);
		ImportResult importJobResult = endPoint.importFile(authentication, s_environmentId, s_repositoryId,
				s_profileId, s_importedFilePath);
		assertThat(importJobResult.getHasRejects()).isFalse();
	}
	
	@Test
	public final void return_import_file_with_rejects_from_hopex_service_call() throws RestApiException, IOException {
		HopexRestApiServer apiServer = new HopexRestApiServer(s_webAppUrl);
		ImportEndPoint endPoint = new ImportEndPoint(apiServer);
		RestApiServer.RestApiCall.Authentication authentication = new Authentication(s_hopexUser, s_hopexPassword);
		ImportResult importJobResult = endPoint.importFile(authentication, s_environmentId, s_repositoryId,
				s_profileId, s_importedFileWithRejectsPath);
		assertThat(importJobResult.getHasRejects()).isTrue();
	}
}
