package ntu.csie.transon.server.meetingroom;
import ntu.csie.transon.db.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.UUID;

import ntu.csie.transon.server.databean.MeetingInformation;
import ntu.csie.transon.server.databean.User;
public class MeetingList {
	private Hashtable<String, MeetingRoom> ongoingMeetingList;
	private static MeetingList meetingList;
	
	private MeetingList(){
		this.ongoingMeetingList =  new Hashtable <String, MeetingRoom>();
		ArrayList<MeetingInformation> meetings = DataBase.getAllMeeting();
		Iterator<MeetingInformation> iter = meetings.iterator();
		//Reconstruct the meetings
		while(iter.hasNext()){
			MeetingInformation information = iter.next();
			System.out.println(information.getMeetingID()+" is reconstructed.");
			ongoingMeetingList.put(information.getMeetingID(), new MeetingRoom(information));
		}
	}
	public static MeetingList getInstance(){
		if(meetingList == null){
			meetingList = new MeetingList();
		}
		return meetingList;
	}
	
	public int getMeetingPort(String MeetingID){
		MeetingRoom meetingRoom = ongoingMeetingList.get(MeetingID);
		if(meetingRoom == null){
			System.err.println("The meeting id: " + MeetingID + "is null!!");
			return -1;
		}
		return meetingRoom.getPort();
	}
	
	public int createMeeting(MeetingInformation info, User user){
		String id = generateMeetingID();
		info.setMeetingID(id);
		info.setStartTime(new Date());
		info.setInitiator(user.getName());
		MeetingRoom meetingroom = new MeetingRoom(info);
		ongoingMeetingList.put(id, meetingroom);
		meetingroom.setAdmin(user.getId());
		DataBase.createMeetingInformation(info);
		return meetingroom.getPort();
	}
	
	public void terminateAMeeting(MeetingInformation info){
		ongoingMeetingList.remove(info.getMeetingID());
	}
	private String generateMeetingID(){
		return UUID.randomUUID().toString().substring(0,8);
	}
	
	
	public ArrayList<MeetingInformation> getNearbyMeetings(double latitude, double longitude){
		ArrayList<MeetingInformation> meetingList = DataBase.getNearbyMeeting(latitude, longitude);
		return meetingList;
	}
	
}
