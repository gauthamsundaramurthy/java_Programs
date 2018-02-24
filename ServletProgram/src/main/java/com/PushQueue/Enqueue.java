package com.PushQueue;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;


@SuppressWarnings("serial")
public class Enqueue extends HttpServlet {
	  
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
	          throws ServletException, IOException {
	
		String user_name = request.getParameter("name");

	    // Add the task to the default queue.
	    Queue queue = QueueFactory.getQueue("test-queue");
	   
	    queue.add(TaskOptions.Builder.withUrl("/worker").param("name", user_name));
		
	    response.sendRedirect("/");
	  }
	}
