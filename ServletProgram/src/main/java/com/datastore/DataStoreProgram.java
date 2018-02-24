package com.datastore;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.rowset.serial.SerialBlob;

import com.google.appengine.api.datastore.Blob;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.EmbeddedEntity;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.PropertyProjection;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.datastore.Query.SortDirection;


public class DataStoreProgram extends HttpServlet{
	
	private List<Entity> getTallestPeople() {
	    DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

	    Query q = new Query("Employee").addSort("height", SortDirection.ASCENDING);

	    PreparedQuery pq = datastore.prepare(q);
	    return pq.asList(FetchOptions.Builder.withLimit(5));
	  }

	
	public void doGet(HttpServletRequest request, HttpServletResponse response) 
		      throws IOException {
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

		// Using batch operations, operating on multiple entities in a single Cloud Datastore call.
		
		Entity emp1 = new Entity("Employee", 101);
		emp1.setProperty("firstName", "Gautham");
		emp1.setProperty("lastName", "Sundar");
		emp1.setProperty("height", 170);
		
		Key k1 = KeyFactory.createKey("Employee","102");
		Entity emp2 = new Entity("Employee","102",k1);
		emp2.setProperty("firstName","Kishore");
		emp2.setProperty("lastName","Devaraj");
		emp2.setProperty("height", 165);
		
		Key k2 = KeyFactory.createKey("Employee","103");
		Entity emp3 = new Entity("Employee","103",k2);
		emp3.setProperty("firstName","Ajay");
		emp3.setProperty("lastName","RadhaKrishnan");
		emp3.setProperty("height", 189);
		
		Key k3 = KeyFactory.createKey("Employee", "104");
		Entity emp4 = new Entity(k3);
		ArrayList<String> hobby_list = new ArrayList<String>();
		hobby_list.add("cricket");
		hobby_list.add("chess");
		hobby_list.add("singing");
		emp4.setProperty("hobby",hobby_list);
		datastore.put(emp4);
		
		List<Entity> employees = Arrays.asList(emp1,emp2,emp3,emp4);
		datastore.put(employees);
		
		
		//Getting an entity
		try
		{
			Key k = KeyFactory.createKey("Employee", 101);
			Entity emp = datastore.get(k);
			System.out.println("Getting entity" + emp);
			
			String name = (String) emp.getProperty("firstName");
            
            Long height = (Long) emp.getProperty("height");
			System.out.println(name + "'s height is " + height);
		}
		catch(EntityNotFoundException e){
			System.out.println(e);
		}
		
		//Getting key for an entity
	    System.out.println("Getting key.." + emp1.getKey());
	    
		
		//ancestor
		Entity emp5 = new Entity("Employee");
		emp5.setProperty("firstName","Rohit");
		emp5.setProperty("lastName","Sharma");
		emp5.setProperty("height", 180);
		
		datastore.put(emp5);

		Entity address = new Entity("Address", "addr", emp4.getKey());
		address.setProperty("street","Gandhi nagar");
		address.setProperty("city","tiruppur");
		address.setProperty("state", "tamil nadu");
		
		datastore.put(address);
		
		// delete a entity
		System.out.println("Deleting entity");
		Key employeeKey = KeyFactory.createKey("Employee", 101);
	    datastore.delete(employeeKey);
		
	    //accessing multi-valued property
	    try
	    {
	    	emp4 = datastore.get(emp4.getKey());
	    	@SuppressWarnings("unchecked")
	    	ArrayList<String> hobbies = (ArrayList<String>) emp4.getProperty("hobby");
	    	System.out.println(hobbies);
	    	
	    }
	    catch(EntityNotFoundException e)
	    {
	    	System.out.println(e);
	    }
	    
	    //Embedded entities
	    Entity embedded_entity = new Entity("Address");
	    EmbeddedEntity embeddedContactInfo = new EmbeddedEntity();

	    embeddedContactInfo.setProperty("homeAddress", "123 Fake St, Made, UP 45678");
	    embeddedContactInfo.setProperty("phoneNumber", "555-555-5555");
	    embeddedContactInfo.setProperty("emailAddress", "test@example.com");

	    embedded_entity.setProperty("contactInfo", embeddedContactInfo);
	    datastore.put(embedded_entity);
	    
	    //setPropertiesFrom
	    Entity setproperties_sample = new Entity("Address");
	    EmbeddedEntity embedded_ContactInfo = new EmbeddedEntity();
	    embedded_ContactInfo.setPropertiesFrom(embedded_entity);
	    setproperties_sample.setProperty("contactInfo",embedded_ContactInfo);
	    datastore.put(setproperties_sample);
	    
	    //Generating keys
	    Key key1 = KeyFactory.createKey("Employee", 101);
	    System.out.println("Generated key: " + key1);

	    //If the key includes a path component, you can use the helper class KeyFactory.Builder to build the path. 
	    
	    Key k =
	    	    new KeyFactory.Builder("Person", "GreatGrandpa")
	    	        .addChild("Person", "Grandpa")
	    	        .addChild("Person", "Dad")
	    	        .addChild("Person", "Me")
	    	        .getKey();
	    
	    System.out.println("Using key builder: " + k );
	    
	    String personKeyStr = KeyFactory.keyToString(k);
	    System.out.println("String representation" + personKeyStr);
	    
	    Key personKey = KeyFactory.stringToKey(personKeyStr);
	    System.out.println("Actual key: " + personKey);
	    
	    //Querying 
	    Filter employeeFilter = new FilterPredicate("firstName",FilterOperator.EQUAL,"Rohit");
	    Query q = new Query("Employee").setFilter(employeeFilter);
		
	    System.out.println(q);
	    PreparedQuery pq = datastore.prepare(q);
	    
	    
	    //asSingleEntity
	    
	    /*
	    Entity result = pq.asSingleEntity();
	    
	    System.out.println("Querying" + result);
	    
	    String lastName = (String) result.getProperty("lastName");
	    System.out.println("asSingleEntity method , lastName is " + lastName);
	    */
	    
	    // Iterating through Query results
	    for(Entity results : pq.asIterable())
	    {
	    	String lName =  (String) results.getProperty("lastName");
	    	System.out.println(results);
	    	System.out.println("Iterating method, lastName is " + lName  );
	    	
	    }
	    
	    //setKeysOnly()
	    System.out.println("setKeysOnly");
	    System.out.println(q.setKeysOnly());
	    
	    //setting a limit for your query
	    System.out.println("setting limit to 5");
	    System.out.println(pq.asList(FetchOptions.Builder.withLimit(5)) );
	    
	    //Querying without filter
	    Query q1 = new Query("Employee");
	    PreparedQuery entire_entity_list = datastore.prepare(q1);
	    
	    System.out.println("Query without kind.");
	    System.out.println(q1);
	    
	    List<Entity> results = entire_entity_list.asList(FetchOptions.Builder.withLimit(100));
	    System.out.println("Overall queries");
	    System.out.println(results);
	    
	    //Projection queries
	    
	    Query projection_query = new Query("Employee");
		projection_query.addProjection(new PropertyProjection("firstName",String.class));
	    PreparedQuery projection = datastore.prepare(projection_query);
	    
	    List<Entity> guests = projection.asList(FetchOptions.Builder.withLimit(5));
	  	  for (Entity guest : guests) {
	  	    String content = (String) guest.getProperty("firstName");
	  	    System.out.println("Using projection method, firstName is " + content  );
	  	  }
	    
	    
	    
	    //ancestor querying
	    
	    Entity tom = new Entity("Person", "Tom");
	    Key tomKey = tom.getKey();
	    datastore.put(tom);

	    Entity weddingPhoto = new Entity("Photo", tomKey);
	    weddingPhoto.setProperty("imageURL", "http://domain.com/some/path/to/wedding_photo.jpg");

	    Entity babyPhoto = new Entity("Photo", tomKey);
	    babyPhoto.setProperty("imageURL", "http://domain.com/some/path/to/baby_photo.jpg");

	    Entity dancePhoto = new Entity("Photo", tomKey);
	    dancePhoto.setProperty("imageURL", "http://domain.com/some/path/to/dance_photo.jpg");

	    Entity campingPhoto = new Entity("Photo");
	    campingPhoto.setProperty("imageURL", "http://domain.com/some/path/to/camping_photo.jpg");

	    List<Entity> photoList = Arrays.asList(weddingPhoto, babyPhoto, dancePhoto, campingPhoto);
	    datastore.put(photoList);

	    Query photoQuery = new Query("Photo").setAncestor(tomKey);

	    // This returns weddingPhoto, babyPhoto, and dancePhoto, but not campingPhoto, because tom is not an ancestor
	    List<Entity> result =
	        datastore.prepare(photoQuery).asList(FetchOptions.Builder.withDefaults());

	    System.out.println("tom photos " + result);
	    
	    
	    //Kindless ancestor queries
	    Entity ent = new Entity("Person","Gautham");
	    Key entKey = ent.getKey();
	   	datastore.put(ent);
	   	
	   	Entity weddingPhotos = new Entity("Photo", entKey);
	   	weddingPhotos.setProperty("imageURL", "http://domain.com/some/path/to/wedding_photo.jpg");

	   	Entity weddingVideos = new Entity("Video", entKey);
	   	weddingVideos.setProperty("videoURL", "http://domain.com/some/path/to/wedding_video.avi");
	    
	   	List<Entity> mediaList = Arrays.asList(weddingPhotos, weddingVideos);
	   	datastore.put(mediaList);
	   	
	   	// By default, ancestor queries include the specified ancestor itself.
	   	// The following filter excludes the ancestor from the query results.
	   	Filter keyFilter =
	   			new FilterPredicate(Entity.KEY_RESERVED_PROPERTY, FilterOperator.GREATER_THAN, entKey);

	   	Query mediaQuery = new Query().setAncestor(entKey).setFilter(keyFilter);
	   	
	   	List<Entity> media_query_results =
	   		    datastore.prepare(mediaQuery).asList(FetchOptions.Builder.withDefaults());
	   	
	   	System.out.println("media Query: " + media_query_results);
	   	
	   	//Storing hash map in Datastore
	   	
	   	HashMap<String,String> data = new HashMap<String,String>();
	   	data.put("Vishy anand", "Chess");
	   	data.put("Dhoni","cricket");
	   	data.put("Adhiban", "chess");
	   	
	   	ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
	    ObjectOutputStream out = new ObjectOutputStream(byteOut);
	    System.out.println("byte array");
	    byte [] byte_obj = byteOut.toByteArray();
	    
	    
	    Blob blob=  new Blob(byte_obj);
	    Entity ents = new Entity("Person");
	    ents.setProperty("blob_key", blob);
	    datastore.put(ents);
	    
	    
	    System.out.println("byte array..");
	    System.out.println(blob);
	    
	    
	    
	    System.out.println(byteOut.toByteArray() );
	    
	    out.writeObject(data);
	    out.flush();
	    out.close();
	    byteOut.close();
	    System.out.println(out);
	   	
	   
	   	
	   	
	   	
	   	
	   	
	   	
		response.setContentType("text/plain");
		response.setCharacterEncoding("UTF-8");

		response.getWriter().print("Adding Entities to Datastore !!!");
	
	}	
}
