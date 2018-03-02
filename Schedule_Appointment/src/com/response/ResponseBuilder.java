package com.response;

import org.json.simple.JSONObject;

public class ResponseBuilder {

	int statusCode;
	String message;
	
	JSONObject response = new JSONObject();
	
	@SuppressWarnings("unchecked")
	public JSONObject generateStringResponse()
	{
		response.put("statusCode", this.statusCode);
		response.put("message", this.message);
		return response;
	}
	
	public void setResponse(int statusCode, String msg)
	{
		this.statusCode = statusCode;
		this.message = msg;
	}
	
	
	
}
