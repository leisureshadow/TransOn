package ntu.csie.transon.server.connect;

import java.nio.channels.SocketChannel;

import ntu.csie.transon.server.connect.packet.AccessRespondPacketHandler;
import ntu.csie.transon.server.connect.packet.ContinuePacketHandler;
import ntu.csie.transon.server.connect.packet.EnterMeetingPacketHandler;
import ntu.csie.transon.server.connect.packet.GetMeetingLogPacketHandler;
import ntu.csie.transon.server.connect.packet.KickPacketHandler;
import ntu.csie.transon.server.connect.packet.LeavePacketHandler;
import ntu.csie.transon.server.connect.packet.MutePacketHandler;
import ntu.csie.transon.server.connect.packet.PacketHandler;
import ntu.csie.transon.server.connect.packet.PausePacketHandler;
import ntu.csie.transon.server.connect.packet.SendMessagePacketHandler;
import ntu.csie.transon.server.connect.packet.SetAdminPacketHandler;
import ntu.csie.transon.server.connect.packet.TerminatePacketHandler;
import ntu.csie.transon.server.connect.packet.UnmutePacketHandler;
import ntu.csie.transon.server.connect.packet.UnsetAdminPacketHandler;
import ntu.csie.transon.server.connect.packet.UpdateMeetingInfoPacketHandler;
import ntu.csie.transon.server.meetingroom.MeetingRoom;

public class MeetingServiceHandlerFactory implements ServiceHandlerFactory {

	private MeetingRoom meetingRoom;
	
	public MeetingServiceHandlerFactory(MeetingRoom _meetingRoom){
		meetingRoom = _meetingRoom;
	}
	
	@Override
	public ServiceHandler createServiceHandler(SocketChannel handle) {
		ServiceHandler serviceHandler = new MeetingServiceHandler(handle,meetingRoom);
		PacketHandler packetHandler = createPacketHandlerChain(serviceHandler);
		serviceHandler.setPacketHandler(packetHandler);
		return serviceHandler;
	}

	@Override
	public PacketHandler createPacketHandlerChain(ServiceHandler serviceHandler) {
		PacketHandler enterMeeting = new EnterMeetingPacketHandler(serviceHandler);
		PacketHandler accessRespond = new AccessRespondPacketHandler(serviceHandler);
		PacketHandler setAdmin = new SetAdminPacketHandler(serviceHandler);
		PacketHandler unsetAdmin = new UnsetAdminPacketHandler(serviceHandler);
		PacketHandler kick = new KickPacketHandler(serviceHandler);
		PacketHandler mute = new MutePacketHandler(serviceHandler);
		PacketHandler unmute = new UnmutePacketHandler(serviceHandler);
		PacketHandler pause = new PausePacketHandler(serviceHandler);
		PacketHandler kontinue = new ContinuePacketHandler(serviceHandler);
		PacketHandler terminate = new TerminatePacketHandler(serviceHandler);
		PacketHandler leave = new LeavePacketHandler(serviceHandler);
		PacketHandler getMeetingLog = new GetMeetingLogPacketHandler(serviceHandler);
		PacketHandler sendMessage = new SendMessagePacketHandler(serviceHandler);
		PacketHandler updateMeetingInfo = new UpdateMeetingInfoPacketHandler(serviceHandler); 
		
		enterMeeting.setNextPacketHandler(accessRespond);
		accessRespond.setNextPacketHandler(setAdmin);
		setAdmin.setNextPacketHandler(unsetAdmin);
		unsetAdmin.setNextPacketHandler(kick);
		kick.setNextPacketHandler(mute);
		mute.setNextPacketHandler(unmute);
		unmute.setNextPacketHandler(pause);
		pause.setNextPacketHandler(kontinue);
		kontinue.setNextPacketHandler(terminate);
		terminate.setNextPacketHandler(leave);
		leave.setNextPacketHandler(getMeetingLog);
		getMeetingLog.setNextPacketHandler(sendMessage);
		sendMessage.setNextPacketHandler(updateMeetingInfo);
		return enterMeeting;
	}

}
