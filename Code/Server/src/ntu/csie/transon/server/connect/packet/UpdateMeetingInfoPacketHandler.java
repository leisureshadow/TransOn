package ntu.csie.transon.server.connect.packet;

import ntu.csie.transon.server.connect.MeetingServiceHandler;
import ntu.csie.transon.server.connect.ServiceHandler;
import ntu.csie.transon.server.databean.MeetingInformation;
import ntu.csie.transon.server.meetingroom.MeetingRoom;

public class UpdateMeetingInfoPacketHandler extends PacketHandler {

	public UpdateMeetingInfoPacketHandler(ServiceHandler _serviceHandler) {
		super(_serviceHandler);
	}

	@Override
	protected boolean isResponsible(Packet packet) {
		return packet.getCommand().compareTo("UpdateMeetingInfo") == 0;
	}

	@Override
	protected void handle(Packet packet) {
		MeetingInformation meetingInformation = (MeetingInformation)packet.getItem("meetingInfo", MeetingInformation.class);
		MeetingRoom meetingRoom = ((MeetingServiceHandler)serviceHandler).getMeetingRoom();
		meetingRoom.updateMeetingInfo(meetingInformation);
	}

}
