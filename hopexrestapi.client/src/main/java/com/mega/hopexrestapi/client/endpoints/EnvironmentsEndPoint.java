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
import com.mega.hopexrestapi.client.endpoints.types.Environment;

public class EnvironmentsEndPoint {
	private RestApiServer _restApiServer;

	public EnvironmentsEndPoint(RestApiServer restApiServer) {
		_restApiServer = restApiServer;
	}
	
	public List<Environment> getEnvironments()
			throws RestApiException, ParserConfigurationException, SAXException, IOException {
		RestApiCall call = new RestApiCall("environments");
		RestApiResult response = _restApiServer.callEndPoint(call);
		return parseEnvironments(response.getContent());
	}

	private List<Environment> parseEnvironments(InputStream xmlStream)
			throws ParserConfigurationException, SAXException, IOException {
		List<Environment> environmentsList = new ArrayList<Environment>();
		DefaultHandler handler = new DefaultHandler() {
			Stack<String> _elementsStack = new Stack<String>();
			String _lastElement = "";
			String _nextToLastElement = "";
			String _currentEnvironmentIdentifier = "";
			String _currentEnvironmentName = "";

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
				if (qName == "Environment") {
					new Environment(_currentEnvironmentIdentifier, _currentEnvironmentName);
					Environment currentEnvironment = new Environment(
							_currentEnvironmentIdentifier, _currentEnvironmentName);
					_currentEnvironmentIdentifier = "";
					_currentEnvironmentName = "";
					environmentsList.add(currentEnvironment);
				}
			}

			public void characters(char[] ch, int start, int length) {
				if (_nextToLastElement == "Environment") {
					switch (_lastElement) {
					case "Id":
						_currentEnvironmentIdentifier = new String(ch, start, length);
						break;
					case "Name":
						_currentEnvironmentName = new String(ch, start, length);
						break;
					}
				}
			}
		};
		SAXParserFactory factory = SAXParserFactory.newInstance();
		SAXParser parser = factory.newSAXParser();
		parser.parse(xmlStream, handler);
		return environmentsList;
	}
}
