package ntu.csie.transon.server.connect.packet;

import java.util.Date;

import ntu.csie.transon.server.connect.HistoryServiceHandler;
import ntu.csie.transon.server.connect.ServiceHandler;
import ntu.csie.transon.server.history.HistoryRoom;

public class GetHistoryLogPacketHandler extends PacketHandler {

	public GetHistoryLogPacketHandler(ServiceHandler _serviceHandler) {
		super(_serviceHandler);
	}

	@Override
	protected boolean isResponsible(Packet packet) {
		return packet.getCommand().compareTo("GetLog") == 0;
	}

	@Override
	protected void handle(Packet packet) {
		HistoryServiceHandler historySH = (HistoryServiceHandler)serviceHandler; 
		Date date = (Date)packet.getItem("endTime",Date.class);
		HistoryRoom historyRoom = ((HistoryServiceHandler)serviceHandler).getHistoryRoom();
		String userId = historySH.getUser().getId();
		historyRoom.getLog(userId,date);
	}

}
