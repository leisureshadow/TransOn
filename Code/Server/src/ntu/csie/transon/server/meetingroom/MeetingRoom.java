package ntu.csie.transon.server.meetingroom;

import java.nio.channels.SelectionKey;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import ntu.csie.transon.db.DataBase;
import ntu.csie.transon.server.connect.Acceptor;
import ntu.csie.transon.server.connect.MeetingServiceHandler;
import ntu.csie.transon.server.connect.MeetingServiceHandlerFactory;
import ntu.csie.transon.server.connect.Reactor;
import ntu.csie.transon.server.connect.packet.Packet;
import ntu.csie.transon.server.databean.MeetingInformation;
import ntu.csie.transon.server.databean.Message;
import ntu.csie.transon.server.databean.User;

public class MeetingRoom {
	private Acceptor acceptor;
	private MeetingInformation information;
	private Set<String> admins, blacklist;
	private HashMap<String, Participant> participants;
	private HashMap<String, MeetingServiceHandler> waitingQueue;

	public MeetingRoom(MeetingInformation information) {
		this.acceptor = new Acceptor(new MeetingServiceHandlerFactory(this));
		this.acceptor.open();
		Reactor.getInstance().registerHandler(this.acceptor, SelectionKey.OP_ACCEPT);
		this.information = information;
		this.participants = new HashMap<String, Participant>();
		this.waitingQueue = new HashMap<String, MeetingServiceHandler>();
		this.admins = new HashSet<String>();
		this.blacklist = new HashSet<String>();
		
		information.cleanParticipant();
		for(User user: information.getAdmins()){
			admins.add(user.getId());
		}
		for(User user: information.getBlacklist()){
			blacklist.add(user.getId());
		}
	}
	
	public boolean inAdmin(String userID){		
		return admins.contains(userID);
	}
	
	public boolean inBlacklist(String userID){
		return blacklist.contains(userID);
	}
	
	public boolean inParticipant(String userID){
		if (participants == null) System.out.println("participants is null");
		for (String t:participants.keySet()) System.out.println(t);
		return participants.containsKey(userID);
	}
	
	public boolean inWaitingQueue(String userID){
		return waitingQueue.containsKey(userID);
	}

	public int getPort(){
		return acceptor.getPort();
	}
	
	public void accept(String userID) {
		if (!inWaitingQueue(userID)) {
			System.out.println("Accept: " + userID + "is not in waitingQueue.");
			return;
		}
		
		MeetingServiceHandler meetingServiceHandler = waitingQueue.get(userID);
		waitingQueue.remove(userID);
		Participant participant = new Participant(userID, meetingServiceHandler);
		meetingServiceHandler.setParticipant(participant);
		participants.put(userID, participant);
		information.addParticipant(userID);

		Packet packet = new Packet("MeetingInfo");
		packet.addItems("meetingInfo", getInformation());
		meetingServiceHandler.sendCommand(packet);
		
		updateMeetingInfo();
	}

	public void reject(String userID) {
		if (!inWaitingQueue(userID)) {
			System.out.println("Reject: " + userID + "is not in waitingQueue.");
			return;
		}
		MeetingServiceHandler meetingServiceHandler = waitingQueue.get(userID);
		waitingQueue.remove(userID);
		meetingServiceHandler.sendCommand(new Packet("Reject"));
	}

	public void participate(String userID, MeetingServiceHandler meetingServiceHandler) {
		Participant participant = new Participant(userID, meetingServiceHandler);
		meetingServiceHandler.setParticipant(participant);
		participants.put(userID, participant);
		information.addParticipant(userID);

		Packet returnPacket = new Packet("MeetingInfo");
		returnPacket.addItems("meetingInfo", information);
		meetingServiceHandler.sendCommand(returnPacket);
		updateMeetingInfo();
	}

	public void kick(String userID) {
		if (!inParticipant(userID)) {
			System.out.println("Kick: " + userID + "is not in participants.");
			return;
		}
		participants.get(userID).kick();
		participants.remove(userID);
		information.removeParticipant(userID);
		information.addToBlacklist(userID);
		blacklist.add(userID);
		
		updateMeetingInfo();
	}

	public void mute(String userID) {
		if (!inParticipant(userID)) {
			System.out.println("Mute: " + userID + "is not in participants.");
			return;
		}
		participants.get(userID).mute();
	}

	public void unmute(String userID) {
		if (!inParticipant(userID)) {
			System.out.println("Unmute: " + userID + "is not in participants.");
			return;
		}
		participants.get(userID).unmute();
	}

	public void setAdmin(String userID) {
		admins.add(userID);
		information.addAdmin(userID);
		if (!inParticipant(userID)) {
			System.out.println("SetAdmin: " + userID + "is not in participants.");
			return;
		}
		participants.get(userID).setAdmin();
		
		updateMeetingInfo();
	}

	public void unSetAdmin(String userID) {
		if (!inParticipant(userID)) {
			System.out.println("UnSetAdmin: " + userID + "is not in participants.");
			return;
		}
		participants.get(userID).unSetAdmin();
		admins.remove(userID);
		information.removeAdmin(userID);
		
		updateMeetingInfo();
	}

	public void leave(String userID) {
		if (!inParticipant(userID)) {
			System.out.println("Leave: " + userID + "is not in participants.");
			return;
		}
		participants.get(userID).unregisterHandler();
		participants.remove(userID);
		information.removeParticipant(userID);
		updateMeetingInfo();
	}

	public void pause() {
		for (Participant participant : participants.values()) {
			participant.pause();
		}
		information.setPause(true);
		
		updateMeetingInfo();
	}

	public void _continue() {
		for (Participant participant : participants.values()) {
			participant._continue();
		}
		information.setPause(false);
		
		updateMeetingInfo();
	}

	public void terminate() {
		information.setEndTime(new Date());
		
		for (String userID : participants.keySet()) {
			DataBase.addUserToReadablelist(information.getMeetingID(), userID);
		}
		
		for (Participant participant : participants.values()) {
			participant.terminate();
		}
		Reactor.getInstance().cancelRegister(acceptor);
		MeetingList.getInstance().terminateAMeeting(information);
		DataBase.updateMeetingInformation(information);
	}

	public void sendMessage(String text, User user) {
		Message msg = new Message(user, text, new Date());
		DataBase.saveMessage(information.getMeetingID(), msg);
		for (Participant participant : participants.values()) {
			participant.sendMessage(msg);
		}
	}
	
	public void sendAcceptRequest(String userID, MeetingServiceHandler meetingServiceHandler) {
		waitingQueue.put(userID, meetingServiceHandler);
		User user = DataBase.getUserInformation(userID);
		for (String uid : admins) {
			participants.get(uid).sendAcceptRequest(user);
		}
	}
	
	public ArrayList<Message> getLog(Date date){
		return DataBase.getHistory(information.getMeetingID(), 30, date);
	}

	public MeetingInformation getInformation() {
		return information;
	}
	
	public void updateMeetingInfo(MeetingInformation _information){
		information = _information;
		this.updateMeetingInfo();
		
	}
	public void updateMeetingInfo(){
		for (Participant participant : participants.values()) {
			participant.updateMeetingInfo(information);
		}
	}

}
