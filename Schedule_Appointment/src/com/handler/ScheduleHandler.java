package com.handler;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import com.model.Employee;
import com.model.MeetingInfo;
import com.handler.DateProcess;
import com.response.ResponseBuilder;

public class ScheduleHandler {

	private String status = "available";
	private int statusCode = 200;
	
	DateProcess processDate = new DateProcess();
	
	
	public void insertEmployeeData(Connection conn,ResponseBuilder respObj,Employee emp)
	{
		try {
			Statement stmt = conn.createStatement();
			stmt.executeUpdate("INSERT INTO employee(empId,firstName,lastName,emailId,company,team,position,authority) VALUE ('"+emp.getEmpId()+"','"+emp.getFirstName()+"','"+emp.getLastName()+"','"+emp.getEmailId()+"','"+emp.getCompany()+"','"+emp.getTeam()+"','"+emp.getPosition()+"','"+emp.getAuthority()+"')");
			respObj.setResponse(200, "Registered successfully");
			
		} catch (SQLException e) {
			respObj.setResponse(500, "Exception thrown while accessing database");
			e.printStackTrace();
		}
		
	}
	
	public UTCTime convertToUTC(MeetingInfo meeting)
	{
	   String date,month,hour,minute,second, startDate,startTime, endDate, endTime;	
	   LocalDateTime localtDateAndTime;
	   ZonedDateTime dateAndTime;
		
	   OffsetDateTime odt = OffsetDateTime.now ( ZoneId.systemDefault ()) ;
	   ZoneOffset zoneOffset = odt.getOffset ();
	   ZoneId zoneId = ZoneId.ofOffset("UTC", zoneOffset);
	
	   DateTimeFormatter formatters = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
	   
	   // meeting startDate & startTime to UTC standard
	   
	   String string = meeting.getStartDate() + " " + meeting.getStartTime();
	   localtDateAndTime = LocalDateTime.parse(string, formatters);
	   dateAndTime = ZonedDateTime.of(localtDateAndTime, zoneId );
	   
	   ZonedDateTime startDateAndTime = dateAndTime.withZoneSameInstant(ZoneOffset.UTC);
	   month = (startDateAndTime.getMonthValue() < 10 ? "0" : "") + startDateAndTime.getMonthValue();
	   date = (startDateAndTime.getDayOfMonth() < 10 ? "0" : "") + startDateAndTime.getDayOfMonth();
	   hour = (startDateAndTime.getHour() < 10 ? "0" : "") + startDateAndTime.getHour();
	   minute = (startDateAndTime.getMinute() < 10 ? "0" : "") + startDateAndTime.getMinute();
	   second = (startDateAndTime.getSecond() < 10 ? "0" : "") + startDateAndTime.getSecond();
	   
	   startDate = startDateAndTime.getYear()+"-"+month+"-"+ date;
	   startTime = hour+":"+minute+":"+second;
	   
	   // meeting endDate & endTime to UTC standard 
	   
	   string = meeting.getEndDate() + " " + meeting.getEndTime();
	   localtDateAndTime = LocalDateTime.parse(string, formatters);
	   dateAndTime = ZonedDateTime.of(localtDateAndTime, zoneId );
	   
	   ZonedDateTime endDateAndTime = dateAndTime.withZoneSameInstant(ZoneOffset.UTC);
	   month = (endDateAndTime.getMonthValue() < 10 ? "0" : "") + endDateAndTime.getMonthValue();
	   date = (endDateAndTime.getDayOfMonth() < 10 ? "0" : "") + endDateAndTime.getDayOfMonth();
	   hour = (endDateAndTime.getHour() < 10 ? "0" : "") + endDateAndTime.getHour();
	   minute = (endDateAndTime.getMinute() < 10 ? "0" : "") + endDateAndTime.getMinute();
	   second = (endDateAndTime.getSecond() < 10 ? "0" : "") + endDateAndTime.getSecond();
	   
	   endDate = endDateAndTime.getYear()+"-"+month+"-"+date;
	   endTime = hour+":"+minute+":"+second;
	 	  
       UTCTime utc = new UTCTime(startDate,startTime,endDate,endTime); 
	   
	  return utc;
	}
	
