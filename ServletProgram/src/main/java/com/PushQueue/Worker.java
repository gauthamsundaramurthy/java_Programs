package com.PushQueue;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Worker extends HttpServlet {
	
	  protected void doPost(HttpServletRequest request, HttpServletResponse response)
	          throws ServletException, IOException {
	    
		String user_name = request.getParameter("name");

	    System.out.println("From worker, user name is  " + user_name);
	    
	    response.setContentType("text/plain");
		response.setCharacterEncoding("UTF-8");
		
		response.getWriter().print("Worker implementation !!!");

	  }
}



