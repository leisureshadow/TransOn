package ntu.csie.transon.server.history;

import java.nio.channels.SelectionKey;
import java.util.Date;
import java.util.HashMap;
import java.util.ArrayList;
import ntu.csie.transon.db.*;
import ntu.csie.transon.server.connect.Acceptor;
import ntu.csie.transon.server.connect.HistoryServiceHandler;
import ntu.csie.transon.server.connect.HistoryServiceHandlerFactory;
import ntu.csie.transon.server.connect.Reactor;
import ntu.csie.transon.server.connect.packet.Packet;
import ntu.csie.transon.server.databean.User;
import ntu.csie.transon.server.databean.Message;
import ntu.csie.transon.server.databean.MeetingInformation;

public class HistoryRoom {
	
	private HashMap<User, HistoryServiceHandler> userList;
	private Acceptor acceptor;
	private MeetingInformation info;
	
	public HistoryRoom(String meetingId){
	this.acceptor = new Acceptor(new HistoryServiceHandlerFactory(this));
	this.acceptor.open();
	Reactor.getInstance().registerHandler(this.acceptor, SelectionKey.OP_ACCEPT);
	this.info = DataBase.getMeetingInformation(meetingId);
	this.userList = new HashMap<User, HistoryServiceHandler>(0);
	}
	
	public int getPort(){
		return acceptor.getPort();
	}
	
	public void getLog(String userID,Date endtime){
		User user = DataBase.getUserInformation(userID);
		int num = 30; 
		ArrayList<Message> log = DataBase.getHistory(info.getMeetingID(),num,endtime);
		Packet returnPacket = new Packet("Log");
		returnPacket.addItems("log", log);
		userList.get(user).sendCommand(returnPacket);
	}
	
	public void leave(User user){
		userList.remove(user.getId());
		if(userList.size() == 0)
			Reactor.getInstance().cancelRegister(this.acceptor);
		
	}
	public void enter(String userId, HistoryServiceHandler handler){
		User user = DataBase.getUserInformation(userId);
		userList.put(user, handler);
		handler.setUser(user);
		Packet returnPacket = new Packet("HistoryInfo");
		returnPacket.addItems("historyInfo", info);
		handler.sendCommand(returnPacket);
	}
	
	public void setHistoryInfo(MeetingInformation historyinfo){
		DataBase.updateMeetingInformation(historyinfo);
		info = historyinfo;
	}
	
	public void updateHistoryInfo(MeetingInformation historyInfo){
		DataBase.updateMeetingInformation(historyInfo);
		info = historyInfo;
		updateAllUser();
	}
	
	public void updateAllUser(){
		Packet updatePacket = new Packet("UpdateHistoryInfo");
		updatePacket.addItems("historyInfo", info);
		for(User user:userList.keySet()){
			HistoryServiceHandler handler = userList.get(user);
			handler.sendCommand(updatePacket);
		}
	}
	
	public MeetingInformation getHistoryInfo(){
		return info;
	}
}
