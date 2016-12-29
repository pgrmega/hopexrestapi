package com.mega.hopexrestapi.client.endpoints;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import com.mega.hopexrestapi.client.RestApiServer;
import com.mega.hopexrestapi.client.RestApiServer.RestApiCall;
import com.mega.hopexrestapi.client.RestApiServer.RestApiException;
import com.mega.hopexrestapi.client.RestApiServer.RestApiResult;
import com.mega.hopexrestapi.client.endpoints.types.Repository;

public class RepositoriesEndPoint {
	private RestApiServer _restApiServer;

	public RepositoriesEndPoint(RestApiServer restApiServer) {
		_restApiServer = restApiServer;
	}


	public List<Repository> getRepositories(String environmentId)
			throws RestApiException, ParserConfigurationException, SAXException, IOException {
		RestApiCall call = new RestApiCall("environments/" + environmentId + "/repositories");
		RestApiResult response = _restApiServer.callEndPoint(call);
		return parseRepositories(response.getContent());
	}

	private List<Repository> parseRepositories(InputStream xmlStream)
			throws ParserConfigurationException, SAXException, IOException {
		List<Repository> repositoriesList = new ArrayList<Repository>();
		DefaultHandler handler = new DefaultHandler() {
			Stack<String> _elementsStack = new Stack<String>();
			String _lastElement = "";
			String _nextToLastElement = "";
			String _currentRepositoryIdentifier = "";
			String _currentRepositoryName = "";

			public void startElement(String uri, String localName, String qName, Attributes attributes) {
				_elementsStack.push(qName);
				_nextToLastElement = _lastElement;
				_lastElement = qName;
			}

			public void endElement(String uri, String localName, String qName) {
				_elementsStack.pop();
				_lastElement = _nextToLastElement;
				try {
					_nextToLastElement = _elementsStack.elementAt(_elementsStack.size() - 2);
				} catch (ArrayIndexOutOfBoundsException e) {
					_nextToLastElement = "";
				}
				if (qName == "Base") {
					new Repository(_currentRepositoryIdentifier, _currentRepositoryName);
					Repository currentRepository = new Repository(_currentRepositoryIdentifier,
							_currentRepositoryName);
					_currentRepositoryIdentifier = "";
					_currentRepositoryName = "";
					repositoriesList.add(currentRepository);
				}
			}

			public void characters(char[] ch, int start, int length) {
				if (_nextToLastElement == "Base") {
					switch (_lastElement) {
					case "Id":
						_currentRepositoryIdentifier = new String(ch, start, length);
						break;
					case "Name":
						_currentRepositoryName = new String(ch, start, length);
						break;
					}
				}
			}
		};
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser = factory.newSAXParser();
		parser.parse(xmlStream, handler);
		return repositoriesList;
	}
}
