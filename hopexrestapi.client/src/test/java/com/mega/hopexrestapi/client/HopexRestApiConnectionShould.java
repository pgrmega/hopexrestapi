package com.mega.hopexrestapi.client;

import com.mega.hopexrestapi.client.RestApiServer;
import com.mega.hopexrestapi.client.RestApiServer.RestApiException;
import com.mega.hopexrestapi.client.endpoints.types.Repository;
import com.mega.hopexrestapi.client.endpoints.EnvironmentsEndPoint;
import com.mega.hopexrestapi.client.endpoints.RepositoriesEndPoint;
import com.mega.hopexrestapi.client.endpoints.types.Environment;

import static org.assertj.core.api.Assertions.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * Unit tests
 */
public class HopexRestApiConnectionShould {

	class TestRestApiServer implements RestApiServer {
		InputStream _restApiResult;

		public TestRestApiServer(InputStream restApiResult) {
			_restApiResult = restApiResult;
		}

		public RestApiResult callEndPoint(RestApiCall call) {
			return new RestApiResult(_restApiResult);
		}
	}

	private static final class HopexWebApiEnvironmentComparator implements Comparator<Environment> {
		@Override
		public int compare(Environment o1, Environment o2) {
			int result = o1.getIdentifier().compareTo(o2.getIdentifier());
			if (result == 0) {
				result = o1.getName().compareTo(o2.getName());
			}
			return result;
		}
	}

	private static final class HopexWebApiRepositoryComparator implements Comparator<Repository> {
		@Override
		public int compare(Repository o1, Repository o2) {
			int result = o1.getIdentifier().compareTo(o2.getIdentifier());
			if (result == 0) {
				result = o1.getName().compareTo(o2.getName());
			}
			return result;
		}
	}

	@Test
	public final void return_environments_list_matching_environments_service_xml_result()
			throws RestApiException, ParserConfigurationException, SAXException, IOException {
		String xmlEnvironments = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><Environments><Environment><Id>7zZn1PSHO97B</Id><Name>C:\\Users\\Public\\Documents\\HOPEX V2\\EnvTestsLab_SQLEXPRESS_770int_default_4558</Name></Environment><Environment><Id>OA)wplUHOzGC</Id><Name>C:\\Users\\Public\\Documents\\HOPEX V2\\EnvTestsLab_ORACLE_EXPRESS_770int_default_4558</Name></Environment></Environments>";
		InputStream _restApiResult = new ByteArrayInputStream(xmlEnvironments.getBytes(StandardCharsets.UTF_8));
		TestRestApiServer apiServer = new TestRestApiServer(_restApiResult);
		EnvironmentsEndPoint endPoint = new EnvironmentsEndPoint(apiServer);
		List<Environment> actual = endPoint.getEnvironments();
		List<Environment> expected = new ArrayList<Environment>();
		expected.add(new Environment("7zZn1PSHO97B",
				"C:\\Users\\Public\\Documents\\HOPEX V2\\EnvTestsLab_SQLEXPRESS_770int_default_4558"));
		expected.add(new Environment("OA)wplUHOzGC",
				"C:\\Users\\Public\\Documents\\HOPEX V2\\EnvTestsLab_ORACLE_EXPRESS_770int_default_4558"));
		assertThat(actual).usingElementComparator(new HopexWebApiEnvironmentComparator()).containsAll(expected);
	}

	@Test
	public final void return_repositories_list_matching_repositories_service_xml_result()
			throws RestApiException, ParserConfigurationException, SAXException, IOException {
		String xmlrepositories = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?><EnvironmentDatabases Name=\"9vpK)C5MOzmP\"><Base><Id>WvpKSD7MO9lN</Id><Name>MEGA Tutorial</Name></Base><Base><Id>(xpK0M7MOLqW</Id><Name>SOHO</Name></Base><Base><Id>4upKk27MOvxv</Id><Name>EA</Name></Base></EnvironmentDatabases>";
		InputStream _restApiResult = new ByteArrayInputStream(xmlrepositories.getBytes(StandardCharsets.UTF_8));
		TestRestApiServer apiServer = new TestRestApiServer(_restApiResult);
		RepositoriesEndPoint endPoint = new RepositoriesEndPoint(apiServer);
		List<Repository> actual = endPoint.getRepositories("9vpK)C5MOzmP");
		List<Repository> expected = new ArrayList<Repository>();
		expected.add(new Repository("WvpKSD7MO9lN", "MEGA Tutorial"));
		expected.add(new Repository("(xpK0M7MOLqW", "SOHO"));
		expected.add(new Repository("4upKk27MOvxv", "EA"));
		assertThat(actual).usingElementComparator(new HopexWebApiRepositoryComparator()).containsAll(expected);
	}
}
