package com.response;

import java.util.ArrayList;

import org.json.simple.JSONObject;

public class ResponseBuilder {

	int statusCode;
	String message;
	ArrayList<String> employee_Collision = new ArrayList<String>();
	
	JSONObject response = new JSONObject();
	
	@SuppressWarnings("unchecked")
	public JSONObject generateStringResponse()
	{
		response.put("statusCode", this.statusCode);
		response.put("message", this.message);
		return response;
	}
	
	@SuppressWarnings("unchecked")
	public JSONObject generateScheduleResponse()
	{
		response.put("statusCode", this.statusCode);
		response.put("message", this.message);
		response.put("employee_clash", this.employee_Collision);
		return response;
	}
	
	public void setResponse(int statusCode, String msg)
	{
		this.statusCode = statusCode;
		this.message = msg;
	}
	
	public void setScheduleResponse(int statusCode, String msg,ArrayList<String> empCollision)
	{
		this.statusCode = statusCode;
		this.message = msg;
		this.employee_Collision = empCollision;
	}
	
	
	
}
