package com.handlr;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;

import com.model.User;

public class validateRegistration {

	public String validate_userName(User user)
	{
		String userName = user.getUserName();
		String status = "valid";
		
		if(userName.length() == 0 || userName.length() > 25)
		{
			status = "invalid";
		}
		return status;	
	}
	
	public String checkDuplicateId(String primaryEmail)
	{
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		String status="valid";
		Query q = new Query("userInfo");
	    PreparedQuery pq = datastore.prepare(q);
	    
	    for(Entity entity : pq.asIterable())
	    {
	    	String datastoreEmail = (String) entity.getProperty("primaryEmail");
	    	if(primaryEmail.equals(datastoreEmail))
	    	{
	    		status =  "invalid";
	    	}
	    }
	    return status;
	}
	
	public String validate_primaryEmailId(User user)
	{
		String emailId = user.getPrimaryEmail();
		
		String status = "invalid";
		
		if (emailId.contains("@") && ( emailId.contains(".com") || emailId.contains(".co.in")|| emailId.contains(".org")) )
		{
			status = checkDuplicateId(emailId);
		}
		return status;
	}
	
	public String validate_alternativeEmailId(User user)
	{ 
		String status = "invalid";
		if( user.getAlternativeEmail().isEmpty() )
		{
			status = "valid";
		}
		else
		{
			String emailId = user.getAlternativeEmail();
			if (emailId.contains("@") && ( emailId.contains(".com") || emailId.contains(".co.in")|| emailId.contains(".org")) )
			{
				status = checkDuplicateId(emailId);
			}	
		}
		return status;
			
	}
		
	public String validate_cloudStorage(User user)
	{
		String storageType = user.getCloudType();
		String status = "invalid";
		if (storageType.equals("google")|| storageType.equals("yahoo") || storageType.equals("hot mail") )
		{
			status ="valid";
		}
		return status;
	}
	
	public String validateUserInfo(User user)
	{
		String userNameStatus = validate_userName(user);
		String primaryEmailStatus = validate_primaryEmailId(user);
		String alternativeEmailStatus = validate_alternativeEmailId(user);
		String cloudStorageStatus = validate_cloudStorage(user);
		
		if(userNameStatus.equals("valid") &&   primaryEmailStatus.equals("valid") && alternativeEmailStatus.equals("valid") && cloudStorageStatus.equals("valid") )
		{
			return "valid";
		}
		else if (userNameStatus == "invalid")
		{
			return "Invalid user name";
		}
		else if (primaryEmailStatus == "invalid" || alternativeEmailStatus == "invalid" )
		{
			return "Please give a valid email Id";
		}
		else
		{
			return "Invalid cloud Storage";
		}
				
	}
}

