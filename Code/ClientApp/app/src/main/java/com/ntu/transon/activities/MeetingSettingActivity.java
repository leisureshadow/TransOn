package com.ntu.transon.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.ntu.transon.AndroidApplication;
import com.ntu.transon.R;
import com.ntu.transon.User;
import com.ntu.transon.UserAdapter;
import com.ntu.transon.meeting_room.MeetingInformation;
import com.ntu.transon.meeting_room.MeetingParticipation;
import com.ntu.transon.meeting_room.MeetingRoom;

import java.util.ArrayList;


public class MeetingSettingActivity extends ActionBarActivity {

    private ScrollView mLayout;

    private void fillRowEditText(View view, String label, Object value) {
        fillRowEditText(view, label, value, true, true);
    }

    private void fillRowEditText(View view, String label, Object value, boolean enable) {
        fillRowEditText(view, label, value, enable, true);
    }

    private void fillRowEditText(View view, String label, Object value, boolean enable, boolean singleLine) {
        TextView labelView = (TextView) view.findViewById(R.id.label);
        labelView.setText(label);
        EditText valueView = (EditText) view.findViewById(R.id.value);
        valueView.setText(String.valueOf(value));
        valueView.setSingleLine(singleLine);
        valueView.setEnabled(enable && getMeetingParticipation().isAdmin());
    }

    private void fillRowSwitch(View view, String label, boolean switch1) {
        TextView labelView = (TextView) view.findViewById(R.id.label);
        labelView.setText(label);
        Switch switch1View = (Switch) view.findViewById(R.id.switch1);
        switch1View.setChecked(switch1);
        switch1View.setEnabled(getMeetingParticipation().isAdmin());
    }

