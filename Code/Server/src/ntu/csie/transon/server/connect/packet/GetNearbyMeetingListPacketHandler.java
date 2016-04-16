package ntu.csie.transon.server.connect.packet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import ntu.csie.transon.server.connect.ServiceHandler;
import ntu.csie.transon.server.databean.MeetingInformation;
import ntu.csie.transon.server.meetingroom.MeetingList;

public class GetNearbyMeetingListPacketHandler extends PacketHandler {

	public GetNearbyMeetingListPacketHandler(ServiceHandler _serviceHandler) {
		super(_serviceHandler);
	}

	@Override
	protected boolean isResponsible(Packet packet) {
		return packet.getCommand().compareTo("GetNearbyMeetingList") == 0;
	}

	@Override
	protected void handle(Packet packet) {
		Double latitude = (Double)packet.getItem("latitude",Double.class);
		Double longitude = (Double)packet.getItem("longitude",Double.class);
		MeetingList meetingList = MeetingList.getInstance();
		ArrayList<MeetingInformation> meetings = meetingList.getNearbyMeetings(latitude,longitude);
		Iterator<MeetingInformation> iter = meetings.iterator();
		
		Packet returnPacket = new Packet("NearbyMeetingList");
		ArrayList<HashMap<String,Object>> item = new ArrayList<HashMap<String,Object>>(0);
		while(iter.hasNext()){
			HashMap<String,Object> briefMeetingInfo = new HashMap<String,Object>(0);
			MeetingInformation meetingInfo = iter.next();
			briefMeetingInfo.put("subject", meetingInfo.getSubject());
			briefMeetingInfo.put("location", meetingInfo.getLocation());
			briefMeetingInfo.put("meetingId", meetingInfo.getMeetingID());
			item.add(briefMeetingInfo);
		}
		returnPacket.addItems("meetingList", item);
		serviceHandler.sendCommand(returnPacket);
	}

}
