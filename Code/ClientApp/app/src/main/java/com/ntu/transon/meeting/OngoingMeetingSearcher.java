package com.ntu.transon.meeting;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;


import com.google.gson.reflect.TypeToken;
import com.ntu.transon.AndroidApplication;
import com.ntu.transon.activities.EnterByIdFragment;
import com.ntu.transon.activities.LobbyActivity;
import com.ntu.transon.activities.MeetingInitiateActivity;
import com.ntu.transon.connection.Connector;
import com.ntu.transon.connection.Listener;
import com.ntu.transon.connection.Packet;
import com.ntu.transon.connection.ServiceHandler;
import com.ntu.transon.meeting_room.MeetingInformation;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by 佳芷 on 2014/12/26.
 */
public class OngoingMeetingSearcher implements LocationListener{
    private HashMap ongoingMeetings;
    private Context context;
    private int meetingPort;
    private MeetingSearchFragment meetingSearchView;
    private MeetingInitiateActivity initiateActivity;
    private EnterByIdFragment enterByIdFragment;
    private LobbyActivity lobbyActivity;
    public OngoingMeetingSearcher(MeetingInitiateActivity meetingInitiateActivity) throws IOException{
        this.context = meetingInitiateActivity;
        this.initiateActivity = meetingInitiateActivity;

        //add listeners to handler
        addListeners();
    }
    public OngoingMeetingSearcher(LobbyActivity lobby,MeetingSearchFragment meetingSearchFragment) throws IOException{
        this.context = lobby;
        this.lobbyActivity = lobby;
        this.meetingSearchView = meetingSearchFragment;

        //add listeners to handler
        addListeners();
    }
    public OngoingMeetingSearcher(LobbyActivity lobby,EnterByIdFragment enterByIdFragment) throws IOException{
        this.context = lobby;
        this.lobbyActivity = lobby;
        this.enterByIdFragment = enterByIdFragment;

        //add listeners to handler
        addListeners();
    }
    public OngoingMeetingSearcher(Activity activity){
        this.context = activity;
        addListeners();
    }
    private void addListeners() {
        ServiceHandler loginHandler = AndroidApplication.getInstance().getLoginHandler();
        loginHandler.addListener("MeetingPort",new Listener() {
            @Override
            public void handle(Packet p)  {
                handleMeetingPort(p);
                createMeetingRoom();
            }
        });
       loginHandler.addListener("NearbyMeetingList",new Listener() {
            @Override
            public void handle(Packet pkt)  {
                try {
                    setNearbyMeetingList(pkt);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }
    public void initiateMeeting(String subject,String location,String description,boolean isPrivate,boolean isSecret) throws IOException {
        String userId = AndroidApplication.getInstance().getUser().getId();
        Location loc = getLocation();
        MeetingInformation meetingInfo = new MeetingInformation(subject,location,loc.getLatitude(),loc.getLongitude(),description,userId,isPrivate,isSecret);

        Packet pkt = new Packet("Create");
        pkt.addItems("meetingInfo",meetingInfo);

        writeToLoginHandler(pkt);
    }

    /***
     * this will be called by meeting search fragment/activity
     * **/
    public void enterMeetingRoom(String meetingId) throws IOException {
        Log.v("enter_meeting","meeting id : "+meetingId);
        requestMeetingPort(meetingId);
    }

    public void requestMeetingPort(String meetingId) throws IOException {
        //make packet
        Packet pkt = new Packet("GetMeetingPort");
        pkt.addItems("meetingId",meetingId);

        writeToLoginHandler(pkt);
    }
    private void handleMeetingPort(Packet pkt){
        this.meetingPort=  pkt.getItem("port", Integer.class);

        try {

            Connector.connect(AndroidApplication.getInstance().getParticipantModuleHandler(),meetingPort);
            Log.v("enterMeeting","connector is connected. meeting port ="+meetingPort);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public void requestNearbyMeetingList() throws IOException {
        Packet pkt = makeGetNearbyPacket();
        writeToLoginHandler(pkt);
    }
    public void setNearbyMeetingList(Packet pkt) throws IOException {
       ongoingMeetings = new HashMap<String,String>();
        ArrayList<HashMap<String,String>> meetingList =
                (ArrayList<HashMap<String,String>>) pkt.getItem("meetingList", new TypeToken<ArrayList<HashMap<String,String>>>(){}.getType());
        for(HashMap meeting:meetingList){
            //need to be modified....
            ongoingMeetings.put( meeting.get("meetingId"), meeting.get("subject"));
        }


        if(meetingList.isEmpty()){
            ongoingMeetings.put("","Oops! There is no nearby meeting.");
        }
        //for test
        /*HashMap test = new HashMap();
        test.put("0","id:1; subject: meeting1;location: 123");
        test.put("1","id:2; subject: meeting2;location: 456");*/

        meetingSearchView.showNearbyMeetings(ongoingMeetings);

    }
    private Packet makeGetNearbyPacket(){
        Location location = getLocation();
        Packet pkt = new Packet("GetNearbyMeetingList");
        pkt.addItems("latitude",location.getLatitude());
        pkt.addItems("longitude",location.getLongitude());
        return pkt;

    }
    protected Location getLocation(){
        LocationManager lm= (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if(location==null) {location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);}
        return location;
    }

    private void createMeetingRoom(){
        if(lobbyActivity !=null)
            lobbyActivity.createMeetingRoomActivity();
        else
            initiateActivity.createMeetingRoomActivity();
    }

    private void writeToLoginHandler(Packet pkt)  {
        ServiceHandler loginHandler = AndroidApplication.getInstance().getLoginHandler();
        loginHandler.write(pkt);
        Log.v("loginHandler","write to login handler"+pkt.getCommand());
    }
    private void writeToParticipantHandler(Packet pkt)  {
        ServiceHandler participantHandler = AndroidApplication.getInstance().getParticipantModuleHandler();
        participantHandler.write(pkt);
        Log.v("participantHandler", "write to participant handler" + pkt.getCommand());
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
