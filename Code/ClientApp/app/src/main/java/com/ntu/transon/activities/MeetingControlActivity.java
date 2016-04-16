package com.ntu.transon.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.ntu.transon.AndroidApplication;
import com.ntu.transon.R;
import com.ntu.transon.User;
import com.ntu.transon.UserAdapter;
import com.ntu.transon.meeting_room.MeetingParticipation;
import com.ntu.transon.meeting_room.MeetingRoom;

/**
 * Created by Mars on 2015/1/4.
 */
public class MeetingControlActivity extends ActionBarActivity {

    private void setButtonWithPopupWindow(ImageButton button, final PopupItemListener popupItemListener) {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParticipationPopupWindow participationPopupWindow = new ParticipationPopupWindow(popupItemListener);
                participationPopupWindow.show();
            }
        });
    }

    private MeetingRoom getMeetingRoom() {
        return (MeetingRoom) AndroidApplication.getInstance().getRoom();
    }

    private MeetingParticipation getMeetingParticipation() {
        return (MeetingParticipation) getMeetingRoom().getParticipation();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_control);

        setButtonWithPopupWindow((ImageButton) findViewById(R.id.mute_button), new PopupItemListener() {
            @Override
            public void onClick(User user) {
                getMeetingParticipation().mute(user);
            }
        });
        setButtonWithPopupWindow((ImageButton) findViewById(R.id.unmute_button), new PopupItemListener() {
            @Override
            public void onClick(User user) {
                getMeetingParticipation().unmute(user);
            }
        });
        setButtonWithPopupWindow((ImageButton) findViewById(R.id.kick_button), new PopupItemListener() {
            @Override
            public void onClick(User user) {
                getMeetingParticipation().kick(user);
            }
        });

        ImageButton pauseButton = (ImageButton) findViewById(R.id.pause_button);
        pauseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getMeetingParticipation().sendPause();
            }
        });
        ImageButton continueButton = (ImageButton) findViewById(R.id.continue_button);
        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getMeetingParticipation().sendContinue();
            }
        });
        ImageButton ternateButton = (ImageButton) findViewById(R.id.terminate_button);
        ternateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getMeetingParticipation().sendTerminate();
            }
        });

        ListView mAccessRequestList = (ListView) findViewById(R.id.control_access_request_list);
        final UserAdapter userAdapter = getMeetingRoom().getAccessResquestAdapter();
        mAccessRequestList.setAdapter(userAdapter);

        mAccessRequestList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                final User user = userAdapter.getUser(position);
                AlertDialog alertDialog = new AlertDialog.Builder(MeetingControlActivity.this)
                                          .setTitle("Access Request")
                                          .setMessage("Do you accept " + user.getName() + "?")
                                          .setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                                              @Override
                                              public void onClick(DialogInterface dialog, int which) {
                                                  getMeetingParticipation().accessRespond(user.getId(), true);
                                                  userAdapter.removeUser(position);
                                              }
                                          })
                                          .setNegativeButton("Reject", new DialogInterface.OnClickListener() {
                                              @Override
                                              public void onClick(DialogInterface dialog, int which) {
                                                  getMeetingParticipation().accessRespond(user.getId(), false);
                                                  userAdapter.removeUser(position);
                                              }
                                          })
                                          .create();
                alertDialog.show();
            }
        });
    }

    interface PopupItemListener {
        public void onClick(User user);
    }

    class ParticipationPopupWindow extends PopupWindow {

        ParticipationPopupWindow(final PopupItemListener popupItemListener) {
            super(MeetingControlActivity.this);

            final LayoutInflater inflater = LayoutInflater.from(MeetingControlActivity.this);
            View participationListPopup = inflater.inflate(R.layout.participation_list, null);
            final ListView mParticipationList = (ListView) participationListPopup.findViewById(R.id.participation_list);
            mParticipationList.setAdapter(new UserAdapter(MeetingControlActivity.this, getMeetingRoom().getMeetingInformation().getParticipants()));

            setContentView(participationListPopup);

            final DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int popupWidth = (int) (displayMetrics.widthPixels * 0.70);
            int popupHeight = (int) (displayMetrics.heightPixels * 0.70);

            setWidth(popupWidth);
            setHeight(popupHeight);
            setFocusable(true);
            setOutsideTouchable(true);

            ImageButton closeButton = (ImageButton) participationListPopup.findViewById(R.id.close_button);
            closeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ParticipationPopupWindow.this.dismiss();
                }
            });

            mParticipationList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    popupItemListener.onClick((User) parent.getItemAtPosition(position));
                }
            });
        }

        public void show() {
            showAtLocation(MeetingControlActivity.this.findViewById(R.id.meeting_control_layout), Gravity.CENTER, 0, 0);
        }
    }
}