	public int insertMeetingData(Connection conn,MeetingInfo meeting,UTCTime utcTime)
	{
		int meetingId = 0;
		try {
			Statement stmt = conn.createStatement();
			stmt.executeUpdate("INSERT INTO meetingInfo(name,description,startDate,startTime,endDate,endTime,venue,organiser) VALUE ('"+meeting.getName()+"','"+meeting.getDescription()+"','"+utcTime.getStartDate()+"','"+utcTime.getStartTime()+"','"+utcTime.getEndDate()+"','"+ utcTime.getEndTime()+"','"+meeting.getVenue()+"','"+meeting.getOrganiser()+"')");
	
			ResultSet rs = stmt.executeQuery("SELECT meetingId FROM meetingInfo where startDate = '" + utcTime.getStartDate() +"' AND startTime ='"+ utcTime.getStartTime() +"'");
			if(rs.next())
			{
				meetingId = rs.getInt("meetingId");
				return meetingId;
			}
		} catch (SQLException e) {
			this.statusCode = 500;
			this.status = "Exception thrown while accessing database";
			e.printStackTrace();
		}
		
		return meetingId;
	}
	
	public boolean isOrganiser(Connection conn, String empIdOfManager)
	{
		boolean isEligible = false;
		try {
			Statement stmt = conn.createStatement();
			ResultSet rs = null;
			rs = stmt.executeQuery("SELECT authority FROM employee where empId = '" + empIdOfManager +"'");
			if(rs.next())
			{
				if(rs.getString("authority").equals("yes") )
				{
					isEligible = true;
				}
				else
				{
					this.status = "Organiser do not have authority to schedule an event";
				}
			}
			else
			{
				this.status = "Invalid Organiser Id";
			}
			
		} catch (SQLException e) {
			this.statusCode = 500;
			this.status = "Exception thrown while accessing database";
			e.printStackTrace();
		}
		
		return isEligible;
	}
	
	public void appointmentScheduler(Connection conn,ResponseBuilder respObj,MeetingInfo meeting)
	{
		if( isOrganiser(conn,meeting.getOrganiser()) )
		{
			UTCTime utcTime = convertToUTC(meeting);
			
			String timeDiff = timeDifference(utcTime.getStartDate(),utcTime.getStartTime(),utcTime.getEndDate(),utcTime.getEndTime());
			
			if(timeDiff.equals("greater"))
			{
				this.statusCode = 400;
				this.status = "start time is greater than end Time";
			}
			else
			{
				checkAppointmentCollision(conn,meeting,utcTime);
			
				if(this.status.equals("available"))
				{
					try {
						if(meeting.getRepeatMode().equals("none"))
						{
							int meetingId = insertMeetingData(conn,meeting,utcTime);
							if (meetingId!=0)
							{  
								// insert organizer meeting schedule
								Statement stmt = conn.createStatement();
								stmt.executeUpdate("INSERT INTO employeeSchedule(empId,meetingId) VALUE ('"+ meeting.getOrganiser() +"','"+ meetingId + "')");
								
								// insert employee meeting schedule
								for(String empId : meeting.getGuest())
								{
									stmt = conn.createStatement();
									stmt.executeUpdate("INSERT INTO employeeSchedule(empId,meetingId) VALUE ('"+ empId +"','"+ meetingId + "')");
									this.status = "Appointment is scheduled successfully"; 
								}
							}
						}
						if(meeting.getRepeatMode().equals("weekly"))
						{
							int count = 1;
							while(count<=4)
							{
								int meetingId = insertMeetingData(conn,meeting,utcTime);
								if (meetingId!=0)
								{  
									// insert organizer meeting schedule
									Statement stmt = conn.createStatement();
									stmt.executeUpdate("INSERT INTO employeeSchedule(empId,meetingId) VALUE ('"+ meeting.getOrganiser() +"','"+ meetingId + "')");
									
									//insert employee meeting schedule
									for(String empId : meeting.getGuest())
									{
										stmt = conn.createStatement();
										stmt.executeUpdate("INSERT INTO employeeSchedule(empId,meetingId) VALUE ('"+ empId +"','"+ meetingId + "')");
										this.status = "Appointment is scheduled successfully"; 
									}
								}
								processDate.addDate(utcTime,meeting.getRepeatMode() );
								count++;
							}
							count=1;
							while(count<=3)
							{
								processDate.minusDate(utcTime,meeting.getRepeatMode() );
								count++;
							}
						}
			
					} catch (SQLException e) {
						this.statusCode = 500;
						this.status = "Exception thrown while accessing database";
						e.printStackTrace();
					}
				}
			}
		}
		
		respObj.setResponse(this.statusCode, this.status);
	}