    private void fillRowListPopup(View view, String label, final UserListHandler userListHandler) {
        TextView labelView = (TextView) view.findViewById(R.id.label);
        labelView.setText(label);
        labelView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final LayoutInflater inflater = LayoutInflater.from(MeetingSettingActivity.this);
                View usernameListPopup = inflater.inflate(R.layout.list_user, null);
                final ListView mUsernameList = (ListView) usernameListPopup.findViewById(R.id.list_user);
                mUsernameList.setAdapter(new UserAdapter(MeetingSettingActivity.this, userListHandler.getUserList()));

                final DisplayMetrics displayMetrics = new DisplayMetrics();
                getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                int popupWidth = (int) (displayMetrics.widthPixels * 0.70);
                int popupHeight = (int) (displayMetrics.heightPixels * 0.70);

                final PopupWindow mUsernameListPopup = new PopupWindow(usernameListPopup, popupWidth, popupHeight);
                mUsernameListPopup.setFocusable(true);
                mUsernameListPopup.setOutsideTouchable(true);

                ImageButton closeButton = (ImageButton) usernameListPopup.findViewById(R.id.close_button);
                closeButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mUsernameListPopup.dismiss();
                    }
                });

                final ImageButton addButton = (ImageButton) usernameListPopup.findViewById(R.id.add_button);
                if (getMeetingParticipation().isAdmin() && userListHandler.isModifiable()) {
                    addButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            View participationListPopup = inflater.inflate(R.layout.participation_list, null);
                            final ListView mParticipationList = (ListView) participationListPopup.findViewById(R.id.participation_list);
                            mParticipationList.setAdapter(new UserAdapter(MeetingSettingActivity.this, userListHandler.getCandidates()));

                            int popupWidth = (int) (displayMetrics.widthPixels * 0.60);
                            int popupHeight = (int) (displayMetrics.heightPixels * 0.60);
                            final PopupWindow mParticipationListPopup = new PopupWindow(participationListPopup, popupWidth, popupHeight);
                            mUsernameListPopup.setFocusable(true);
                            mUsernameListPopup.setOutsideTouchable(true);

                            ImageButton closeButton = (ImageButton) participationListPopup.findViewById(R.id.close_button);
                            closeButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mParticipationListPopup.dismiss();
                                }
                            });

                            mParticipationList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    userListHandler.addUser((User) parent.getItemAtPosition(position));
                                    ((UserAdapter) mUsernameList.getAdapter()).setUsers(userListHandler.getUserList());
                                    mParticipationListPopup.dismiss();
                                }
                            });

                            mParticipationListPopup.showAtLocation(mLayout, Gravity.CENTER, 0, 0);
                        }
                    });
                    mUsernameList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(final AdapterView<?> parent, View view, final int position, long id) {
                            final AlertDialog alertDialog = new AlertDialog.Builder(MeetingSettingActivity.this)
                                                      .setTitle("Remove user from list")
                                                      .setMessage("Are you sure?")
                                                      .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                          @Override
                                                          public void onClick(DialogInterface dialog, int which) {
                                                              userListHandler.removeUser((User) parent.getItemAtPosition(position));
                                                              ((UserAdapter) mUsernameList.getAdapter()).setUsers(userListHandler.getUserList());
                                                              dialog.dismiss();
                                                          }
                                                      })
                                                      .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                                          @Override
                                                          public void onClick(DialogInterface dialog, int which) {
                                                              dialog.dismiss();
                                                          }
                                                      })
                                                      .create();
                            alertDialog.show();
                        }
                    });
                } else {
                    addButton.setVisibility(View.GONE);
                }

                mUsernameListPopup.showAtLocation(mLayout, Gravity.CENTER, 0, 0);
            }
        });
    }

    private MeetingRoom getMeetingRoom() {
        return (MeetingRoom) AndroidApplication.getInstance().getRoom();
    }

    private MeetingInformation getMeetingInformation() {
        MeetingRoom meetingRoom = getMeetingRoom();
        return (meetingRoom == null ? null : meetingRoom.getMeetingInformation());
    }

    private MeetingParticipation getMeetingParticipation() {
        return (MeetingParticipation) getMeetingRoom().getParticipation();
    }

    interface UserListHandler {
        public boolean isModifiable();
        public void addUser(User user);
        public void removeUser(User user);
        public ArrayList<User> getCandidates();
        public ArrayList<User> getUserList();
    }

    class BaseUserListHandler implements UserListHandler {
        private ArrayList<User> userList;
        private ArrayList<User> candidates;

        public BaseUserListHandler(ArrayList<User> userList, ArrayList<User> candidates) {
            this.userList = userList;
            this.candidates = candidates;
        }

        @Override
        public boolean isModifiable() {
            return !candidates.isEmpty();
        }

        @Override
        public void addUser(User user) {
            userList.add(user);
        }

        @Override
        public void removeUser(User user) {
            userList.remove(user);
        }

        @Override
        public ArrayList<User> getCandidates() {
            ArrayList<User> resultList = new ArrayList<User>(candidates);
            resultList.removeAll(userList);
            return resultList;
        }

        @Override
        public ArrayList<User> getUserList() {
            return userList;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meeting_setting);

        final MeetingInformation meetingInformation = getMeetingInformation();

        fillRowEditText(findViewById(R.id.meeting_id), "Meeting ID", meetingInformation.getMeetingID(), false);
        fillRowEditText(findViewById(R.id.meeting_subject), "Subject", meetingInformation.getSubject());
        fillRowEditText(findViewById(R.id.meeting_location), "Location", meetingInformation.getLocation());
        fillRowEditText(findViewById(R.id.meeting_latitude), "Latitude", meetingInformation.getLatitude(), false);
        fillRowEditText(findViewById(R.id.meeting_longtitude), "Longitude", meetingInformation.getLongitude(), false);
        fillRowEditText(findViewById(R.id.meeting_start_time), "Start time", meetingInformation.getStartTime(), false);
        fillRowEditText(findViewById(R.id.meeting_end_time), "End time", meetingInformation.getEndTime(), false);
        fillRowEditText(findViewById(R.id.meeting_description), "Description", meetingInformation.getDescription(), true, false);
        fillRowEditText(findViewById(R.id.meeting_initiator), "Initiator", meetingInformation.getInitiator(), false);
        fillRowSwitch(findViewById(R.id.meeting_private), "Private", meetingInformation.isPrivate());
        fillRowSwitch(findViewById(R.id.meeting_secret), "Secret", meetingInformation.isSecret());

        fillRowListPopup(findViewById(R.id.meeting_participant_list), "Participant list",
                new BaseUserListHandler(meetingInformation.getParticipants(), new ArrayList<User>()));
        fillRowListPopup(findViewById(R.id.meeting_administrator_list), "Administrator list",
                new BaseUserListHandler(meetingInformation.getAdministrators(), meetingInformation.getParticipants()) {
                    @Override
                    public void addUser(User user) {
                        super.addUser(user);
                        getMeetingParticipation().setAdmin(user);
                    }

                    @Override
                    public void removeUser(User user) {
                        super.removeUser(user);
                        getMeetingParticipation().unsetAdmin(user);
                    }
                });
        fillRowListPopup(findViewById(R.id.meeting_history_readable_list), "History readable list",
                new BaseUserListHandler(meetingInformation.getReadableList(), meetingInformation.getParticipants()));
        fillRowListPopup(findViewById(R.id.meeting_blacklist), "Blacklist",
                new BaseUserListHandler(meetingInformation.getBlacklist(), new ArrayList<User>()));



        mLayout = (ScrollView) findViewById(R.id.meeting_setting_layout);
        ImageButton updateButton = (ImageButton) findViewById(R.id.update_button);
        if (getMeetingParticipation().isAdmin()) {
            updateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateMeetingInformation(meetingInformation);
                    getMeetingParticipation().sendUpdateMeetingInfo(meetingInformation);

                    Toast.makeText(MeetingSettingActivity.this,"setting updated",Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            updateButton.setVisibility(View.GONE);
        }
    }

    private void updateMeetingInformation(MeetingInformation meetingInformation){
        TextView description = (TextView) findViewById(R.id.meeting_description).findViewById(R.id.value);
        TextView subject = (TextView) findViewById(R.id.meeting_subject).findViewById(R.id.value);
        TextView location = (TextView) findViewById(R.id.meeting_location).findViewById(R.id.value);
        Switch privateSwitch = (Switch) findViewById(R.id.meeting_private).findViewById(R.id.switch1);
        Switch secretSwitch = (Switch) findViewById(R.id.meeting_secret).findViewById(R.id.switch1);
        meetingInformation.setDescription(description.getText().toString());
        meetingInformation.setSubject(subject.getText().toString());
        meetingInformation.setLocation(location.getText().toString());
        meetingInformation.setPrivate(privateSwitch.isChecked());
        meetingInformation.setSecret(secretSwitch.isChecked());
        //TODO: some setting need to be update....
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_meeting_setting, menu);
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
