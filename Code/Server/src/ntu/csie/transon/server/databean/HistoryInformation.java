package ntu.csie.transon.server.databean;

import java.util.ArrayList;



public class HistoryInformation {
	private String subject, location, startTime, endTime, description, initiator, meetingId;
	private double latitude, longitude;
	private ArrayList<User> readableList;

	public HistoryInformation(String subject, String location, String startTime, String endTime,
	String description, String initiator, double latitude, double longitude, String meetingId) {
	
	this.subject = subject;
	this.location = location;
	this.startTime = startTime;
	this.endTime = endTime;
	this.description = description;
	this.initiator = initiator;
	this.latitude = latitude;
	this.longitude = longitude;
	this.meetingId = meetingId;
	readableList = new ArrayList<User>();
	}
	
	public String getSubject(){
		return subject;
	}
	public String getLocation(){
		return location;
	}
	public double getLatitude(){
		return latitude;
	}
	public double getLongtitude(){
		return longitude;
	}
	public String getendTime(){
		return endTime;
	}
	public String getstartTime(){
		return startTime;
	}
	public String getdescription(){
		return description;
	}
	public ArrayList<User> getReadableList(){
		return readableList;
	}
	
	
	
}
