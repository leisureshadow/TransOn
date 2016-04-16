package ntu.csie.transon.server.connect.packet;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import ntu.csie.transon.server.connect.MeetingServiceHandler;
import ntu.csie.transon.server.connect.ServiceHandler;
import ntu.csie.transon.server.connect.packet.LeavePacketHandler;
import ntu.csie.transon.server.connect.packet.Packet;
import ntu.csie.transon.server.connect.packet.PacketHandler;
import ntu.csie.transon.server.databean.MeetingInformation;
import ntu.csie.transon.server.databean.User;
import ntu.csie.transon.server.meetingroom.MeetingRoom;

import org.junit.Test;

public class LeavePacketHandlerTest {

	@Test
	public void testHandle() {
		Packet fakePacket = new Packet("Leave");
		MeetingInformation meetingInformation = new MeetingInformation("fakeId","fakeSubject","fakeLocation",
				                                                       new Date(),new Date(),"fakeDescription",
				                                                       "fakeInitiate",0,0,false,false,false);
		MeetingRoom meetingRoom = new MeetingRoom(meetingInformation);
		MeetingServiceHandler serviceHandler = new MeetingServiceHandler(null, meetingRoom);
		PacketHandler handler = new LeavePacketHandler(null);
		
	}

}