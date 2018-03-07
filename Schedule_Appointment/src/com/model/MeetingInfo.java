package com.model;

import java.util.ArrayList;

public class MeetingInfo {

	private String name;
	private String description;
	private String periodFrom;
	private String periodTo;
	private String startDate;
	private String startTime;
	private String endDate;
	private String endTime;
	private String venue;
	private String organiser;
	private ArrayList<String> guest;
	private String repeatMode;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name.toLowerCase().trim();
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description.toLowerCase().trim();
	}
	
	public String getStartDate() {
		return startDate;
	}
	public void setStartDate(String startDate) {
		this.startDate = startDate.trim();
	}
	
	public String getStartTime() {
		return startTime;
	}
	public void setStartTime(String startTime) {
		this.startTime = startTime.trim();
	}
	
	public String getEndDate() {
		return endDate;
	}
	public void setEndDate(String endDate) {
		this.endDate = endDate.trim();
	}
	
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime.trim();
	}
	
	public String getVenue() {
		return venue;
	}
	public void setVenue(String venue) {
		this.venue = venue.toLowerCase().trim();
	}

	public String getOrganiser() {
		return organiser;
	}
	public void setOrganiser(String organiser) {
		this.organiser = organiser.toLowerCase().trim();
	}
	
	public ArrayList<String> getGuest() {
		return guest;
	}
	public void setGuest(ArrayList<String> guest) {
		this.guest = guest;
	}
	
	public String getRepeatMode() {
		return repeatMode;
	}
	public void setRepeatMode(String repeatMode) {
		this.repeatMode = repeatMode.toLowerCase().trim();
	}
	public String getPeriodFrom() {
		return periodFrom;
	}
	public void setPeriodFrom(String periodFrom) {
		this.periodFrom = periodFrom;
	}
	
	public String getPeriodTo() {
		return periodTo;
	}
	public void setPeriodTo(String periodTo) {
		this.periodTo = periodTo;
	}
	
	
}
