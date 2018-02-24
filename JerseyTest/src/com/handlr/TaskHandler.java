package com.handlr;

import java.util.ArrayList;
import java.util.List;
import org.json.simple.JSONObject;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.model.Address;
import com.model.User;

public class TaskHandler {
	public String registrationHandler(User user)
	{
		validateRegistration validate = new validateRegistration();
		String validationStatus = validate.validateUserInfo(user);
		
		if (validationStatus.equals("valid") )
		{	
			Database database = new Database();
			
			// Datastore
			
			Entity phoneInfoEntity = database.generateEntity_phoneInfo(user);
			Entity userInfoEntity = database.generateEntity_userInfo(user);
			
			ArrayList<Entity> addressEntity = new ArrayList<Entity>();
			List<Address> addressList = user.getAddress();
			
			for (Address addr: addressList){
				Entity addressInfoEntity = database.generateEntity_addressInfo(user.getPrimaryEmail(), addr.getType(), addr.getStreet() , addr.getCity(),addr.getState(),addr.getCountry() );
				addressEntity.add(addressInfoEntity);
			}
			
			// Memcache
			Key userKey = KeyFactory.createKey("userInfo",user.getPrimaryEmail());
			Entity memCacheEntity = database.generateEntity_Memcache(user);
			
			System.out.println("Memcache entity " + memCacheEntity);
			
			database.put(userKey,memCacheEntity,userInfoEntity,phoneInfoEntity,addressEntity);
			
			return "Success";
		}
		else
		{
			return validationStatus; 
		}
	}
	
	
	//Delete request
	
	@SuppressWarnings("unused")
	public String checkRecordExistence(String kind,String primaryEmail)
	{
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		
		Key key = KeyFactory.createKey(kind, primaryEmail);
		
		if(kind.equals("addressInfo"))
		{
			Key userKey = KeyFactory.createKey("userInfo", primaryEmail);
			try {
				Entity entity = datastore.get(userKey);
				String addressKey = (String)entity.getProperty("address");
				
				Filter addressFilter = new FilterPredicate("addressId",FilterOperator.EQUAL,addressKey);
			    Query q = new Query("addressInfo").setFilter(addressFilter);
			    PreparedQuery pq = datastore.prepare(q);
			    for(Entity result : pq.asIterable())
			    {
			    	return "exist";
			    }
				
			} catch (EntityNotFoundException e) {
				e.printStackTrace();
				return "Record does not exist";
			}
		    return "Record does not exist";
		}
		else
		{
			try {
				Entity entity = datastore.get(key);
				return "exist";
			} catch (EntityNotFoundException e) {
				e.printStackTrace();
				return "Record does not exist";
			}
		}	
	}
		
	public String checkRecordExistence_memCache(String primaryEmail)
	{
		MemcacheService memcache = MemcacheServiceFactory.getMemcacheService();
		Key key = KeyFactory.createKey("userInfo", primaryEmail);
		if( memcache.get(key) == null)
		{
			return "Record does not exist";
		}
		else
		{
			return "exist";
		}
			
	}
	
