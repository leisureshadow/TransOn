package ntu.csie.transon.server.connect.packet;

import java.util.ArrayList;
import java.util.Date;

import ntu.csie.transon.server.connect.MeetingServiceHandler;
import ntu.csie.transon.server.connect.ServiceHandler;
import ntu.csie.transon.server.databean.Message;
import ntu.csie.transon.server.meetingroom.MeetingRoom;

public class GetMeetingLogPacketHandler extends PacketHandler {

	public GetMeetingLogPacketHandler(ServiceHandler _serviceHandler) {
		super(_serviceHandler);
	}

	@Override
	protected boolean isResponsible(Packet packet) {
		return packet.getCommand().compareTo("GetLog") == 0;
	}

	@Override
	protected void handle(Packet packet) {
		Date date = (Date)packet.getItem("endTime",Date.class);
		MeetingRoom meetingRoom = ((MeetingServiceHandler)serviceHandler).getMeetingRoom();
		ArrayList<Message> log = meetingRoom.getLog(date);
		
		Packet returnPacket = new Packet("Log");
		returnPacket.addItems("log", log);
		serviceHandler.sendCommand(returnPacket);
	}

}
