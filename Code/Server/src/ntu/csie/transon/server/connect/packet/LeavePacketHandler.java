package ntu.csie.transon.server.connect.packet;

import ntu.csie.transon.server.connect.MeetingServiceHandler;
import ntu.csie.transon.server.connect.ServiceHandler;
import ntu.csie.transon.server.meetingroom.MeetingRoom;

public class LeavePacketHandler extends PacketHandler {

	public LeavePacketHandler(ServiceHandler _serviceHandler) {
		super(_serviceHandler);
	}

	@Override
	protected boolean isResponsible(Packet packet) {
		return packet.getCommand().compareTo("Leave") == 0;
	}

	@Override
	protected void handle(Packet packet) {
		MeetingServiceHandler meetingSH = (MeetingServiceHandler)serviceHandler;
		String userId = meetingSH.getParticipant().getUser().getId();
		MeetingRoom meetingRoom = meetingSH.getMeetingRoom();
		meetingRoom.leave(userId);
	}

}
