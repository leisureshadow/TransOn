package ntu.csie.transon.server.databean;

import java.util.Date;
import java.util.HashSet;
import ntu.csie.transon.db.DataBase;

public class MeetingInformation {
	private String meetingID, subject, location, description, initiator;
	private Date startTime, endTime;
	private double latitude, longitude;
	private boolean isPrivate, isSecret, isPause; 
	private HashSet<User> blacklist, administrators, readableList, participants;
	
	public MeetingInformation(String meetingID, String subject, String location, Date startTime, Date endTime,
			String description, String initiator, double latitude, double longitude, boolean _private, boolean secret, boolean pause) {
		this.meetingID = meetingID;
		this.subject = subject;
		this.location = location;
		this.startTime = startTime;
		this.endTime = endTime;
		this.description = description;
		this.initiator = initiator;
		this.latitude = latitude;
		this.longitude = longitude;
		this.isPrivate = _private;
		this.isSecret = secret;
		this.isPause = pause;
		this.blacklist = new HashSet<User>();
		this.administrators = new HashSet<User>();
		this.participants = new HashSet<User>();
		this.readableList = new HashSet<User>();
	}
	
	public void setMeetingID(String meetingID) {
		this.meetingID = meetingID;
	}
	
	public void addParticipant(String userid){
		participants.add(DataBase.getUserInformation(userid));
		DataBase.addUserToParticipant(meetingID, userid);
	}
	
	public void restoreParticipant(String userid){
		participants.add(DataBase.getUserInformation(userid));
	}
	
	public void removeParticipant(String userID){
		for (User user : participants) {
		    if (user.getId().equals(userID))
		    {
		    	participants.remove(user);
				DataBase.deleteUserToParticipant(meetingID, userID);
		    	return;
		    }
		}
	}
	
	public void cleanParticipant(){
		participants = new HashSet<User>(0);
	}
	
	public void addToReadablelist(String userid){
		readableList.add(DataBase.getUserInformation(userid));
		DataBase.addUserToReadablelist(meetingID, userid);
	}
	
	public void restoreToReadablelist(String userid){
		readableList.add(DataBase.getUserInformation(userid));
	}
	
	public void removeFromReadablelist(String userID){
		for (User user : readableList) {
		    if (user.getId().equals(userID))
		    {
		    	readableList.remove(user);
				DataBase.deleteUserToReadablelist(meetingID, userID);
		    	return;
		    }
		}
	}
	
	public void addToBlacklist(String userID){
		blacklist.add(DataBase.getUserInformation(userID));
		DataBase.addUserToBlacklist(meetingID, userID);
	}
	
	public void restoreToBlacklist(String userid){
		blacklist.add(DataBase.getUserInformation(userid));
	}
	
	public void removeFromBlacklist(String userID){
		for (User user : blacklist) {
		    if (user.getId().equals(userID))
		    {
		    	blacklist.remove(user);
				DataBase.deleteUserToBlacklist(meetingID, userID);
		    	return;
		    }
		}
	}
	
	public HashSet<User> getBlacklist(){
		return blacklist;
	}
	
	public void addAdmin(String userID)
	{
		administrators.add(DataBase.getUserInformation(userID));
		DataBase.addUserToAdminlist(meetingID, userID);
	}
	
	public void restoreAdmin(String userid){
		administrators.add(DataBase.getUserInformation(userid));
	}
	
	public HashSet<User> getAdmins(){
		return administrators;
	}
	
	public void removeAdmin(String userID)
	{
		for (User user : administrators) {
		    if (user.getId().equals(userID))
		    {
		    	administrators.remove(user);
				DataBase.deleteUserToAdminlist(meetingID, userID);
		    	return;
		    }
		}
	}
	
	public String getMeetingID() {
		return meetingID;
	}
	
	public String getSubject() {
		return subject;
	}
	
	public void setSubject(String subject) {
		this.subject = subject;
		DataBase.updateMeetingInformation(this);
	}
	
	public String getLocation() {
		return location;
	}
	
	public void setLocation(String location) {
		this.location = location;
		DataBase.updateMeetingInformation(this);
	}
	
	public Date getStartTime() {
		return startTime;
	}
	
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
		DataBase.updateMeetingInformation(this);
	}
	
	public Date getEndTime() {
		return endTime;
	}
	
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
		DataBase.updateMeetingInformation(this);
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
		DataBase.updateMeetingInformation(this);
	}
	
	public String getInitiator() {
		return initiator;
	}
	
	public void setInitiator(String initiator) {
		this.initiator = initiator;
		DataBase.updateMeetingInformation(this);
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
		DataBase.updateMeetingInformation(this);
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
		DataBase.updateMeetingInformation(this);
	}

	public boolean is_private() {
		return isPrivate;
	}

	public void set_private(boolean _private) {
		this.isPrivate = _private;
		DataBase.updateMeetingInformation(this);
	}
	
	public boolean isSecret() {
		return isSecret;
	}

	public void setSecret(boolean secret) {
		this.isSecret = secret;
		DataBase.updateMeetingInformation(this);
	}

	public boolean isPause() {
		return isPause;
	}

	public void setPause(boolean pause) {
		this.isPause = pause;
		DataBase.updateMeetingInformation(this);
	}
}
