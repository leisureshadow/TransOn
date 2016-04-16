package ntu.csie.transon.server.connect.packet;

import ntu.csie.transon.server.connect.HistoryServiceHandler;
import ntu.csie.transon.server.connect.ServiceHandler;
import ntu.csie.transon.server.history.HistoryRoom;

public class EnterHistoryPacketHandler extends PacketHandler {

	public EnterHistoryPacketHandler(ServiceHandler _serviceHandler) {
		super(_serviceHandler);
	}

	@Override
	protected boolean isResponsible(Packet packet) {
		return packet.getCommand().compareTo("EnterHistory") == 0;
	}

	@Override
	protected void handle(Packet packet) {
		String userId = (String)packet.getItem("userId",String.class);
		HistoryServiceHandler historySH = (HistoryServiceHandler)serviceHandler;
		HistoryRoom historyRoom = historySH.getHistoryRoom();
		
		historyRoom.enter(userId,historySH);
	}

}
