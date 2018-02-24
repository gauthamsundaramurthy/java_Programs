package com.memcache;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.NamespaceManager;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

public class NameSpaceSample extends HttpServlet{

	public void doGet(HttpServletRequest request, HttpServletResponse response) 
		      throws IOException {
		
		String name="Kishore";
		String hobby="Dancing";
		
		
		MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
		syncCache.put(name, hobby);

		System.out.println("Saving to memcache using low-level API");
		
		String result = (String) syncCache.get(name);
		System.out.println("After fetching " + result);

		//Saving entries to "abc" Namespace 
		
		String oldNamespace = NamespaceManager.get();
		System.out.println("Default namespace: " + oldNamespace);
		NamespaceManager.set("abc");
		
		String userName="Vishy";
		String userHobby="Chess";
		
		try {
			
			syncCache.put(userName, userHobby);  // stores value in namespace “abc”
			String res = (String) syncCache.get(userName);
			System.out.println("In 'abc' namespace..." + res);
		} finally {
		  NamespaceManager.set(oldNamespace);
		  String res = (String) syncCache.get(userName);
		  System.out.println("In default namespace " + res);
		  
		  String results = (String) syncCache.get(name);
		  System.out.println("In default namespace " + results);
		  
		}
		
		response.setContentType("text/plain");
 		response.setCharacterEncoding("UTF-8");

 		response.getWriter().print("Testing NameSpace..");
	}
}
