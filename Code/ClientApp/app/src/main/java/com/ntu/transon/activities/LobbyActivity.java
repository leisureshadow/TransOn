package com.ntu.transon.activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.ntu.transon.history.HistoryExploreFragment;
import com.ntu.transon.meeting.MeetingSearchFragment;
import com.ntu.transon.R;

public class LobbyActivity extends ActionBarActivity implements MeetingSearchFragment.OnFragmentInteractionListener,HistoryExploreFragment.OnFragmentInteractionListener {

    private Button meetingSearchButton;
    private Button historyExploreButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

        setActionBar();

        FragmentManager fm = getFragmentManager();

        FragmentTransaction ft =fm.beginTransaction();
        ft.add(R.id.fragmentContainer,(Fragment)new MeetingSearchFragment());
        ft.commit();

        //handle button
        meetingSearchButton = (Button)findViewById(R.id.meeting_search_button);
        historyExploreButton = (Button)findViewById(R.id.history_explorer_button);
        meetingSearchButton.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v){
                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft =fm.beginTransaction();
                ft.replace(R.id.fragmentContainer,(Fragment)new MeetingSearchFragment());
                ft.commit();
            }
        });
        historyExploreButton.setOnClickListener(new Button.OnClickListener(){
            @Override
            public void onClick(View v){
                FragmentManager fm = getFragmentManager();
                FragmentTransaction ft =fm.beginTransaction();
                ft.replace(R.id.fragmentContainer,(Fragment)new HistoryExploreFragment());
                ft.commit();
            }
        });
    }

    private void setActionBar(){
        ActionBar actionBar = getSupportActionBar();

    }
    public void createMeetingRoomActivity(){
        Intent intent = new Intent(this,MeetingRoomActivity.class);
        startActivity(intent);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_lobby, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.create_meeting) {
            Intent intent = new Intent(this,MeetingInitiateActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_lobby, container, false);
            return rootView;
        }
    }

    public void onBackPressed()
    {
        super.onBackPressed();
        startActivity(new Intent(this, LobbyActivity.class));
        finish();

    }
}