	public String DeleteUserRecord(User user)
	{
		String userRecordStatus = checkRecordExistence("userInfo",user.getPrimaryEmail());
		String phoneRecordStatus = checkRecordExistence("phoneInfo",user.getPrimaryEmail());
		String addressRecordStatus = checkRecordExistence("addressInfo",user.getPrimaryEmail());
		String memCacheRecordStatus = checkRecordExistence_memCache(user.getPrimaryEmail());
		
		String deleteStatus = "Record does not exist";
		if(userRecordStatus.equals("exist") && phoneRecordStatus.equals("exist") && addressRecordStatus.equals("exist") && memCacheRecordStatus.equals("exist") )
		{	
			Key userKey = KeyFactory.createKey("userInfo", user.getPrimaryEmail());
			Key phoneKey = KeyFactory.createKey("phoneInfo", user.getPrimaryEmail());
			Key addressKey = KeyFactory.createKey("addressInfo", user.getPrimaryEmail());
	    
			Database database = new Database();
		    database.delete(userKey,phoneKey,addressKey.toString());
			
			deleteStatus = "Deleted successfully";
		}
		
		return deleteStatus;
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<JSONObject> filterOnprimaryEmail(String primaryEmail)
	{
		ArrayList<JSONObject> result = new ArrayList<JSONObject>();
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		MemcacheService memcache = MemcacheServiceFactory.getMemcacheService();
		Key userKey = KeyFactory.createKey("userInfo", primaryEmail);
		
		Entity memCacheResult = (Entity) memcache.get(userKey);
		System.out.println("memCacheResult " + memCacheResult);
		
		if(memCacheResult == null)
		{
			try {
				// Getting from datastore
				System.out.println("fetching from datastore");
				Entity entity = datastore.get(userKey);
				
				JSONObject jsonObj = new JSONObject();
				
				jsonObj.put("userName", entity.getProperty("userName"));
				jsonObj.put("primaryEmail", entity.getProperty("primaryEmail"));
				jsonObj.put("alternativeEmail", entity.getProperty("alternativeEmail"));
				jsonObj.put("cloudType", entity.getProperty("cloudType"));
				
				Key phoneNumberKey = (Key) entity.getProperty("phoneNumber");
				
				Entity phoneNumber = datastore.get(phoneNumberKey);
				
				JSONObject obj = new JSONObject();
				if(phoneNumber.getProperty("mobileNumber")!=null)
				{
					obj.put("mobileNumber", phoneNumber.getProperty("mobileNumber"));
				}
				if(phoneNumber.getProperty("residenceNumber")!=null)
				{
					obj.put("residenceNumber", phoneNumber.getProperty("residenceNumber"));
				}
				if(phoneNumber.getProperty("officeNumber")!=null)
				{
					obj.put("officeNumber", phoneNumber.getProperty("officeNumber"));
				}
				jsonObj.put("phoneNumber", obj);
				
				Filter addressFilter = new FilterPredicate("addressId",FilterOperator.EQUAL,entity.getProperty("address"));
			    Query q = new Query("addressInfo").setFilter(addressFilter);
			    PreparedQuery pq = datastore.prepare(q);
			    ArrayList<JSONObject> addressList = new ArrayList<JSONObject>();
			    for(Entity res : pq.asIterable())
			    {
			    	JSONObject addressJson = new JSONObject();
			    	addressJson.put("type", res.getProperty("type"));
			    	addressJson.put("street", res.getProperty("street"));
			    	addressJson.put("city", res.getProperty("city"));
			    	addressJson.put("state", res.getProperty("state"));
			    	addressJson.put("country", res.getProperty("country"));
			    	addressList.add(addressJson);
			    }
			    jsonObj.put("address", addressList);
				result.add(jsonObj);
			}
			catch (EntityNotFoundException e) {
				e.printStackTrace();
			}
		}
		else
		{
			JSONObject jsonObj = new JSONObject(); 
			
			jsonObj.put("userName", memCacheResult.getProperty("userName"));
			jsonObj.put("primaryEmail", memCacheResult.getProperty("primaryEmail"));
			jsonObj.put("alternativeEmail", memCacheResult.getProperty("alternativeEmail"));
			jsonObj.put("cloudType", memCacheResult.getProperty("cloudType"));
			
			String primaryEmailId = (String) memCacheResult.getProperty("primaryEmail");
			Key key = KeyFactory.createKey("phoneInfo", primaryEmailId);
			try {
				Entity entity = datastore.get(key);
				JSONObject phoneNumber = new JSONObject();
				
				for (String str : entity.getProperties().keySet())
				{
					phoneNumber.put(str,entity.getProperty(str));
				}
				jsonObj.put("phoneNumber", phoneNumber);
			} catch (EntityNotFoundException e) {
				e.printStackTrace();
			} 
			
			result.add(jsonObj);
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<JSONObject> filterOnCloudType(String cloudType)
	{
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		ArrayList<JSONObject> result = new ArrayList<JSONObject>();
		
		if(cloudType.equals("google") || cloudType.equals("yahoo") || cloudType.equals("hot mail"))
		{
			Filter propertyFilter =
			        new FilterPredicate("cloudType", FilterOperator.EQUAL, cloudType);
			
			Query query = new Query("userInfo").setFilter(propertyFilter);
			PreparedQuery pq = datastore.prepare(query);
			   
		    for(Entity entity : pq.asIterable())
		    {
		    	JSONObject jsonObj = new JSONObject();
				
		    	jsonObj.put("userName", entity.getProperty("userName"));
				jsonObj.put("primaryEmail", entity.getProperty("primaryEmail"));
				jsonObj.put("alternativeEmail", entity.getProperty("alternativeEmail"));
				jsonObj.put("cloudType", entity.getProperty("cloudType"));
				
				Key phoneKey =  (Key) entity.getProperty("phoneNumber");
				try {
					Entity ent = (Entity) datastore.get(phoneKey);
					JSONObject obj = new JSONObject();
					
					if(ent.getProperty("mobileNumber")!=null)
					{
						obj.put("mobileNumber", ent.getProperty("mobileNumber"));
					}
					if(ent.getProperty("residenceNumber")!=null)
					{
						obj.put("residenceNumber", ent.getProperty("residenceNumber"));
					}
					if(ent.getProperty("officeNumber")!=null)
					{
						obj.put("officeNumber", ent.getProperty("officeNumber"));
					}
					jsonObj.put("phoneNumber", obj);	
					
				} catch (EntityNotFoundException e) {
					e.printStackTrace();
				}
				
				Filter addressFilter = new FilterPredicate("addressId",FilterOperator.EQUAL,entity.getProperty("address"));
			    Query q = new Query("addressInfo").setFilter(addressFilter);
			    
			    PreparedQuery preparedQuery = datastore.prepare(q);
			    ArrayList<JSONObject> addressList = new ArrayList<JSONObject>();
			    for(Entity res : preparedQuery.asIterable())
			    {
			    	JSONObject addressJson = new JSONObject();
			    	addressJson.put("type", res.getProperty("type"));
			    	addressJson.put("street", res.getProperty("street"));
			    	addressJson.put("city", res.getProperty("city"));
			    	addressJson.put("state", res.getProperty("state"));
			    	addressJson.put("country", res.getProperty("country"));
			    	addressList.add(addressJson);
			    }
			    jsonObj.put("address", addressList);
			    
				result.add(jsonObj);
			}
		}
		
		else if(cloudType.equals("all"))
		{
			Query query = new Query("userInfo");
			PreparedQuery pq = datastore.prepare(query);
			   
		    for(Entity entity : pq.asIterable())
		    {
		    	JSONObject jsonObj = new JSONObject();
				
		    	jsonObj.put("userName", entity.getProperty("userName"));
				jsonObj.put("primaryEmail", entity.getProperty("primaryEmail"));
				jsonObj.put("alternativeEmail", entity.getProperty("alternativeEmail"));
				jsonObj.put("cloudType", entity.getProperty("cloudType"));
				
				// Fetching phone Number 
				
				Key phoneKey =  (Key) entity.getProperty("phoneNumber");
				try {
					Entity ent = (Entity) datastore.get(phoneKey);
					JSONObject obj = new JSONObject();
					
					if(ent.getProperty("mobileNumber")!=null)
					{
						obj.put("mobileNumber", ent.getProperty("mobileNumber"));
					}
					if(ent.getProperty("residenceNumber")!=null)
					{
						obj.put("residenceNumber", ent.getProperty("residenceNumber"));
					}
					if(ent.getProperty("officeNumber")!=null)
					{
						obj.put("officeNumber", ent.getProperty("officeNumber"));
					}
					jsonObj.put("phoneNumber", obj);	
					
				} catch (EntityNotFoundException e) {
					e.printStackTrace();
				}
				
				// Fetching address details
				
				Filter addressFilter = new FilterPredicate("addressId",FilterOperator.EQUAL,entity.getProperty("address"));
			    Query q = new Query("addressInfo").setFilter(addressFilter);
			    
			    PreparedQuery preparedQuery = datastore.prepare(q);
			    ArrayList<JSONObject> addressList = new ArrayList<JSONObject>();
			    for(Entity res : preparedQuery.asIterable())
			    {
			    	JSONObject addressJson = new JSONObject();
			    	addressJson.put("type", res.getProperty("type"));
			    	addressJson.put("street", res.getProperty("street"));
			    	addressJson.put("city", res.getProperty("city"));
			    	addressJson.put("state", res.getProperty("state"));
			    	addressJson.put("country", res.getProperty("country"));
			    	addressList.add(addressJson);
			    }
			    jsonObj.put("address", addressList);
			    
				result.add(jsonObj);
			}

		}
		return result;
	}

	public String updateMemcache(JSONObject request)
	{
		String primaryEmail = (String) request.get("primaryEmail");
		String memCacheRecordStatus = checkRecordExistence_memCache(primaryEmail);
		
		if(memCacheRecordStatus.equals("exist"))
		{
			MemcacheService memcache = MemcacheServiceFactory.getMemcacheService();
			String fieldToUpdate = (String) request.get("fieldToUpdate");
			Key userKey = KeyFactory.createKey("userInfo", primaryEmail);
			Entity entity = (Entity) memcache.get(userKey);
			
			if(fieldToUpdate.equals("phoneNumber") || fieldToUpdate.equals("address"))
			{
				memCacheRecordStatus = "Success";
			}
			else
			{
				String valueToReplace = (String) request.get("valueToReplace");
			
				entity.setProperty(fieldToUpdate, valueToReplace);
				memcache.put(userKey, entity);
				memCacheRecordStatus = "Success";
			}
			return memCacheRecordStatus;
		}
		else
		{ 
			return memCacheRecordStatus;
		}
	}
	
	@SuppressWarnings("unchecked")
	public String updateDatastore(JSONObject request)
	{
		String primaryEmail = (String) request.get("primaryEmail");
		String fieldToUpdate = (String) request.get("fieldToUpdate");
		
		DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
		
		String datastoreStatus;
		if(fieldToUpdate.equals("address"))
		{
				Entity entity;
				try {
					Key userKey = KeyFactory.createKey("userInfo", primaryEmail);
					entity = (Entity) datastore.get(userKey);
					
					ArrayList<JSONObject> valueToReplace = (ArrayList<JSONObject>) request.get("valueToReplace");
					
					Filter addressFilter = new FilterPredicate("addressId",FilterOperator.EQUAL,entity.getProperty("address"));
				    Query q = new Query("addressInfo").setFilter(addressFilter);
				    PreparedQuery pq = datastore.prepare(q);
				   
				    for(Entity res : pq.asIterable())
				    {
				    	String field = (String) res.getProperty("type");
				    	for (JSONObject address: valueToReplace)
				    	{
							String targetField = (String) address.get("type");
							if(targetField.equals(field))
							{	
								res.setProperty("street", address.get("street"));
								res.setProperty("city", address.get("city"));
								res.setProperty("state", address.get("state"));
								res.setProperty("country", address.get("country"));
								
								datastore.put(res);
								System.out.println("Resultant entity : " + res);
							}
						}
				    }
				  datastoreStatus = "Success";
				} catch (EntityNotFoundException e) {
					datastoreStatus = "Record does not exist";
					e.printStackTrace();
				}
		}
		else if (fieldToUpdate.equals("phoneNumber") )
		{	
			try {
				Key phoneKey = KeyFactory.createKey("phoneInfo", primaryEmail);
				
				Entity entity = (Entity) datastore.get(phoneKey);
				JSONObject valueToReplace = (JSONObject) request.get("valueToReplace");
				
				for (Object key : valueToReplace.keySet()) {
			        String field = (String)key;
			        entity.setProperty(field, valueToReplace.get(field) );
			    }
				
				datastore.put(entity);				
				datastoreStatus = "Success";
			
			} catch (EntityNotFoundException e) {
				datastoreStatus = "Record does not exist";
				e.printStackTrace();
			}
		}
		else
		{
			String valueToReplace = (String) request.get("valueToReplace");
			
			Key userKey = KeyFactory.createKey("userInfo", primaryEmail);
			
			try {
				Entity entity = datastore.get(userKey);
				entity.setProperty(fieldToUpdate, valueToReplace);
				datastore.put(entity);
				datastoreStatus = "Success";
			} catch (EntityNotFoundException e) {
				datastoreStatus = "Record does not exist";
				e.printStackTrace();
			}
		}
		return datastoreStatus;
	}
	
	public String updateHandlr(JSONObject request)
	{
		String memCacheStatus = updateMemcache(request);
		String datastoreStatus = updateDatastore(request);
		
		if(memCacheStatus.equals("Success") && datastoreStatus.equals("Success") )
		{
			return "Successfully updated";
		}
		else if(!memCacheStatus.equals("Success"))
		{
			 return memCacheStatus;
		}
		else
		{
			return datastoreStatus;
		}
	}
		
}