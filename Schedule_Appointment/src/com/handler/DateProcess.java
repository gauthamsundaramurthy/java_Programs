package com.handler;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateProcess {
	Date meetingStartDate,meetingEndDate;
	DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
	
	
	public void addDate(UTCTime utcTime,String repeatMode)
	{
		try {
			meetingStartDate = sdf.parse(utcTime.getStartDate());
			meetingEndDate = sdf.parse(utcTime.getEndDate());
			
			Calendar c = Calendar.getInstance();
			Calendar c1 = Calendar.getInstance();
		    
			c.setTime(meetingStartDate);
		    c1.setTime(meetingEndDate);
		    
		    // manipulate date
	        if(repeatMode.equals("weekly"))
	        {
	        	c.add(Calendar.DATE, 7); 
	        	c1.add(Calendar.DATE, 7);
	        }
	        if(repeatMode.equals("monthly"))
	        {
	        	c.add(Calendar.MONTH,1);
	        	c1.add(Calendar.MONTH, 1);
	        }
	        
	        // convert to date
	        
	        Date addedStartDate = c.getTime();
	        Date addedEndDate = c1.getTime();
	        
	        utcTime.setDateTime(dateFormat.format(addedStartDate),utcTime.getStartTime(),dateFormat.format(addedEndDate),utcTime.getEndTime());
	        
			
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	public void minusDate(UTCTime utcTime,String repeatMode)
	{
		try {
			meetingStartDate = sdf.parse(utcTime.getStartDate());
			meetingEndDate = sdf.parse(utcTime.getEndDate());
			
			Calendar c = Calendar.getInstance();
			Calendar c1 = Calendar.getInstance();
		    
			c.setTime(meetingStartDate);
		    c1.setTime(meetingEndDate);
		    
		    // manipulate date
	        if(repeatMode.equals("weekly"))
	        {
	        	c.add(Calendar.DATE, -7); 
	        	c1.add(Calendar.DATE, -7);
	        }
	        if(repeatMode.equals("monthly"))
	        {
	        	c.add(Calendar.MONTH,-1);
	        	c1.add(Calendar.MONTH, -1);
	        }
	        
	        // convert to date
	        
	        Date addedStartDate = c.getTime();
	        Date addedEndDate = c1.getTime();
	        
	        utcTime.setDateTime(dateFormat.format(addedStartDate),utcTime.getStartTime(),dateFormat.format(addedEndDate),utcTime.getEndTime());
	        
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
	}
}
