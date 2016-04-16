package ntu.csie.transon.server.meetingroom;

import ntu.csie.transon.db.DataBase;
import ntu.csie.transon.server.connect.MeetingServiceHandler;
import ntu.csie.transon.server.connect.Reactor;
import ntu.csie.transon.server.connect.packet.Packet;
import ntu.csie.transon.server.databean.MeetingInformation;
import ntu.csie.transon.server.databean.Message;
import ntu.csie.transon.server.databean.User;

public class Participant {
	private User user;

	private MeetingServiceHandler meetingServiceHandler;
	
	public Participant(String userid, MeetingServiceHandler meetingServiceHandler) {
		this.user = DataBase.getUserInformation(userid);
		this.meetingServiceHandler = meetingServiceHandler;
	}
	
	public void kick(){
		Packet packet = new Packet("Kick");
		meetingServiceHandler.sendCommand(packet);
		unregisterHandler();
	}
	
	public void mute(){
		Packet packet = new Packet("Mute");
		meetingServiceHandler.sendCommand(packet);
	}
	
	public void unmute(){
		Packet packet = new Packet("Unmute");
		meetingServiceHandler.sendCommand(packet);
	}
	
	public void setAdmin(){
		Packet packet = new Packet("SetAdmin");
		meetingServiceHandler.sendCommand(packet);
	}
	
	public void unSetAdmin(){
		Packet packet = new Packet("UnsetAdmin");
		meetingServiceHandler.sendCommand(packet);
	}
	
	public void pause(){
		Packet packet = new Packet("Pause");
		meetingServiceHandler.sendCommand(packet);
	}
	
	public void _continue(){
		Packet packet = new Packet("Continue");
		meetingServiceHandler.sendCommand(packet);
	}
	
	public void terminate(){
		Packet packet = new Packet("Terminate");
		meetingServiceHandler.sendCommand(packet);
		unregisterHandler();
	}
	
	public void sendMessage(Message msg){
		Packet packet = new Packet("Message");
		packet.addItems("message", msg);
		meetingServiceHandler.sendCommand(packet);
	}
	
	public void sendAcceptRequest(User user){
		Packet packet = new Packet("AccessRequest");
		packet.addItems("user", user);
		meetingServiceHandler.sendCommand(packet);
	}
	
	public User getUser() {
		return user;
	}
	
	public void unregisterHandler(){
		Reactor.getInstance().cancelRegister(meetingServiceHandler);
	}
	
	public void updateMeetingInfo(MeetingInformation meetingInformation){
		Packet packet = new Packet("UpdateMeetingInfo");
		packet.addItems("meetingInfo", meetingInformation);
		meetingServiceHandler.sendCommand(packet);
	}
}
