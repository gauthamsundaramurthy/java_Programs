package com;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Login extends HttpServlet{
	
	public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		   
		   StringBuilder sb = new StringBuilder();
		   BufferedReader br = request.getReader();
		   String str;
		   while( (str = br.readLine()) != null)
		   {
			   sb.append(str);
		   }
		   String json_string = sb.toString();
		   
		   try
		   {
			   JSONObject req_json_obj = (JSONObject)  new JSONParser().parse(json_string);
			   String userName = (String) req_json_obj.get("user-name");
			   String password = (String) req_json_obj.get("password");
			 
			   String message;
			   int status=400;
			   
			   SaveUserDetails obj = SaveUserDetails.getInstance();
			   HashMap<String,String> userInfo= obj.getHashMap();
			   
			   
			   if (userInfo.containsKey(userName) )
				{
					if(password.trim().equals(userInfo.get(userName)) )
					{
						status= 200;
						message= "Login successfull !!!! ";
					}
					else
					{
						message= "Invalid password. Try again." ;
					}
				}
				else
				{
					message = "User name does not exist.";
				}
				
			   //Set output format to JSON
			   response.setContentType("application/json");
			   
			   JSONObject response_json_obj = new JSONObject();
			   response_json_obj.put("status", status);
			   response_json_obj.put("message", message);
			   
			   // Actual logic goes here.
				PrintWriter out = response.getWriter();
				System.out.println(out);
				out.println(response_json_obj);
		      

		   }
		   catch(ParseException e)
		   {
			   
		   }
		   
		   	   }

}
