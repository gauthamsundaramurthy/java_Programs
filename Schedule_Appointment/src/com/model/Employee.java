package com.model;

public class Employee {

	private String empId;
	private String firstName;
	private String lastName;
	private String emailId;
	private String company;
	private String team;
	private String position;
	private String authority;
	
	public String getEmpId() {
		return empId;
	}
	public void setEmpId(String empId) {
		this.empId = empId.toLowerCase().trim();
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName.toLowerCase().trim();
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName.toLowerCase().trim();
	}
	public String getEmailId() {
		return emailId;
	}
	public void setEmailId(String emailId) {
		this.emailId = emailId.toLowerCase().trim();
	}
	public String getCompany() {
		return company;
	}
	public void setCompany(String company) {
		this.company = company.toLowerCase().trim();
	}
	public String getTeam() {
		return team;
	}
	public void setTeam(String team) {
		this.team = team.toLowerCase().trim();
	}
	public String getPosition() {
		return position;
	}
	public void setPosition(String position) {
		this.position = position.toLowerCase().trim();
	}
	public String getAuthority() {
		return authority;
	}
	
	public void setAuthority(String authority) {
		this.authority = authority.toLowerCase().trim();
	}

	
	
}
