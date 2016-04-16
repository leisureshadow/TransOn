package com.ntu.transon.history_room;


import com.google.gson.reflect.TypeToken;
import com.ntu.transon.AndroidApplication;
import com.ntu.transon.meeting_room.MeetingInformation;
import com.ntu.transon.meeting_room.Message;
import com.ntu.transon.connection.Packet;
import com.ntu.transon.connection.Listener;
import com.ntu.transon.meeting_room.Participation;

import java.util.Date;
import java.util.List;

/**
 * Created by Mars on 2014/12/27.
 */
public class HistoryParticipation extends Participation {

    public HistoryParticipation() {
        super(AndroidApplication.getInstance().getHistoryModuleHandler());
    }

    @Override
    protected void addListener() {

        handler.addListener("Log", new Listener() {
            @Override
            public void handle(Packet p) {
                receiveLog((List<Message>) p.getItem("log", new TypeToken<List<Message>>(){}.getType()));
            }
        });

    }





    private void receiveLog(List<Message> log) {
        getHistoryRoom().addLog(log);
    }

    public void getLog(Date endTime) {
        Packet packet = new Packet("GetLog");
        packet.addItems("endTime", endTime);
        write(packet);
    }


    // error handling
    private void write(Packet packet) {
        handler.write(packet);
    }

    private HistoryRoom getHistoryRoom() {
        return (HistoryRoom) room;
    }
    public void sendUpdateMeetingInfo(MeetingInformation meetingInformation) {
        Packet packet = new Packet("UpdateHistoryInfo");
        packet.addItems("meetingInfo", meetingInformation);
        write(packet);
    }
}
