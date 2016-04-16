package ntu.csie.transon.server.connect.packet;

import ntu.csie.transon.server.connect.MeetingServiceHandler;
import ntu.csie.transon.server.connect.ServiceHandler;
import ntu.csie.transon.server.meetingroom.MeetingRoom;

public class SetAdminPacketHandler extends PacketHandler {

	public SetAdminPacketHandler(ServiceHandler _serviceHandler) {
		super(_serviceHandler);
	}

	@Override
	protected boolean isResponsible(Packet packet) {
		return packet.getCommand().compareTo("SetAdmin") == 0;
	}

	@Override
	protected void handle(Packet packet) {
		String userId = (String)packet.getItem("userId",String.class);
		MeetingRoom meetingRoom = ((MeetingServiceHandler)serviceHandler).getMeetingRoom();
		meetingRoom.setAdmin(userId);
	}

}
