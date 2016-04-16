package com.ntu.transon.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ListView;

import com.ntu.transon.AndroidApplication;
import com.ntu.transon.meeting_room.Message;
import com.ntu.transon.meeting_room.MessageAdapter;
import com.ntu.transon.connection.Packet;
import com.ntu.transon.R;
import com.ntu.transon.connection.Listener;
import com.ntu.transon.connection.ParticipantModuleHandler;
import com.ntu.transon.connection.ServiceHandler;
import com.ntu.transon.meeting_room.MeetingInformation;
import com.ntu.transon.meeting_room.MeetingParticipation;
import com.ntu.transon.meeting_room.MeetingRoom;
import com.ntu.transon.meeting_room.SpeechTransformer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;


public class MeetingRoomActivity extends ActionBarActivity {

    private MessageAdapter messageAdapter;
    SpeechTransformer speechTransformer;
    private boolean isMuteBySelf;
    private MenuItem controlItem;

    private MeetingRoom getMeetingRoom() {
        return (MeetingRoom) AndroidApplication.getInstance().getRoom();
    }

    private MeetingParticipation getMeetingParticipation() {
        MeetingRoom meetingRoom = getMeetingRoom();
        return meetingRoom == null ? null : (MeetingParticipation) getMeetingRoom().getParticipation();
    }

    private com.ntu.transon.meeting_room.MeetingInformation meetingInformation;
    private ListView msgList;
    protected ListView getListView() {
        if (msgList == null) {
            msgList = (ListView) findViewById(R.id.message_list);
        }
        return msgList;
    }

    private EditText toSendEditText;
    protected EditText getToSendEditText() {
        if (toSendEditText == null) {
            toSendEditText = (EditText) findViewById(R.id.to_send_editText);
        }
        return toSendEditText;
    }

