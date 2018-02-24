package com;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Register extends HttpServlet{
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		   
		StringBuilder sb = new StringBuilder();
	    BufferedReader br = request.getReader();
	    String str;
	    
	    while( (str = br.readLine()) != null ){
	        sb.append(str);
	    }
	    
	    String json_string = sb.toString();
	    
	    try {
			JSONObject jObj = (JSONObject) new JSONParser().parse(json_string);
			response.setContentType("application/json");

			   String userName = (String) jObj.get("user-name");
			   String password = (String) jObj.get("password");
			   String confirmPassword = (String) jObj.get("confirm-password");
			   String email = (String) jObj.get("e-mail");
			   String message;
			   int status=400;
			   
			    Registration reg = new Registration(userName, password,confirmPassword,email);
				String userNameStatus = reg.checkForUserName();
				String passwordStatus = reg.checkForUserPassword();
				String emailStatus = reg.checkForUserEmail();
				
				if(userNameStatus=="valid" && passwordStatus=="valid" && emailStatus=="valid")
				{
					status=200;
					reg.saveDetails(userName,email,password);
					message = "Registration successfull !!!";
				}
				
				else if(userNameStatus!= "valid")
				{
					message = userNameStatus;
				}
				else if(passwordStatus!="valid")
				{
					message = passwordStatus;
				}
				else
				{
					message = emailStatus;
				}		   
				
			JSONObject response_jObj = new JSONObject();
			response_jObj.put("status",status);
			response_jObj.put("message",message);
			
			// Actual logic goes here.
			PrintWriter out = response.getWriter();
			System.out.println(response_jObj);
			out.println( response_jObj);
						
		} catch (ParseException e) {
			e.printStackTrace();
		}

	   
		   	   	      
	   }

}
