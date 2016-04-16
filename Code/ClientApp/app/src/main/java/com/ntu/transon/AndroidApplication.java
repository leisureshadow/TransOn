package com.ntu.transon;

import android.app.Application;

import com.ntu.transon.connection.HistoryModuleHandler;
import com.ntu.transon.connection.LoginHandler;
import com.ntu.transon.connection.ParticipantModuleHandler;
import com.ntu.transon.connection.Reactor;
import com.ntu.transon.connection.ServiceHandler;
import com.ntu.transon.meeting_room.MeetingRoom;
import com.ntu.transon.meeting_room.Room;


import java.io.IOException;

/**
 * Created by User on 2014/12/30.
 */
public class AndroidApplication extends Application{

    private static AndroidApplication sInstance;
    private LoginHandler loginHandler;
    private ParticipantModuleHandler participantModuleHandler;
    private HistoryModuleHandler historyModuleHandler;
    private Reactor reactor;
    private User user;
    private Room room;
    private Thread reactorThread;


    public static AndroidApplication getInstance(){
        return sInstance;
    }

    @Override
    public void onCreate(){
        super.onCreate();
        sInstance = this;
        sInstance.initializeInstance();
//        startReactor();
    }

    protected void initializeInstance(){
        try {
            reactor = new Reactor();
        } catch (IOException e) {
            e.printStackTrace();
        }
        loginHandler = new LoginHandler(reactor,"LoginHandler");
        participantModuleHandler = new ParticipantModuleHandler(reactor,"ParticipantModuleHandler");
        historyModuleHandler = new HistoryModuleHandler(reactor," HistoryModuleHandler");
    }


    public void startReactor(){
        reactorThread = new Thread(reactor);
        reactorThread.start();
    }

    public void stopReactor(){
        reactorThread .interrupt();
        reactor.setTerminate(true);

    }
    public LoginHandler getLoginHandler(){return loginHandler;}
    public ParticipantModuleHandler getParticipantModuleHandler(){return participantModuleHandler;}
    public HistoryModuleHandler getHistoryModuleHandler(){return historyModuleHandler;}

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }
}
