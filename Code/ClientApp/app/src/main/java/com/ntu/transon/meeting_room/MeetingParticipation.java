package com.ntu.transon.meeting_room;


import android.content.Intent;

import com.google.gson.reflect.TypeToken;
import com.ntu.transon.AndroidApplication;
import com.ntu.transon.connection.Packet;
import com.ntu.transon.User;
import com.ntu.transon.activities.LobbyActivity;
import com.ntu.transon.connection.Listener;

import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * Created by Mars on 2014/12/27.
 */
public class MeetingParticipation extends Participation {

    public MeetingParticipation() {
        super(AndroidApplication.getInstance().getParticipantModuleHandler());
    }

    @Override
    protected void addListener() {
        handler.addListener("AccessRequest", new Listener() {
            @Override
            public void handle(Packet p) {
                accessRequest(p.getItem("user", User.class));
            }
        });
        handler.addListener("SetAdmin", new Listener() {
            @Override
            public void handle(Packet p) {
                setAdmin();
            }
        });
        handler.addListener("UnsetAdmin", new Listener() {
            @Override
            public void handle(Packet p) {
                unsetAdmin();
            }
        });
        handler.addListener("Kick", new Listener() {
            @Override
            public void handle(Packet p) {
                kick();
            }
        });
        handler.addListener("Mute", new Listener() {
            @Override
            public void handle(Packet p) {
                mute();
            }
        });
        handler.addListener("Unmute", new Listener() {
            @Override
            public void handle(Packet p) {
                unmute();
            }
        });
        handler.addListener("Pause", new Listener() {
            @Override
            public void handle(Packet p) {
                receivePause();
            }
        });
        handler.addListener("Continue", new Listener() {
            @Override
            public void handle(Packet p) {
                receiveContinue();
            }
        });
        handler.addListener("Terminate", new Listener() {
            @Override
            public void handle(Packet p) {
                receiveTerminate();
            }
        });
        handler.addListener("Log", new Listener() {
            @Override
            public void handle(Packet p) {
                receiveLog((List<Message>) p.getItem("log", new TypeToken<List<Message>>(){}.getType()));
            }
        });
        handler.addListener("Message", new Listener() {
            @Override
            public void handle(Packet p) {
                receiveMessage(p.getItem("message", Message.class));
            }
        });
        handler.addListener("UpdateMeetingInfo", new Listener() {
            @Override
            public void handle(Packet p) {
                receiveUpdateMeetingInfo(p.getItem("meetingInfo", MeetingInformation.class));
            }
        });
    }

    private void accessRequest(User user) {
        if (isAdmin) {
            getMeetingRoom().getAccessResquestAdapter().addUser(user);
            getMeetingRoom().showToast("Access Request");
        }
    }

    public void accessRespond(String userId, boolean acceptance) {
        if (isAdmin) {
            Packet packet = new Packet("AccessRespond");
            packet.addItems("acceptance", acceptance);
            packet.addItems("userId", userId);
            write(packet);
        }
    }

    public void leave() {
        Packet packet = new Packet("Leave");
        write(packet);

        finish();
    }

    private void kick() {
        getMeetingRoom().showToast("You are kicked.");

        finish();
    }

    public void kick(User user) {
        if (isAdmin) {
            Packet packet = new Packet("Kick");
            packet.addItems("userId", user.getId());
            write(packet);
        }
    }

    private void mute() {
        getMeetingRoom().showToast("You are muted.");

        isMute = true;
    }

    public void mute(User user) {
        if (isAdmin) {
            Packet packet = new Packet("Mute");
            packet.addItems("userId", user.getId());
            write(packet);
        }
    }

    private void unmute() {
        getMeetingRoom().showToast("You are unmuted.");

        isMute = false;
    }

    public void unmute(User user) {
        if (isAdmin) {
            Packet packet = new Packet("Unmute");
            packet.addItems("userId", user.getId());
            write(packet);
        }
    }

    private void receivePause() {
        getMeetingRoom().showToast("Meeting is paused.");

        isPause = true;
    }

    public void sendPause() {
        if (isAdmin) {
            Packet packet = new Packet("Pause");
            write(packet);
        }
    }

    private void receiveContinue() {
        getMeetingRoom().showToast("Meeting is continued.");

        isPause = false;
    }

    public void sendContinue() {
        if (isAdmin) {
            Packet packet = new Packet("Continue");
            write(packet);
        }
    }

    private void receiveTerminate() {
        getMeetingRoom().showToast("Meeting is terminated.");

        finish();
    }

    public void sendTerminate() {
        if (isAdmin) {
            Packet packet = new Packet("Terminate");
            write(packet);
        }
    }

    private void setAdmin() {
        getMeetingRoom().showToast("You are admin now.");

        isAdmin = true;
        getMeetingRoom().notifyIsAdmin(true);
    }

    public void setAdmin(User user) {
        if (isAdmin) {
            Packet packet = new Packet("SetAdmin");
            packet.addItems("userId", user.getId());
            write(packet);
        }
    }

    private void unsetAdmin() {
        getMeetingRoom().showToast("You are not admin now.");

        isAdmin = false;
        getMeetingRoom().notifyIsAdmin(false);
    }

    public void unsetAdmin(User user) {
        if (isAdmin) {
            Packet packet = new Packet("UnsetAdmin");
            packet.addItems("userId", user.getId());
            write(packet);
        }
    }

    private void receiveLog(List<Message> log) {
        getMeetingRoom().addLog(log);
    }

    public void getLog(Date endTime) {
        Packet packet = new Packet("GetLog");
        packet.addItems("endTime", endTime);
        write(packet);
    }

    private void receiveMessage(Message message) {
        getMeetingRoom().addMessage(message);
    }

    public void sendMessage(String message) {
        if (!isMute && !isPause) {
            Packet packet = new Packet("SendMessage");
            packet.addItems("message", message);
            write(packet);
        }
    }

    private void receiveUpdateMeetingInfo(MeetingInformation meetingInformation) {
        getMeetingRoom().setMeetingInformation(meetingInformation);
    }

    public void sendUpdateMeetingInfo(MeetingInformation meetingInformation) {
        Packet packet = new Packet("UpdateMeetingInfo");
        packet.addItems("meetingInfo", meetingInformation);
        write(packet);
    }

    // error handling
    private void write(Packet packet) {
        handler.write(packet);
    }

    private void finish() {
        try {
            handler.disconnect();
        } catch (IOException e) {
            e.printStackTrace();
        }

        AndroidApplication application = AndroidApplication.getInstance();
        Intent intent = new Intent(application, LobbyActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        application.startActivity(intent);
        getMeetingRoom().getMeetingRoomActivity().finish();
    }

    private MeetingRoom getMeetingRoom() {
        return (MeetingRoom) room;
    }
}
