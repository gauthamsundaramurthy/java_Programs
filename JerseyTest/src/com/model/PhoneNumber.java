package com.model;

import java.util.ArrayList;


public class PhoneNumber {

	private ArrayList<Long> mobileNumber;
	private ArrayList<Long> residenceNumber;
	private ArrayList<Long> officeNumber;
	
	public ArrayList<Long> getMobileNumber() {
		return mobileNumber;
	}
	
	public void setMobileNumber(ArrayList<Long> mobileNumber) {
		this.mobileNumber = mobileNumber;
	}
	
	public ArrayList<Long> getResidenceNumber() {
		return residenceNumber;
	}
	
	public void setResidenceNumber(ArrayList<Long> residenceNumber) {
		this.residenceNumber = residenceNumber;
	}
	
	public ArrayList<Long> getOfficeNumber() {
		return officeNumber;
	}
	
	public void setOfficeNumber(ArrayList<Long> officeNumber) {
		this.officeNumber = officeNumber;
	}
	
}
