package com.ntu.transon.meeting_room;

import com.ntu.transon.User;

import java.util.Date;

/**
 * Created by 佳芷 on 2014/12/8.
 */
public class Message {
    private User speaker;
    private String message;
    private Date timestamp;

    public Message(User speaker, String msg, Date timestamp){
        this.speaker = speaker;
        this.message = msg;
        this.timestamp = timestamp;
    }

    public String getMessage() {
        return message;
    }

    public User getSpeaker() {
        return speaker;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString(){
        return speaker + " says :\n" + message;

    }
}
