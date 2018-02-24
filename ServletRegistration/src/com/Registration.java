package com;

import java.util.HashMap;
import com.SaveUserDetails;

public class Registration {
	protected String userName;
	protected String password;
	protected String confirmPassword;
	protected String email;
   
	Registration(String Username, String password, String confirmPassword, String email)
	{
		this.userName = Username ; 
		this.password = password;
		this.confirmPassword = confirmPassword;
		this.email = email;
	}
	
	public String checkForUserName()
	{
		String status = "valid";
		if(userName.length()==0)
		{
			status = "Username field is empty.";
		}
		return status;
	}
	
	public String checkForUserPassword()
	{
		String status = "valid";
		if(password.length()<=4 || password.length()>15)
		{
			status = "Password should contain minimum of 5 characters & maximum of 15 characters.";
		}
		else if(!password.equals(confirmPassword))
		{
			status="Password field differs from confirm Password field.";
		}
		return status;
	}
	
	public String checkForUserEmail()
	{
		String status = "Invalid email.";
		
		if(email.contains("@") && email.contains("."))
		{
			status= "valid";
		}
		SaveUserDetails saveObj = SaveUserDetails.getInstance();
		HashMap<String,String>  userInfoEmailDetails= saveObj.getHashMap_EmailDetails();
		for (String key : userInfoEmailDetails.keySet() ) {
		    String email_from_hash_map = userInfoEmailDetails.get(key);
		    if (email.equals(email_from_hash_map))
		    {
		    	status= "e-mail already exist.";
		    }
		}
		return status;
	}
	
	public void saveDetails(String userName, String email, String password)
	{
		SaveUserDetails saveObj = SaveUserDetails.getInstance();
		HashMap<String,String>  userInfo= saveObj.getHashMap();
	    userInfo.put(userName,password);
	    
	    HashMap<String,String>  userInfoEmailDetails= saveObj.getHashMap_EmailDetails();
	    userInfoEmailDetails.put(userName,email);
	    
	}
	
}