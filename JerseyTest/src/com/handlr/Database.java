package com.handlr;

import java.util.ArrayList;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.Filter;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Query.FilterPredicate;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.model.User;

public class Database 
{
	private DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	private MemcacheService memcache = MemcacheServiceFactory.getMemcacheService();
	
	public Entity generateEntity_phoneInfo(User user)
	{
		Entity entity = new Entity("phoneInfo",user.getPrimaryEmail());
		
		if(!user.getPhoneNumber().getMobileNumber().isEmpty())
		{
			entity.setProperty("mobileNumber",user.getPhoneNumber().getMobileNumber() );
		}
		if (!user.getPhoneNumber().getResidenceNumber().isEmpty())
		{
			entity.setProperty("residenceNumber",user.getPhoneNumber().getResidenceNumber() );
		}
		if(!user.getPhoneNumber().getOfficeNumber().isEmpty())
		{
			entity.setProperty("officeNumber",user.getPhoneNumber().getOfficeNumber());
		}
		return entity;
	}
	
	public Entity generateEntity_userInfo(User user)
	{
		Key key = KeyFactory.createKey("phoneInfo", user.getPrimaryEmail());
		Key addresKey = KeyFactory.createKey("addressInfo", user.getPrimaryEmail());
		
		Entity entity = new Entity("userInfo",user.getPrimaryEmail());
		
		entity.setProperty("userName",user.getUserName() );
		entity.setProperty("primaryEmail",user.getPrimaryEmail() );
		entity.setProperty("alternativeEmail",user.getAlternativeEmail() );
		entity.setProperty("cloudType", user.getCloudType());
		entity.setProperty("phoneNumber",key);
		entity.setProperty("address", addresKey.toString());
		
		return entity;
	}
	
	public Entity generateEntity_addressInfo(String primaryEmail,String type,String street, String city, String state, String country )
	{
		Key key = KeyFactory.createKey("addressInfo", primaryEmail);
		
		Entity entity= new Entity("addressInfo");
		entity.setProperty("addressId", key.toString());
		entity.setProperty("type", type);
		entity.setProperty("street", street);
		entity.setProperty("city", city);
		entity.setProperty("state", state);
		entity.setProperty("country", country);
		
		return entity;
	}
	
	public Entity generateEntity_Memcache(User user)
	{	
		Entity entity = new Entity("userInfo",user.getPrimaryEmail());
		
		entity.setProperty("userName",user.getUserName() );
		entity.setProperty("primaryEmail",user.getPrimaryEmail() );
		entity.setProperty("alternativeEmail",user.getAlternativeEmail() );
		entity.setProperty("cloudType", user.getCloudType());
		
		return entity;
	}
	
	public void put(Key userKey,Entity memCacheEntity,Entity userEntity,Entity phoneInfoEntity,ArrayList<Entity> addressEntity)
	{
		this.memcache.put(userKey, memCacheEntity);
		this.datastore.put(userEntity);
		this.datastore.put(phoneInfoEntity);
		
		for (Entity entity: addressEntity){
			this.datastore.put(entity);
		}
	}	
	
	public void delete(Key userKey, Key phoneKey , String addressKey)
	{
			this.memcache.delete(userKey);
			this.datastore.delete(userKey);
			this.datastore.delete(phoneKey);

			DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
			
		    Filter addressFilter = new FilterPredicate("addressId",FilterOperator.EQUAL,addressKey.toString());
		    Query q = new Query("addressInfo").setFilter(addressFilter);
		    PreparedQuery pq = datastore.prepare(q);
		    for(Entity result : pq.asIterable())
		    {
		    	this.datastore.delete(result.getKey());
		    }
	}	    
		    
}