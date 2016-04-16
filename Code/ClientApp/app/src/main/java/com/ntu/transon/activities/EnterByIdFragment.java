package com.ntu.transon.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.ntu.transon.meeting.OngoingMeetingSearcher;
import com.ntu.transon.R;

import java.io.IOException;


public class EnterByIdFragment extends DialogFragment {
    private OngoingMeetingSearcher meetingSearcher;
    private EditText id;
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_enter_by_id, null);
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view)
                // Add action buttons
                .setPositiveButton("enter", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        enterMeetingRoom();
                    }
                })
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        EnterByIdFragment.this.getDialog().cancel();
                    }
                });
        this.id = (EditText) view.findViewById(R.id.enter_by_id);
        return builder.create();
    }
    private void enterMeetingRoom(){
        /*Intent intent = new Intent(getActivity(),MeetingRoomActivity.class);
        startActivity(intent);*/
        try {
            meetingSearcher = new OngoingMeetingSearcher((LobbyActivity)getActivity(),this);
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            String meetingId = id.getText().toString();
            meetingSearcher.enterMeetingRoom(meetingId);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

