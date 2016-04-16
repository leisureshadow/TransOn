package ntu.csie.transon.server.connect.packet;

import ntu.csie.transon.server.connect.ServiceHandler;
import ntu.csie.transon.server.history.HistorySearcher;

public class GetHistoryPortPacketHandler extends PacketHandler {

	public GetHistoryPortPacketHandler(ServiceHandler _serviceHandler) {
		super(_serviceHandler);
	}

	@Override
	protected boolean isResponsible(Packet packet) {
		return packet.getCommand().compareTo("GetHistoryPort") == 0;
	}

	@Override
	protected void handle(Packet packet) {
		String historyId = (String)packet.getItem("historyId",String.class);
		HistorySearcher historySearcher = HistorySearcher.getInstance();
		int port = historySearcher.getHistoryPort(historyId);
		
		Packet returnPacket = new Packet("HistoryPort");
		returnPacket.addItems("port", port);
		serviceHandler.sendCommand(returnPacket);
	}

}
