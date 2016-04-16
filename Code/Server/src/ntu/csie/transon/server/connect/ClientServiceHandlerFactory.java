package ntu.csie.transon.server.connect;

import java.nio.channels.SocketChannel;

import ntu.csie.transon.server.connect.packet.CreatePacketHandler;
import ntu.csie.transon.server.connect.packet.GetHistoryPortPacketHandler;
import ntu.csie.transon.server.connect.packet.GetMeetingPortPacketHandler;
import ntu.csie.transon.server.connect.packet.GetNearbyMeetingListPacketHandler;
import ntu.csie.transon.server.connect.packet.GetReadableHistoryListPacketHandler;
import ntu.csie.transon.server.connect.packet.LoginPacketHandler;
import ntu.csie.transon.server.connect.packet.PacketHandler;

public class ClientServiceHandlerFactory implements ServiceHandlerFactory {

	
	public ServiceHandler createServiceHandler(SocketChannel handle) {
		ServiceHandler serviceHandler = new ClientServiceHandler(handle);
		PacketHandler packetHandler = createPacketHandlerChain(serviceHandler);
		serviceHandler.setPacketHandler(packetHandler);
		return serviceHandler;
	}

	
	public PacketHandler createPacketHandlerChain(ServiceHandler serviceHandler) {
		PacketHandler getNearbyMeetingList = new GetNearbyMeetingListPacketHandler(serviceHandler);
		PacketHandler getMeetingPort = new GetMeetingPortPacketHandler(serviceHandler);
		PacketHandler create = new CreatePacketHandler(serviceHandler);
		PacketHandler getReadableHistoryList = new GetReadableHistoryListPacketHandler(serviceHandler);
		PacketHandler getHistoryPort = new GetHistoryPortPacketHandler(serviceHandler);
		PacketHandler login = new LoginPacketHandler(serviceHandler);
		
		getNearbyMeetingList.setNextPacketHandler(getMeetingPort);
		getMeetingPort.setNextPacketHandler(create);
		create.setNextPacketHandler(getReadableHistoryList);
		getReadableHistoryList.setNextPacketHandler(getHistoryPort);
		getHistoryPort.setNextPacketHandler(login);
		return getNearbyMeetingList;
	}

}