	public void checkAppointmentCollision(Connection conn,MeetingInfo meeting,UTCTime utcTime)
	{	
		ResultSet resultset = null;
		
		try {
			
			String employeeIds = "'" + meeting.getOrganiser() + "'";
			for(String empId: meeting.getGuest())
			{
				employeeIds = employeeIds + ",'" + empId + "'"; 
			}
			
			Statement stmt = conn.createStatement();
			resultset = stmt.executeQuery("SELECT * from employeeSchedule INNER JOIN meetingInfo ON employeeSchedule.meetingId = meetingInfo.meetingId where employeeSchedule.empId  IN (" + employeeIds + ")" );
			if(resultset.next())
			{
				resultset.beforeFirst();
				while(resultset.next())
				{
					if(meeting.getRepeatMode().equals("none") )
					{
						checkForDateCollision(utcTime,resultset.getString("startDate"),resultset.getString("startTime"),resultset.getString("endDate"),resultset.getString("endTime") );
					}
					if(meeting.getRepeatMode().equals("weekly") )
					{
						int count = 1;
						while(count<=3)
						{
							checkForDateCollision(utcTime,resultset.getString("startDate"),resultset.getString("startTime"),resultset.getString("endDate"),resultset.getString("endTime") );
							processDate.addDate(utcTime,meeting.getRepeatMode() );
							count++;
						}
						checkForDateCollision(utcTime,resultset.getString("startDate"),resultset.getString("startTime"),resultset.getString("endDate"),resultset.getString("endTime") );
						count=1;
						while(count<=3)
						{
							processDate.minusDate(utcTime,meeting.getRepeatMode() );
							count++;
						}
					}
						
				}
			}
			
					System.out.println("waiting for result. ");
			
		} catch (SQLException e) {
			this.statusCode = 500;
			this.status = "Exception thrown while accessing database";
			e.printStackTrace();
		}
		
	}
	
	
	public void checkForDateCollision(UTCTime utcTime, String startDate, String startTime, String endDate, String endTime)
	{
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date meetingStartDate,startDateFromDB , meetingEndDate , endDateFromDB;
		try {
			meetingStartDate = sdf.parse(utcTime.getStartDate());
			startDateFromDB = sdf.parse(startDate);
			meetingEndDate = sdf.parse(utcTime.getEndDate());
			endDateFromDB = sdf.parse(endDate);
			
			String conflictDate = "both";
			//System.out.println(".................");
	        if ( (meetingStartDate.compareTo(endDateFromDB) > 0) || (meetingEndDate.compareTo(startDateFromDB) < 0)) {
	        	//System.out.println("slot is free to book");
	        } 
	        else if ( ( (meetingStartDate.compareTo(startDateFromDB) > 0) && (meetingStartDate.compareTo(endDateFromDB) < 0)) || ( (meetingEndDate.compareTo(startDateFromDB) > 0) && (meetingEndDate.compareTo(endDateFromDB) < 0)) ) {
	            this.status = "Appointment cannot be scheduled";
	        }
	        else if((meetingStartDate.compareTo(startDateFromDB) < 0) && (meetingEndDate.compareTo(endDateFromDB) > 0) )
	        {	this.status ="Appointment cannot be scheduled";
	        }
	        else if ( (meetingStartDate.compareTo(startDateFromDB) == 0) && (meetingEndDate.compareTo(endDateFromDB) == 0) ) {
	            //System.out.println("may collide because of same date");
	            checkForTimeCollision(conflictDate,utcTime,startDate,startTime,endDate,endTime);
	        }
	        else if ( (meetingStartDate.compareTo(startDateFromDB) == 0) || (meetingStartDate.compareTo(endDateFromDB) == 0)  ) {
	            //System.out.println("may collide because of start date");
	        	conflictDate = "meetingStartDate";
	            checkForTimeCollision(conflictDate,utcTime,startDate,startTime,endDate,endTime);
	        }
	        else if((meetingEndDate.compareTo(startDateFromDB) == 0) || (meetingEndDate.compareTo(endDateFromDB) == 0))
	        {
	        	conflictDate = "meetingEndDate";
	            checkForTimeCollision(conflictDate,utcTime,startDate,startTime,endDate,endTime);
	        
	        	//System.out.println("may collide because of end date");
	        }
	        else {
	        	this.status = "Appointment cannot be scheduled";
	        	//System.out.println("logic error");
	        } 
	        
		} catch (ParseException e) {
			this.statusCode = 500;
			this.status = "Exception thrown while accessing database";
			e.printStackTrace();
		}
	}
	
