package ntu.csie.transon.server.connect.packet;

import ntu.csie.transon.server.connect.HistoryServiceHandler;
import ntu.csie.transon.server.connect.ServiceHandler;
import ntu.csie.transon.server.databean.MeetingInformation;
import ntu.csie.transon.server.history.HistoryRoom;

public class SetHistoryInfoPacketHandler extends PacketHandler {

	public SetHistoryInfoPacketHandler(ServiceHandler _serviceHandler) {
		super(_serviceHandler);
	}

	@Override
	protected boolean isResponsible(Packet packet) {
		return packet.getCommand().compareTo("SetHistoryInfo") == 0;
	}

	@Override
	protected void handle(Packet packet) {
		MeetingInformation historyInfo = (MeetingInformation)packet.getItem("historyInfo",MeetingInformation.class);
		HistoryRoom historyRoom = ((HistoryServiceHandler)serviceHandler).getHistoryRoom();
		historyRoom.setHistoryInfo(historyInfo);
	}

}
