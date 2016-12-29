package com.mega.hopexrestapi.client.endpoints.types;

public class Repository {
	private String _identifier = "";
	private String _name = "";

	public Repository(String identifier, String name) {
		_identifier = identifier;
		_name = name;
	}

	public String getIdentifier() {
		return _identifier;
	}

	public String getName() {
		return _name;
	}

}