	public void checkForTimeCollision(String conflictDate, UTCTime utcTime, String startDate, String startTime, String endDate, String endTime)
	{
			String timeDiff ;
			if(conflictDate.equals("meetingEndDate"))
			{
				timeDiff = timeDifference(utcTime.getEndDate(),utcTime.getEndTime(),startDate,startTime);
				if (timeDiff.equals("lesser") || timeDiff.equals("equal"))
				{
					//System.out.println("yes, slot is free....");
				}
				else
				{
					this.status = "Appointment cannot be scheduled";
					//System.out.println("no, slot is not available.... ");
				}
			}
			else if(conflictDate.equals("meetingStartDate"))
			{
				timeDiff = timeDifference(endDate,endTime,utcTime.getStartDate(),utcTime.getStartTime());
				if (timeDiff.equals("lesser") || timeDiff.equals("equal"))
				{
					//System.out.println("yes, slot is free....");
				}
				else 
				{
					this.status="Appointment cannot be scheduled";
					//System.out.println("no, slot is not available.... ");
				}
			}
			else
			{
				
				timeDiff = timeDifference(utcTime.getStartDate(),utcTime.getEndTime(),startDate,startTime);
				String timeDiff1 = timeDifference(startDate,endTime,utcTime.getStartDate(),utcTime.getStartTime());
				
				
				if(timeDiff.equals("equal") || timeDiff.equals("lesser"))
				{
					//System.out.println("yes, slot is available");
				}
				else if (timeDiff1.equals("equal") || timeDiff1.equals("lesser"))
				{
					//System.out.println("yes, slot is available");
				}
				else
				{
					this.status="Appointment cannot be scheduled";
					//System.out.println("no, slot is not available");
				}
				
			}
	}
	
	public String timeDifference(String date1,String time1, String date2, String time2)
	{
		String startTimeStr = date1 + " " + time1;
		String endTimeStr = date2 + " " + time2;
		String status;
		DateTimeFormatter formatter = DateTimeFormatter
				.ofPattern("yyyy-MM-dd HH:mm:ss");
		
		LocalDateTime sTime = LocalDateTime.parse(startTimeStr,
				formatter);
		LocalDateTime eTime = LocalDateTime.parse(endTimeStr, formatter);
		
		Duration duration = Duration.between(sTime, eTime);
		
		if (duration.getSeconds() == 0)
		{
			status="equal";
		   // System.out.println("\n Both Start time and End Time are equal");
		}
		else if (duration.getSeconds() > 0)
		{
			status = "lesser";
			//System.out.println("\n" + time1 + " is less than " + time2);
		}
		else
		{
			status = "greater";
			//System.out.println("\n" + time1 + " is greater than " + time2);
		}	
		return status;
	}
	
}