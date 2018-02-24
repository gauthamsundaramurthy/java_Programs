package com.memcache;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.cache.Cache;
import javax.cache.CacheException;
import javax.cache.CacheFactory;
import javax.cache.CacheStatistics;

import javax.cache.CacheManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.memcache.stdimpl.GCacheFactory;


public class Jcache extends HttpServlet{
	
	public void doGet(HttpServletRequest request, HttpServletResponse response) 
		      throws IOException {
		
		
		Cache cache;
		try {
		
			String name="Gautham";
			String hobby="chess";
            
			
			CacheFactory cacheFactory = CacheManager.getInstance().getCacheFactory();
            cache = cacheFactory.createCache(Collections.emptyMap());
			
			cache.put(name,hobby);
            
			System.out.println("Storing into cache..");
            //System.out.println(cache.size());
            
            String result = (String) cache.get(name);
            
            System.out.println("After fetching from cache.." + result);
            
            CacheStatistics stats = cache.getCacheStatistics();
	        int hits = stats.getCacheHits();
	        int misses = stats.getCacheMisses();
			
            System.out.println("Cache statistics....");
            System.out.println(hits + "  " + misses);
            if(cache.containsKey(name))
            {
            	System.out.println("Key-Value pair is present in Cache..");
            }
            else
            {
            	System.out.println("Key does not exist in Cache...");
            }
            
            response.setContentType("text/plain");
    		response.setCharacterEncoding("UTF-8");

    		response.getWriter().print("Adding memcache..");

		} catch (CacheException e) {

			System.out.println("Cache cannot be created !!!");
            System.out.println(e);

            response.setContentType("text/plain");
    		response.setCharacterEncoding("UTF-8");

    		response.getWriter().print("Adding memcache..");

        }


	}
		
}
