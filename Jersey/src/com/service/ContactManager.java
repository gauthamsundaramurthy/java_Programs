package com.service;

import java.util.ArrayList;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.handler.TaskHandler;
import com.model.User;

@Path("/contactManager")
public class ContactManager {

	@SuppressWarnings("unchecked")
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject registrationService(User user){
		String registrationStatus;
		if(user.getUserName().isEmpty() ||  user.getPrimaryEmail().isEmpty() || ( user.getPhoneNumber().getMobileNumber().isEmpty() && user.getPhoneNumber().getResidenceNumber().isEmpty() && user.getPhoneNumber().getOfficeNumber().isEmpty() ) || user.getCloudType().isEmpty() )
		{
			registrationStatus = "Invalid request";
		}
		else
		{
			TaskHandler taskHandler = new TaskHandler();
			registrationStatus = taskHandler.registrationHandler(user);
		}
		
		JSONObject response = new JSONObject();
		response.put("message",registrationStatus);
		return response;	
	}
	
	@SuppressWarnings("unchecked")
	@DELETE
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject deleteService(User user)
	{
		String deleteStatus;
		if(user.getPrimaryEmail().isEmpty())
		{
			deleteStatus = "Invalid Delete request";
		}
		else
		{	
			TaskHandler taskHandler = new TaskHandler();
			deleteStatus = taskHandler.DeleteUserRecord(user);
			System.out.println(deleteStatus);
		}
		JSONObject response = new JSONObject();
		response.put("message",deleteStatus);
		return response;
	}
	
	@SuppressWarnings("unchecked")
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject retrieveService(@QueryParam("filterOption") String filterOption, @QueryParam("primaryEmail") String primaryEmail , @QueryParam("cloudType") String cloudType)
	{
		ArrayList<JSONObject> result = new ArrayList<JSONObject>();
		
		JSONObject response = new JSONObject();
		
		if(filterOption.equals("primaryEmail") && primaryEmail!=null && (!primaryEmail.isEmpty()) )
		{	
			TaskHandler taskHandler = new TaskHandler();
			result = taskHandler.filterOnprimaryEmail(primaryEmail);
			response.put("message",result);
		}
		else if(filterOption.equals("cloudType") && cloudType!=null && (!cloudType.isEmpty()))
		{
			TaskHandler taskHandler = new TaskHandler();
			result = taskHandler.filterOnCloudType(cloudType);
			response.put("message",result);
		}
		else
		{
			response.put("message","Invalid request");
		}
		return response;
	}
	
	
	@SuppressWarnings("unchecked")
	@PUT
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject updateService(final String jsonInput)
	{  
		JSONObject req_json_obj;
		String updateStatus="Invalid request";  
		try {
			req_json_obj = (JSONObject)  new JSONParser().parse(jsonInput);
			TaskHandler taskHandler = new TaskHandler();
			
			updateStatus = taskHandler.updateHandlr(req_json_obj);
			
		} catch (ParseException e) {
			updateStatus = "Invalid request";
			e.printStackTrace();
		}
		
		JSONObject response = new JSONObject();
		response.put("message",updateStatus);
		return response;
		
	}
	
}

