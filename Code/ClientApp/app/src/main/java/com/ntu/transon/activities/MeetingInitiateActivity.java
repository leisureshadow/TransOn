package com.ntu.transon.activities;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.ntu.transon.meeting.OngoingMeetingSearcher;
import com.ntu.transon.R;

import java.io.IOException;


public class MeetingInitiateActivity extends ActionBarActivity {
    private EditText subject;
    private EditText location;
    private EditText description;
    private RadioGroup visibility;
    private RadioGroup secret;
    private Button button_ok;
    private Button button_cancel;
    private OngoingMeetingSearcher meetingSearcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_initiate);

        try {
            meetingSearcher = new OngoingMeetingSearcher(this);
        } catch (IOException e) {
            e.printStackTrace();
        }

        subject = (EditText)findViewById(R.id.initiate_subject);
        location = (EditText)findViewById(R.id.initiate_location);
        description = (EditText)findViewById(R.id.initiate_description);
        visibility = (RadioGroup)findViewById(R.id.initiate_visibility);
        secret = (RadioGroup)findViewById(R.id.initiate_secret);
        button_ok = (Button)findViewById(R.id.initiate_ok);
        button_cancel = (Button)findViewById(R.id.initiate_cancel);
        handleButtons();
    }
    private boolean getVisibility(){
        // return isPrivate
        if(visibility.getCheckedRadioButtonId()==R.id.initiate_public)
            return false;
        else
            return true;
    }
    private boolean getSecret(){
        if(secret.getCheckedRadioButtonId()==R.id.initiate_secret_no)
            return false;
        else
            return true;
    }
    private void handleButtons(){
        button_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //initiate a new meeting
                try {
                    meetingSearcher.initiateMeeting(subject.getText().toString(),location.getText().toString(),description.getText().toString(),getVisibility(),getSecret());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void createMeetingRoomActivity(){
        Intent intent = new Intent(this,MeetingRoomActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_meeting_initiate, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