    private MessageAdapter getMessageAdapter() {
        return messageAdapter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_room);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);

        final ServiceHandler participantModuleHandler = AndroidApplication.getInstance().getParticipantModuleHandler();

        // Progress Dialog with cancelability
        final ProgressDialog progressDialog = ProgressDialog.show(this, "", "Loading...", true, true, new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                participantModuleHandler.removeListener("MeetingInfo");
                participantModuleHandler.removeListener("Reject");
            }
        });

        // meeting room
        participantModuleHandler.addListener("MeetingInfo", new Listener() {
            @Override
            public void handle(Packet p) {
//                controlItem.setEnabled(getMeetingParticipation().isAdmin());
                progressDialog.dismiss();
                MeetingInformation meetingInformation = p.getItem("meetingInfo", MeetingInformation.class);
                final MeetingRoom meetingRoom = new MeetingRoom(MeetingRoomActivity.this, getMessageAdapter(), meetingInformation);
                AndroidApplication application = AndroidApplication.getInstance();
                application.setRoom(meetingRoom);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        messageAdapter.setMessages(meetingRoom.getMessages());
                        speechTransformer = new SpeechTransformer(MeetingRoomActivity.this,
                                getToSendEditText(), (MeetingParticipation) meetingRoom.getParticipation());
                        speechTransformer.startRecognize();

                        ((MeetingParticipation) meetingRoom.getParticipation()).getLog(new Date());

                    }
                });

                invalidateOptionsMenu();
            }
        });

        // reject
        participantModuleHandler.addListener("Reject", new Listener() {
            @Override
            public void handle(Packet p) {
                progressDialog.dismiss();
                final AlertDialog.Builder builder = new AlertDialog.Builder(MeetingRoomActivity.this);
                builder.setMessage("You are rejected to participate the meeting!")
                       .setCancelable(false)
                       .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                           @Override
                           public void onClick(DialogInterface dialog, int which) {
                               finish();
                           }
                       });
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }
                });

            }
        });

        enterMeeting();
        messageAdapter = new MessageAdapter(this, new ArrayList<Message>());
        getListView().setAdapter(messageAdapter);
        getToSendEditText().setKeyListener(null);

        getListView().setOnScrollListener(new InverseEndlessScrollListener() {
            @Override
            public void onLoadMore() {
                Date time = messageAdapter.getMessage(0).getTimestamp();
                getMeetingParticipation().getLog(new Date(time.getTime() - 1));
            }
        });
    }

    public abstract class InverseEndlessScrollListener implements AbsListView.OnScrollListener {
        // The minimum amount of items to have below your current scroll position
        // before loading more.
        private int visibleThreshold = 5;
        // The current offset index of data you have loaded
        private int currentPage = 0;
        // The total number of items in the dataset after the last load
        private int previousTotalItemCount = 0;
        // True if we are still waiting for the last set of data to load.
        private boolean loading = true;
        // Sets the starting page index
        private int startingPageIndex = 0;

        public InverseEndlessScrollListener() {
        }

        public InverseEndlessScrollListener(int visibleThreshold) {
            this.visibleThreshold = visibleThreshold;
        }

        public InverseEndlessScrollListener(int visibleThreshold, int startPage) {
            this.visibleThreshold = visibleThreshold;
            this.startingPageIndex = startPage;
            this.currentPage = startPage;
        }

        // This happens many times a second during a scroll, so be wary of the code you place here.
        // We are given a few useful parameters to help us work out if we need to load some more data,
        // but first we check if we are waiting for the previous load to finish.
        @Override
        public void onScroll(AbsListView view,int firstVisibleItem,int visibleItemCount,int totalItemCount)
        {
            // If the total item count is zero and the previous isn't, assume the
            // list is invalidated and should be reset back to initial state
            if (totalItemCount < previousTotalItemCount) {
                this.currentPage = this.startingPageIndex;
                this.previousTotalItemCount = totalItemCount;
                if (totalItemCount == 0) { this.loading = true; }
            }
            // If it’s still loading, we check to see if the dataset count has
            // changed, if so we conclude it has finished loading and update the current page
            // number and total item count.
            if (loading && (totalItemCount > previousTotalItemCount)) {
                loading = false;
                previousTotalItemCount = totalItemCount;
                currentPage++;
            }

            // If it isn’t currently loading, we check to see if we have breached
            // the visibleThreshold and need to reload more data.
            // If we do need to reload some more data, we execute onLoadMore to fetch the data.
            if (!loading && (firstVisibleItem < visibleThreshold)) {
                onLoadMore();
                loading = true;
            }
        }

        // Defines the process for actually loading more data based on page
        public abstract void onLoadMore();

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            // Don't take any action on changed
        }
    }

    private void enterMeeting() {
        //make packet
        Packet pkt = new Packet("EnterMeeting");
        pkt.addItems("userId", AndroidApplication.getInstance().getUser().getId());
        //writeToParticipantHandler(pkt);
        final ParticipantModuleHandler participantHandler = AndroidApplication.getInstance().getParticipantModuleHandler();
        participantHandler.write(pkt);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        Intent intent;
        switch (item.getItemId()) {
            case R.id.muteMyself:
                //muteMyself and change icon
                handleMuteMyself(item);
                return true;
            case R.id.meetingSetting:
                //goto setting view
                intent = new Intent(MeetingRoomActivity.this, MeetingSettingActivity.class);
                startActivity(intent);
                return true;
            case R.id.meetingControl:
                //goto control view
                intent = new Intent(MeetingRoomActivity.this, MeetingControlActivity.class);
                startActivity(intent);
                return true;



            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void handleMuteMyself(MenuItem item){
        if(item.getTitle().equals("Mute") ) {
            item.setIcon(R.drawable.ic_action_mic);
            item.setTitle("UnMute");
            speechTransformer.stopRecognize();
            isMuteBySelf = true;
        }
        else {
            item.setIcon(R.drawable.ic_action_mic_muted);
            item.setTitle("Mute");
            speechTransformer.startRecognize();
            isMuteBySelf = false;
        }
    }

    public void notifyIsAdmin(final boolean isAdmin) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                controlItem.setVisible(isAdmin);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_meeting_room, menu);
        controlItem = menu.findItem(R.id.meetingControl);
        //controlItem.setEnabled(false);
        if(getMeetingRoom()!=null) {
            Log.v("Control","Am I admin?"+getMeetingParticipation().isAdmin());
            controlItem.setVisible(getMeetingParticipation().isAdmin());
        }
        else {
            Log.v("Control", "meeting room is null!!!");
            controlItem.setVisible(false);
        }
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        AndroidApplication.getInstance().setRoom(null);
        if (speechTransformer != null) {
            speechTransformer.destroy();
        }
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();

        MeetingParticipation meetingParticipation = getMeetingParticipation();
        if (meetingParticipation != null) {
            meetingParticipation.leave();
        } else {
            ServiceHandler handler = AndroidApplication.getInstance().getParticipantModuleHandler();
            try {
                handler.disconnect();
            } catch (IOException e) {
                e.printStackTrace();
            }

            startActivity(new Intent(this, LobbyActivity.class));
            finish();
        }
    }
}
