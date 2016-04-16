package ntu.csie.transon.server.connect.packet;

import ntu.csie.transon.server.connect.ClientServiceHandler;
import ntu.csie.transon.server.connect.ServiceHandler;
import ntu.csie.transon.server.databean.MeetingInformation;
import ntu.csie.transon.server.databean.User;
import ntu.csie.transon.server.meetingroom.MeetingList;

public class CreatePacketHandler extends PacketHandler {

	public CreatePacketHandler(ServiceHandler _serviceHandler) {
		super(_serviceHandler);
	}

	@Override
	protected boolean isResponsible(Packet packet) {
		return packet.getCommand().compareTo("Create") == 0;
	}

	@Override
	protected void handle(Packet packet) {
		MeetingInformation meetingInfo = (MeetingInformation)packet.getItem("meetingInfo",MeetingInformation.class);
		User user = ((ClientServiceHandler)serviceHandler).getUser();
		MeetingList meetingList = MeetingList.getInstance();
		int port = meetingList.createMeeting(meetingInfo,user);
		
		Packet returnPacket = new Packet("MeetingPort");
		returnPacket.addItems("port", port);
		serviceHandler.sendCommand(returnPacket);
	}

}
