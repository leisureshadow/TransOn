package ntu.csie.transon.server.connect.packet;

import ntu.csie.transon.server.connect.ServiceHandler;
import ntu.csie.transon.server.meetingroom.MeetingList;

public class GetMeetingPortPacketHandler extends PacketHandler {

	public GetMeetingPortPacketHandler(ServiceHandler _serviceHandler) {
		super(_serviceHandler);
	}

	@Override
	protected boolean isResponsible(Packet packet) {
		return packet.getCommand().compareTo("GetMeetingPort") == 0;
	}

	@Override
	protected void handle(Packet packet) {
		String meetingId = (String)packet.getItem("meetingId",String.class);
		MeetingList meetingList = MeetingList.getInstance();
		int port = meetingList.getMeetingPort(meetingId);
		Packet returnPacket = new Packet("MeetingPort");
		returnPacket.addItems("port", port);
		serviceHandler.sendCommand(returnPacket);
	}

}
