package ntu.csie.transon.server.connect;

import java.nio.channels.SocketChannel;

import ntu.csie.transon.server.connect.packet.EnterHistoryPacketHandler;
import ntu.csie.transon.server.connect.packet.GetHistoryLogPacketHandler;
import ntu.csie.transon.server.connect.packet.LeavePacketHandler;
import ntu.csie.transon.server.connect.packet.PacketHandler;
import ntu.csie.transon.server.connect.packet.SetHistoryInfoPacketHandler;
import ntu.csie.transon.server.connect.packet.UpdateHistoryInfoPacketHandler;
import ntu.csie.transon.server.history.HistoryRoom;

public class HistoryServiceHandlerFactory implements ServiceHandlerFactory {

	private HistoryRoom historyRoom;
	
	public HistoryServiceHandlerFactory(HistoryRoom _historyRoom){
		historyRoom = _historyRoom;
	}
	
	@Override
	public ServiceHandler createServiceHandler(SocketChannel handle) {
		ServiceHandler serviceHandler = new HistoryServiceHandler(handle,historyRoom);
		PacketHandler packetHandler = createPacketHandlerChain(serviceHandler);
		serviceHandler.setPacketHandler(packetHandler);
		return serviceHandler;
	}

	@Override
	public PacketHandler createPacketHandlerChain(ServiceHandler serviceHandler) {
		PacketHandler enterHistory = new EnterHistoryPacketHandler(serviceHandler);
		PacketHandler setHistoryInfo = new SetHistoryInfoPacketHandler(serviceHandler);
		PacketHandler getHistoryLog = new GetHistoryLogPacketHandler(serviceHandler);
		PacketHandler leave = new LeavePacketHandler(serviceHandler);
		PacketHandler updateHistoryInfo = new UpdateHistoryInfoPacketHandler(serviceHandler);
		
		enterHistory.setNextPacketHandler(setHistoryInfo);
		setHistoryInfo.setNextPacketHandler(getHistoryLog);
		getHistoryLog.setNextPacketHandler(leave);
		leave.setNextPacketHandler(updateHistoryInfo);
		return enterHistory;
	}

}
