package ntu.csie.transon.server.connect.packet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import ntu.csie.transon.server.connect.ServiceHandler;
import ntu.csie.transon.server.connect.ClientServiceHandler;
import ntu.csie.transon.server.databean.MeetingInformation;
import ntu.csie.transon.server.databean.User;
import ntu.csie.transon.server.history.HistorySearcher;

public class GetReadableHistoryListPacketHandler extends PacketHandler {

	public GetReadableHistoryListPacketHandler(ServiceHandler _serviceHandler) {
		super(_serviceHandler);
	}

	@Override
	protected boolean isResponsible(Packet packet) {
		return packet.getCommand().compareTo("GetReadableHistoryList") == 0;
	}

	@Override
	protected void handle(Packet packet) {
		User user = ((ClientServiceHandler)serviceHandler).getUser();
		HistorySearcher historySearcher = HistorySearcher.getInstance();
		ArrayList<MeetingInformation> history = historySearcher.getReadableHistoryList(user.getId());

		Iterator<MeetingInformation> iter = history.iterator();

		Packet returnPacket = new Packet("ReadableHistoryList");
		ArrayList<HashMap<String,Object>> item = new ArrayList<HashMap<String,Object>>(0);

		while(iter.hasNext()){
			HashMap<String,Object> briefMeetingInfo = new HashMap<String,Object>(0);
			MeetingInformation meetingInfo = iter.next();
			briefMeetingInfo.put("subject", meetingInfo.getSubject());
			briefMeetingInfo.put("location", meetingInfo.getLocation());
			briefMeetingInfo.put("meetingId", meetingInfo.getMeetingID());
			item.add(briefMeetingInfo);
		}

		returnPacket.addItems("historyList", item);
		serviceHandler.sendCommand(returnPacket);
		
	}

}
