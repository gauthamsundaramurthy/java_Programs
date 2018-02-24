package com.memcache;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.NamespaceManager;
import com.google.appengine.api.memcache.AsyncMemcacheService;
import com.google.appengine.api.memcache.ErrorHandlers;
import com.google.appengine.api.memcache.MemcacheServiceFactory;

public class AsyncMemcache extends HttpServlet {

	public void doGet(HttpServletRequest request, HttpServletResponse response) 
		      throws IOException {
		
		AsyncMemcacheService asyncCache = MemcacheServiceFactory.getAsyncMemcacheService();
		
		asyncCache.setErrorHandler(ErrorHandlers.getConsistentLogAndContinue(Level.INFO));
		
		String key="Gautham";
		String hobby="Cricket";
		String result;
		
		asyncCache.put(key,hobby);
		Future<Object> future_value = asyncCache.get(key);
		
		try {
			result = (String) future_value.get();
			System.out.println("async method of fetching..." + result);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		
		response.setContentType("text/plain");
 		response.setCharacterEncoding("UTF-8");

 		response.getWriter().print("Adding data using  AsyncMemCacheService");
		
	}
}
