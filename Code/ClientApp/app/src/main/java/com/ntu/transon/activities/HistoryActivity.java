package com.ntu.transon.activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.ntu.transon.AndroidApplication;
import com.ntu.transon.meeting_room.Message;
import com.ntu.transon.meeting_room.MessageAdapter;
import com.ntu.transon.connection.Packet;
import com.ntu.transon.R;
import com.ntu.transon.connection.HistoryModuleHandler;
import com.ntu.transon.connection.Listener;
import com.ntu.transon.connection.ServiceHandler;
import com.ntu.transon.history_room.HistoryParticipation;
import com.ntu.transon.history_room.HistoryRoom;
import com.ntu.transon.meeting_room.MeetingInformation;

import java.util.ArrayList;
import java.util.Date;

public class HistoryActivity extends ActionBarActivity {

    private MessageAdapter messageAdapter;
    private ListView msgList;

    private HistoryRoom getHistoryRoom() {
        return (HistoryRoom) AndroidApplication.getInstance().getRoom();
    }

    private HistoryParticipation getHistoryParticipation() {
        return (HistoryParticipation) getHistoryRoom().getParticipation();
    }

    private com.ntu.transon.meeting_room.MeetingInformation meetingInformation;

    protected ListView getListView() {
        if (msgList == null) {
            msgList = (ListView) findViewById(R.id.message_list);
        }
        return msgList;
    }
    private MessageAdapter getMessageAdapter() {
        return messageAdapter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        //is return bar needed?
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(false);


        final ServiceHandler participantModuleHandler = AndroidApplication.getInstance().getHistoryModuleHandler();
        // Progress Dialog with cancelability
        final ProgressDialog progressDialog = ProgressDialog.show(this, "", "Loading...", true, true, new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                participantModuleHandler.removeListener("HistoryInfo");
                participantModuleHandler.removeListener("Reject");
            }
        });

        participantModuleHandler.addListener("HistoryInfo", new Listener() {
            @Override
            public void handle(Packet p) {
                progressDialog.dismiss();
                MeetingInformation meetingInformation = p.getItem("historyInfo", MeetingInformation.class);
                final HistoryRoom historyRoom = new HistoryRoom(getMessageAdapter(), meetingInformation);
                AndroidApplication application = AndroidApplication.getInstance();
                application.setRoom(historyRoom);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        messageAdapter.setMessages(historyRoom.getMessages());
                        ((HistoryParticipation)historyRoom.getParticipation()).getLog(new Date());
                    }
                });


            }
        });
        // reject
        participantModuleHandler.addListener("Reject", new Listener() {
            @Override
            public void handle(Packet p) {
                progressDialog.dismiss();
                AlertDialog.Builder builder = new AlertDialog.Builder(HistoryActivity.this);
                builder.setMessage("You are rejected to participate the meeting!")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

 /*  for demo
        Time now = new Time();
        now.setToNow();
        messages = new ArrayList<Message>();
        messages.add(new Message("賄新", "不要讓腎文不開心", now));
        messages.add(new Message("因九", "一個便當吃不飽,可以吃第二個ㄚ", now));
        messages.add(new Message("因九", "逆轟高輝", now));
        messages.add(new Message("救援王扁扁", "難道阿扁湊了嗎QQ", now));
*/
        enterHistory();
        messageAdapter = new MessageAdapter(this, new ArrayList<Message>());
        getListView().setAdapter(messageAdapter);
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_history, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Intent intent;
        //noinspection SimplifiableIfStatement
        switch (item.getItemId()) {
            case R.id.historyDetail:
                //goto detail view
                intent = new Intent(HistoryActivity.this, HistorySettingActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void enterHistory() {
        //make packet
        Packet pkt = new Packet("EnterHistory");
        pkt.addItems("userId", AndroidApplication.getInstance().getUser().getId());
        //writeToParticipantHandler(pkt);
        final HistoryModuleHandler historyHandler = AndroidApplication.getInstance().getHistoryModuleHandler();
        historyHandler.setInitPck(pkt);
    }
    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        startActivity(new Intent(this, LobbyActivity.class));
        finish();

    }
}
