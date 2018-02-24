package com.memcache;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.NamespaceManager;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

public class Memcache extends HttpServlet{

	public void doGet(HttpServletRequest request, HttpServletResponse response) 
		      throws IOException {
		
		String name="Syed";
		String hobby="Painting";
		
		
		MemcacheService syncCache = MemcacheServiceFactory.getMemcacheService();
		syncCache.put(name, hobby);

		System.out.println("Saving to memcache using low-level API");
		
		String result = (String) syncCache.get(name);
		System.out.println("After fetching " + result);

		
		response.setContentType("text/plain");
 		response.setCharacterEncoding("UTF-8");

 		response.getWriter().print("Adding data using Synchronous MemCacheService");
	}
}
