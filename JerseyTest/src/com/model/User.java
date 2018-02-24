package com.model;

import java.util.ArrayList;
import java.util.List;

public class User {
	
	private String userName;
	private String primaryEmail;
	private String alternativeEmail;
	private String cloudType;
	
	private PhoneNumber phoneNumber ;
	
	private List<Address> address = new ArrayList<Address>();
	
	public List<Address> getAddress() {
		return address;
	}
	public void setAddress(List<Address> address) {
		this.address = address;
	}
	
	//Get and Set UserName	
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getUserName() {
		return userName;
	}
	
	//Get and Set 'PrimaryEmail'
	public void setPrimaryEmail(String primaryEmail) {
		this.primaryEmail = primaryEmail.toLowerCase();
	}
	public String getPrimaryEmail() {
		return primaryEmail;
	}

	//Get and Set 'AlternativeEmail'
	public void setAlternativeEmail(String alternativeEmail) {
		this.alternativeEmail = alternativeEmail.toLowerCase();
	}
	public String getAlternativeEmail() {
		return alternativeEmail;
	}
		
	//Get and set 'CloudType'
	public void setCloudType(String cloudType) {
		this.cloudType = cloudType.toLowerCase();
	}
	
	public String getCloudType() {
		return cloudType;
	}
	
	public PhoneNumber getPhoneNumber() {
		return phoneNumber;
	}
	
	public void setPhoneNumber(PhoneNumber phoneNumber) {
		this.phoneNumber = phoneNumber;
	}
	
	
}
