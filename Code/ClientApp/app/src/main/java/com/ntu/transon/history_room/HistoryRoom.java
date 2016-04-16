package com.ntu.transon.history_room;

import com.ntu.transon.meeting_room.Message;
import com.ntu.transon.meeting_room.MessageAdapter;
import com.ntu.transon.meeting_room.MeetingInformation;
import com.ntu.transon.meeting_room.Room;

/**
 * Created by Mars on 2015/1/1.
 */
public class HistoryRoom extends Room {

    public HistoryRoom(MessageAdapter messageAdapter, MeetingInformation meetingInformation) {
        super(messageAdapter, meetingInformation, new HistoryParticipation());
    }

    // TODO may have performance issue
    public void addMessage(Message message) {
        messages.add(message);
        sortMessages();
        notifyAdapter();
    }
}
