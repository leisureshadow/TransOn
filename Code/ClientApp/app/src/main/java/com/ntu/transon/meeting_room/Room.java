package com.ntu.transon.meeting_room;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Mars on 2015/1/4.
 */
public class Room {
    protected MeetingInformation meetingInformation;
    protected ArrayList<Message> messages = new ArrayList<Message>();
    protected Participation participation;
    protected MessageAdapter messageAdapter;

    public Room(MessageAdapter messageAdapter, MeetingInformation meetingInformation, Participation participation) {
        this.messageAdapter = messageAdapter;
        this.meetingInformation = meetingInformation;
        this.participation = participation;
        participation.setMeetingRoom(this);
    }

    public MeetingInformation getMeetingInformation() {
        return meetingInformation;
    }

    public ArrayList<Message> getMessages() {
        return messages;
    }

    public Participation getParticipation() {
        return participation;
    }

    public void setMeetingInformation(MeetingInformation meetingInformation) {
        this.meetingInformation = meetingInformation;
    }

    // TODO may have performance issue
    public void addLog(List<Message> log) {
        messages.addAll(log);
        sortMessages();
        notifyAdapter();
    }

    protected void sortMessages() {
        Collections.sort(messages, new MessageComparator());
    }

    public class MessageComparator implements Comparator<Message> {
        @Override
        public int compare(Message lhs, Message rhs) {
            // TODO may change after timestamp change
            return lhs.getTimestamp().compareTo(rhs.getTimestamp());
        }
    }

    protected void notifyAdapter() {
        messageAdapter.setMessages(messages);
    }
}
