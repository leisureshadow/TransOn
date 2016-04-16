package ntu.csie.transon.server.connect.packet;

import ntu.csie.transon.server.connect.MeetingServiceHandler;
import ntu.csie.transon.server.connect.ServiceHandler;
import ntu.csie.transon.server.databean.User;
import ntu.csie.transon.server.meetingroom.MeetingRoom;

public class SendMessagePacketHandler extends PacketHandler {

	public SendMessagePacketHandler(ServiceHandler _serviceHandler) {
		super(_serviceHandler);
	}

	@Override
	protected boolean isResponsible(Packet packet) {
		return packet.getCommand().compareTo("SendMessage") == 0;
	}

	@Override
	protected void handle(Packet packet) {
		String message = (String)packet.getItem("message",String.class);
		MeetingServiceHandler meetingSH = (MeetingServiceHandler)serviceHandler;
		MeetingRoom meetingRoom = meetingSH.getMeetingRoom();
		User user = meetingSH.getParticipant().getUser();
		meetingRoom.sendMessage(message, user);
	}

}
