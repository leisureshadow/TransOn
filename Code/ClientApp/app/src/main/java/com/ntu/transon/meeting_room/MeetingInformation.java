package com.ntu.transon.meeting_room;

import com.ntu.transon.User;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Mars on 2014/12/27.
 */
public class MeetingInformation {
    private String meetingID;
    private String subject;
    private String location;
    private double latitude;
    private double longitude;
    private Date startTime;
    private Date endTime;
    private String description;
    private String initiator;
    private boolean isPrivate;
    private boolean isSecret;
    private boolean isPause;
    private ArrayList<User> participants = new ArrayList<User>();
    private ArrayList<User> administrators = new ArrayList<User>();
    private ArrayList<User> readableList = new ArrayList<User>();
    private ArrayList<User> blacklist = new ArrayList<User>();

    public MeetingInformation(String subject, String location, double latitude, double longitude, String description, String initiator, boolean isPrivate,boolean isSecret) {
        this.subject = subject;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
        this.description = description;
        this.initiator = initiator;
        this.isPrivate = isPrivate;
        this.isSecret = isSecret;
        this.administrators = new ArrayList<User>();
        this.participants = new ArrayList<User>();
        this.readableList = new ArrayList<User>();
        this.blacklist = new ArrayList<User>();
    }

    public String getMeetingID() {
        return meetingID;
    }

    public void setMeetingID(String meetingID) {
        this.meetingID = meetingID;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getInitiator() {
        return initiator;
    }

    public void setInitiator(String initiator) {
        this.initiator = initiator;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean isPrivate) {
        this.isPrivate = isPrivate;
    }

    public boolean isSecret() {
        return isSecret;
    }

    public void setSecret(boolean isSecret) {
        this.isSecret = isSecret;
    }

    public ArrayList<User> getParticipants() {
        return participants;
    }

    public void setParticipants(ArrayList<User> participants) {
        this.participants = participants;
    }

    public ArrayList<User> getAdministrators() {
        return administrators;
    }

    public void setAdministrators(ArrayList<User> administrators) {
        this.administrators = administrators;
    }

    public ArrayList<User> getReadableList() {
        return readableList;
    }

    public void setReadableList(ArrayList<User> readableList) {
        this.readableList = readableList;
    }

    public ArrayList<User> getBlacklist() {
        return blacklist;
    }

    public void setBlacklist(ArrayList<User> blacklist) {
        this.blacklist = blacklist;
    }
}
