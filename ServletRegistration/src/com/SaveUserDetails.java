package com;

import java.util.HashMap;

public class SaveUserDetails {
	private static SaveUserDetails User_info_obj;
	private HashMap<String,String> user_details= new HashMap<String,String>();
	private HashMap<String,String> user_name_email= new HashMap<String,String>();
	
	private SaveUserDetails() {
	}

	public HashMap<String,String> getHashMap() {
        return this.user_details;
    }	
	
	public HashMap<String,String> getHashMap_EmailDetails() {
		return this.user_name_email;
	}
	
	public static SaveUserDetails getInstance(){
	   if (User_info_obj == null){ 
	       User_info_obj = new SaveUserDetails();
	   }
	   return User_info_obj;
	 }
}