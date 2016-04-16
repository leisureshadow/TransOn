package ntu.csie.transon.server.connect.packet;

import ntu.csie.transon.server.connect.MeetingServiceHandler;
import ntu.csie.transon.server.connect.ServiceHandler;
import ntu.csie.transon.server.meetingroom.MeetingRoom;

public class ContinuePacketHandler extends PacketHandler {

	public ContinuePacketHandler(ServiceHandler _serviceHandler) {
		super(_serviceHandler);
	}

	@Override
	protected boolean isResponsible(Packet packet) {
		return packet.getCommand().compareTo("Continue") == 0;
	}

	@Override
	protected void handle(Packet packet) {
		MeetingRoom meetingRoom = ((MeetingServiceHandler)serviceHandler).getMeetingRoom();
		meetingRoom._continue();
	}

}
