package com.PushQueue;


import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.taskqueue.DeferredTask;
import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;


public class DeferSampleServlet extends HttpServlet {

	  static final int DELAY_MS = 5000;
	  
	  public class ExpensiveOperation implements DeferredTask {
	      
		  String my_name;
		  String my_hobby;
		  
		  public ExpensiveOperation(String name,String hobby)
		  {
			my_name = name;
			my_hobby = hobby;
		  }
		  
		  @Override
		  public void run() {
			  System.out.println("User name: " + my_name + ". Hobby : " + my_hobby);
		  }
	  }

	  public void doPost(final HttpServletRequest request,
	      final HttpServletResponse response) throws IOException {
	    
		// Add the task to the default queue.
	    Queue queue = QueueFactory.getDefaultQueue();
	    
	    // Delay 5 seconds to run for demonstration purposes
	    queue.add(TaskOptions.Builder.withPayload(new ExpensiveOperation(request.getParameter("name"),request.getParameter("hobby")))
	        .etaMillis(System.currentTimeMillis() + DELAY_MS));

	    response.setContentType("text/plain");
	    response.getWriter().println("Task is backgrounded on queue!");
	  }
}