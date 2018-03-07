package com.service;


import javax.ws.rs.Path;

import java.sql.Connection;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.POST;

import org.json.simple.JSONObject;

import com.model.Connector;
import com.model.Employee;
import com.model.MeetingInfo;
import com.handler.ScheduleHandler;
import com.response.ResponseBuilder;

@Path("/")
public class ScheduleAppointment {

	@POST
	@Path("/employeeRegister")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject registerEmployee(Employee emp)
	{
		ResponseBuilder respObj = new ResponseBuilder();
		
		if( emp.getFirstName().isEmpty() || emp.getLastName().isEmpty() || emp.getCompany().isEmpty() || emp.getPosition().isEmpty() || emp.getAuthority().isEmpty() )	
		{
			respObj.setResponse(400, "Invalid JSON");
		}
		else
		{	
			Connector connector = new Connector("schedule_meeting","root","password");
			Connection conn = connector.getConnector();
			
			ScheduleHandler handler = new ScheduleHandler();
			
			handler.insertEmployeeData(conn,respObj,emp);
		}
		
		return respObj.generateStringResponse();
	}
	
	
	@POST
	@Path("/scheduleAppointment")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public JSONObject scheduleAppointment(MeetingInfo meeting)
	{
		ResponseBuilder respObj = new ResponseBuilder();
	
		if(meeting.getName().isEmpty() || meeting.getDescription().isEmpty() || meeting.getStartDate().isEmpty() || meeting.getStartTime().isEmpty() || meeting.getEndDate().isEmpty() || meeting.getEndTime().isEmpty() || meeting.getVenue().isEmpty() ||meeting.getOrganiser().isEmpty() || meeting.getGuest().isEmpty() )
		{
			respObj.setResponse(400, "Invalid JSON");
		}
		else
		{
			Connector connector = new Connector("schedule_meeting","root","password");
			Connection conn = connector.getConnector();
			
			ScheduleHandler handler = new ScheduleHandler();
			handler.appointmentScheduler(conn,respObj,meeting);
		}
		return respObj.generateScheduleResponse();
	}
	
}
