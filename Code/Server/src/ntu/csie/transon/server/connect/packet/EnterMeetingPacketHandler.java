package ntu.csie.transon.server.connect.packet;

import ntu.csie.transon.server.connect.MeetingServiceHandler;
import ntu.csie.transon.server.connect.ServiceHandler;
import ntu.csie.transon.server.meetingroom.MeetingRoom;

public class EnterMeetingPacketHandler extends PacketHandler {

	public EnterMeetingPacketHandler(ServiceHandler _serviceHandler) {
		super(_serviceHandler);
	}

	@Override
	protected boolean isResponsible(Packet packet) {
		return packet.getCommand().compareTo("EnterMeeting") == 0;
	}

	@Override
	protected void handle(Packet packet) {
		String userId = (String)packet.getItem("userId",String.class);
		MeetingServiceHandler meetingSH = (MeetingServiceHandler)serviceHandler;
		MeetingRoom meetingRoom = meetingSH.getMeetingRoom();
		if(meetingRoom.inBlacklist(userId)){
			meetingRoom.reject(userId);
		}else if((!meetingRoom.getInformation().is_private()) ||
				  meetingRoom.inParticipant(userId) ||
				  meetingRoom.inAdmin(userId)){
			//Participant will be set in this step
			meetingRoom.participate(userId, meetingSH);
		}else{
			meetingRoom.sendAcceptRequest(userId, (MeetingServiceHandler)serviceHandler);
		}
	}
}
