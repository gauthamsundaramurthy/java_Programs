package com.PullQueue;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.taskqueue.TaskHandle;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class CreatePullQueue extends HttpServlet {

	private static String processTasks(List<TaskHandle> tasks, Queue q) {
	    String payload;
	    String message;
	    String output;
	    int numberOfDeletedTasks = 0;
	    
	    for (TaskHandle task : tasks) {
	      payload = new String(task.getPayload());
	      output = String.format("Processing: taskName='%s'  payload='%s'", task.getName(), payload);
	      output = String.format("Deleting taskName='%s'", task.getName());
	      
	      q.deleteTask(task);
	      
	      numberOfDeletedTasks++;
	    }
	    if (numberOfDeletedTasks > 0) {
	      message = "Processed and deleted" ;
	    } else {
	      message = "Task Queue has no tasks available for lease.";
	    }
	    return message;
	  }
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
	          throws ServletException, IOException {
	
	String content = request.getParameter("content");	
	String message;
	
	int numberOfTasksToAdd=20;
	int numberOfTasksToLease = 10;
	Queue q = QueueFactory.getQueue("pull-queue");
	   
	for (int i = 0; i < numberOfTasksToAdd; i++) 
	{
        q.add(TaskOptions.Builder.withMethod(TaskOptions.Method.PULL)
            .payload(content.toString()));
    }
	
	List<TaskHandle> tasks = q.leaseTasks(3600, TimeUnit.SECONDS, numberOfTasksToLease);
	message = processTasks(tasks, q);
	
	response.setContentType("text/plain");
	response.setCharacterEncoding("UTF-8");
	
	response.getWriter().print(message);	
	}
}
