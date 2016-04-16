package ntu.csie.transon.server.connect.packet;

import ntu.csie.transon.server.connect.MeetingServiceHandler;
import ntu.csie.transon.server.connect.ServiceHandler;
import ntu.csie.transon.server.meetingroom.MeetingRoom;

public class AccessRespondPacketHandler extends PacketHandler {

	public AccessRespondPacketHandler(ServiceHandler _serviceHandler) {
		super(_serviceHandler);
	}

	@Override
	protected boolean isResponsible(Packet packet) {
		return packet.getCommand().compareTo("AccessRespond") == 0;
	}

	@Override
	protected void handle(Packet packet) {
		Boolean acceptance = (Boolean)packet.getItem("acceptance",Boolean.class);
		String userId = (String)packet.getItem("userId",String.class);
		
		MeetingServiceHandler meetingSH = (MeetingServiceHandler) serviceHandler;
		MeetingRoom meetingRoom = meetingSH.getMeetingRoom();
		if(acceptance){
			meetingRoom.accept(userId);
		}else{
			meetingRoom.reject(userId);
		}
	}

}
