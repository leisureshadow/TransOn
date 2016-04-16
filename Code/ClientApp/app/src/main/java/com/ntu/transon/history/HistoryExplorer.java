package com.ntu.transon.history;

import android.util.Log;

import com.google.gson.reflect.TypeToken;
import com.ntu.transon.AndroidApplication;
import com.ntu.transon.connection.Connector;
import com.ntu.transon.connection.Listener;
import com.ntu.transon.connection.Packet;
import com.ntu.transon.connection.ServiceHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by 佳芷 on 2014/12/26.
 */
public class HistoryExplorer {
    private List<HistorySearchItem> readableHistoryList;
    private int historyPort;
    private HistoryExploreFragment historyExploreView;

    public HistoryExplorer(HistoryExploreFragment historyExploreView){
        this.historyExploreView = historyExploreView;
        addListeners();
    }
    private void addListeners(){
        ServiceHandler loginHandler = AndroidApplication.getInstance().getLoginHandler();
        loginHandler.addListener("ReadableHistoryList",new Listener() {
            @Override
            public void handle(Packet p) {
                setReadableHistoryList(p);
            }
        });
        loginHandler.addListener("HistoryPort",new Listener() {
            @Override
            public void handle(Packet p) {
                handleHistoryPort(p);
                enterHistory();
            }
        });
    }


    private void setReadableHistoryList(Packet pkt){
        readableHistoryList = new ArrayList<HistorySearchItem>();
        //HashMap readableHistory = new HashMap<String,String>();
       ArrayList<HashMap<String,String>> historyList =
               (ArrayList<HashMap<String,String>>) pkt.getItem("historyList", new TypeToken<ArrayList<HashMap<String,String>>>(){}.getType());
        for(HashMap meeting:historyList){
            //need to be modified...
            HistorySearchItem historyItem = new HistorySearchItem((String)meeting.get("meetingId"),(String)meeting.get("subject"),(String)meeting.get("loaction"));
            readableHistoryList.add(historyItem);
        }
        //meetingSearchView.showNearbyMeetings(ongoingMeetings);

        historyExploreView.showReadableHistory(readableHistoryList);

    }
    public void enterHistoryRoom(String historyId){

        //set history info
    }

    private void handleHistoryPort(Packet pkt){
        this.historyPort = (Integer) pkt.getItem("port", Integer.class);
        try {
            Connector.connect(AndroidApplication.getInstance().getHistoryModuleHandler(), this.historyPort);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //below operation is for making packet and write
    public void getHistoryPort(String historyId){
        Log.v("enter_history", "history id : " + historyId);
        //make packet
        Packet pkt = new Packet("GetHistoryPort");
        pkt.addItems("historyId",historyId);
        writeToLoginHandler(pkt);
    }
    public void enterHistory(){
        /*Packet pkt = new Packet("EnterHistory");
        pkt.addItems("userId",AndroidApplication.getInstance().getUser().getId());
        writeToLoginHandler(pkt);*/

        historyExploreView.createHistoryActivity();
    }
    public void getReadableHistoryList(){
        Packet pkt = new Packet("GetReadableHistoryList");
        writeToLoginHandler(pkt);
    }
    public void setHistoryInfo(){
        Packet pkt = new Packet("SetHistoryInfo");
        //to-do: update meeting information
        writeToHistoryHandler(pkt);
    }
    private void writeToLoginHandler(Packet pkt)  {
        ServiceHandler loginHandler = AndroidApplication.getInstance().getLoginHandler();
        loginHandler.write(pkt);
    }
    private void writeToHistoryHandler(Packet pkt)  {
        ServiceHandler historyHandler = AndroidApplication.getInstance().getHistoryModuleHandler();
        historyHandler.write(pkt);
    }
}
