package ntu.csie.transon.server.history;

import static org.junit.Assert.*;

import java.util.Hashtable;
import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.Set;
import java.util.Date;
import java.util.Set;

import ntu.csie.transon.db.DataBase;
import ntu.csie.transon.server.connect.Acceptor;
import ntu.csie.transon.server.connect.HistoryServiceHandler;
import ntu.csie.transon.server.connect.HistoryServiceHandlerFactory;
import ntu.csie.transon.server.connect.Reactor;
import ntu.csie.transon.server.connect.ServiceHandlerFactory;
import ntu.csie.transon.server.databean.MeetingInformation;
import ntu.csie.transon.server.databean.User;
import ntu.csie.transon.server.meetingroom.MeetingList;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class HistoryRoomTest {

	private static MeetingInformation info;
	private static HistoryRoom tester;
//	private DataBase db;
	private static HistoryServiceHandler hs;
	private static User user;
	private Hashtable <User, HistoryServiceHandler> userList;
	private static MeetingList list;
	@BeforeClass
	public static void setup() throws IOException{
		list = MeetingList.getInstance();
		info = new MeetingInformation ("test", "subject", "305", new Date (), new Date (),
				"des", "init", 100, 100, false, false,
				false);
		user = new User("ua","ub","uc");
		list.createMeeting(info, user);
		
		
		SocketChannel s =  SocketChannel.open();
		// hs = new HistoryServiceHandler(s, tester);
		//ServiceHandlerFactory fac =  new ServiceHandlerFactory();
		
		
		//hs = new HistoryServiceHandler(s, tester);
		DataBase.createDataBase("WTF");
		}
	
	@Test
	public void testHistoryRoom() {
		list.createMeeting(info, user);
		list.terminateAMeeting(info);
		HistoryRoom tester = new HistoryRoom(info.getMeetingID());
		//assertEquals(DataBase.getMeetingInformation(info.getMeetingID()), tester1.getHistoryInfo());
		assertEquals(tester.getHistoryInfo().getMeetingID(),info.getMeetingID());
	}

	@Test
	public void testGetPort() {
		assertNotNull(tester.getPort());
	}

	@Test
	public void testGetLog() {
		list.createMeeting(info, user);
	//	System.out.println(DataBase.getHistory(info.getMeetingID(), 10, null));
		
	}

	@Test
	public void testLeave() {
		list.createMeeting(info, user);
		list.terminateAMeeting(info);
		tester = new HistoryRoom(info.getMeetingID());
		tester.leave(user);
		//tester.enter
		//tester.leave
		//cancel register
	}

	@Test
	public void testEnter() {
		
		
		//int s = userList.size();
		//tester.enter(user.getId(),hs);
		//assertEquals(userList.size(),s+1);
		
	}

	@Test
	public void testSetHistoryInfo() {
		list.createMeeting(info, user);
		list.terminateAMeeting(info);
		tester = new HistoryRoom(info.getMeetingID());
		
		assertEquals(tester.getHistoryInfo().getMeetingID(),info.getMeetingID());
		
		info.setSubject("Mother fucker");
		tester.setHistoryInfo(info);
		assertEquals(tester.getHistoryInfo(),info);
		assertNotEquals(tester.getHistoryInfo(),DataBase.getMeetingInformation(info.getMeetingID()));
		
	}

	@Test
	public void testGetHistoryInfo() {
		//System.out.println(info.getMeetingID());
		list.createMeeting(info, user);
		list.terminateAMeeting(info);
		tester = new HistoryRoom(info.getMeetingID());
		assertEquals(tester.getHistoryInfo().getMeetingID(),info.getMeetingID());
		
	//	System.out.println(tester.getHistoryInfo().getMeetingID());
	
		
	}

}
