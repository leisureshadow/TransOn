package ntu.csie.transon.server.history;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.Date;
import java.util.Hashtable;

import ntu.csie.transon.db.DataBase;
import ntu.csie.transon.server.connect.HistoryServiceHandler;
import ntu.csie.transon.server.databean.MeetingInformation;
import ntu.csie.transon.server.databean.User;
import ntu.csie.transon.server.meetingroom.MeetingList;

import org.junit.BeforeClass;
import org.junit.Test;

public class HistorySearcherTest {

	private static HistorySearcher hsr;
	private static User user;
	private Hashtable <User, HistoryServiceHandler> userList;
	private static MeetingList list;
	private static MeetingInformation info;
	private static HistoryRoom tester;
//	private DataBase db;
	private static HistoryServiceHandler hs;
	@BeforeClass
	public static void setup() throws IOException{
		list = MeetingList.getInstance();
		info = new MeetingInformation ("test", "subject", "305", new Date (), new Date (),
				"des", "init", 100, 100, false, false,
				false);
		tester = new HistoryRoom("test");
		
		SocketChannel s =  SocketChannel.open();
		hs = new HistoryServiceHandler(s, tester);
		user = new User("ua","ub","uc");
		DataBase.deleteDataBase("WTF");
		DataBase.createDataBase("WTF");
		
		}
	@Test
	public void testGetInstance() {
		hsr = HistorySearcher.getInstance();
		assertSame(hsr,HistorySearcher.getInstance());
	}

	@Test
	public void testGetReadableHistoryList() {
		//ASK GG
		list.createMeeting(info, user);
		list.terminateAMeeting(info);
		hsr = HistorySearcher.getInstance();
		System.out.println(hsr.getReadableHistoryList(user.getId()));
		assertNotNull(hsr.getReadableHistoryList(user.getId()));
	}

	@Test
	public void testGetHistoryPort() {
		hsr = HistorySearcher.getInstance();
		list.createMeeting(info, user);
		list.terminateAMeeting(info);
		assertEquals(hsr.getHistoryPort(info.getMeetingID()), hsr.getHistoryPort(info.getMeetingID()));
	}

}
