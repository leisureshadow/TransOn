package ntu.csie.transon.server.history;
import java.io.IOException;
import java.net.ServerSocket;
import java.nio.channels.SelectionKey;
import java.util.Hashtable;
import java.util.ArrayList;

import ntu.csie.transon.server.databean.MeetingInformation;
import ntu.csie.transon.server.databean.User;
import ntu.csie.transon.server.connect.Acceptor;
import ntu.csie.transon.server.connect.HistoryServiceHandler;
import ntu.csie.transon.server.connect.HistoryServiceHandlerFactory;
import ntu.csie.transon.server.connect.Reactor;
import ntu.csie.transon.db.DataBase;

public class HistorySearcher {
	private Hashtable<String, Integer> existHistoryRoom;//<MeetingID, port>
	private static HistorySearcher historySearcher;
	
	private HistorySearcher(){
		this.existHistoryRoom = new Hashtable<String,Integer>(0);
	}
	
	public static HistorySearcher getInstance(){
		if(historySearcher == null){
			historySearcher = new HistorySearcher();
		}
		return historySearcher;
	}
	
	public ArrayList<MeetingInformation>getReadableHistoryList(String userId){
		return DataBase.getReadableHistoryList(userId);
	}
	
	public int getHistoryPort(String historyId){
		if(!existHistoryRoom.containsKey(historyId)){
			 HistoryRoom historyroom = new HistoryRoom(historyId);
			 
		//	 historyroom.enter(userId, handler);
			 int port = historyroom.getPort();
			 existHistoryRoom.put(historyId, port);
			 return historyroom.getPort();
		}
		else
		{
			return existHistoryRoom.get(historyId);
		}
	}
	
}
