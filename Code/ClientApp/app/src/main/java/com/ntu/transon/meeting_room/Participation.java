package com.ntu.transon.meeting_room;

import com.ntu.transon.AndroidApplication;
import com.ntu.transon.User;
import com.ntu.transon.connection.ServiceHandler;

/**
 * Created by Mars on 2015/1/4.
 */
public abstract class Participation {
    protected boolean isAdmin;
    protected boolean isMute;
    protected boolean isPause;
    protected Room room;
    private User user = AndroidApplication.getInstance().getUser();
    protected ServiceHandler handler;

    protected Participation(ServiceHandler handler) {
        this.handler = handler;
        addListener();
    }

    public void setMeetingRoom(Room room) {
        this.room = room;
        for (User admin : room.getMeetingInformation().getAdministrators()) {
            if (admin.getId().equals(user.getId())) {
                isAdmin = true;
                break;
            }
        }
    }

    protected abstract void addListener();

    public boolean isPause() {
        return isPause;
    }

    public boolean isMute() {
        return isMute;
    }

    public boolean isAdmin() {
        return isAdmin;
    }
}
