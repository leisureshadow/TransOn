package ntu.csie.transon.server.meetingroom;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.Set;

import ntu.csie.transon.server.databean.MeetingInformation;
import ntu.csie.transon.server.databean.User;
import ntu.csie.transon.db.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

public class MeetingListTest {

	private static MeetingList list;
	private static MeetingInformation info;
	private static User user;
	private DataBase db;
	@BeforeClass
	public static void setup(){
		list = MeetingList.getInstance();
		info = new MeetingInformation ("test", "subject", "305", new Date (), new Date (),
				"des", "init", 25.0194250,121.5415430, false, false,
				false);
		user = new User("ua","ub","uc");
		DataBase.deleteDataBase("mfk");
		DataBase.createDataBase("mfk");
		DataBase.createUserInformation(user);
	}
	@Test
	public void testGetInstance() {
		assertSame(list, MeetingList.getInstance());
	}

	@Test
	public void testGetMeetingPort() {
	//	fail("NOO");
		list.createMeeting(info, user);
		assertNotNull(list.getMeetingPort(info.getMeetingID()));
	}

	@Test
	public void testCreateMeeting() {
		assertEquals(list.createMeeting(info, user), list.getMeetingPort(info.getMeetingID()));
	}

	@Test
	public void testTerminateAMeeting() {
		list.createMeeting(info, user);
		String s1 = info.getMeetingID();
		list.terminateAMeeting(info);
		list.createMeeting(info, user);
		String s2 = info.getMeetingID();
		list.terminateAMeeting(info);
		assertNotEquals(s1,s2);		
	}

	@Test
	public void testGetNearbyMeetings() {
		list.createMeeting(info, user);
		assertNotNull(DataBase.getNearbyMeeting(25.0194,121.542));
	}
	

}
