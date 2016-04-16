package com.ntu.transon.meeting;

import android.app.Activity;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.ntu.transon.R;
import com.ntu.transon.activities.EnterByIdFragment;
import com.ntu.transon.activities.LobbyActivity;
import com.ntu.transon.activities.MeetingRoomActivity;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MeetingSearchFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MeetingSearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MeetingSearchFragment extends Fragment  {

    private ListView nearbyMeetingList;
    private Button enterMeetingButton;
    private Button showNearByMeetingButton;
    private OngoingMeetingSearcher meetingSearcher;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MeetingSearchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MeetingSearchFragment newInstance(String param1, String param2) {
        MeetingSearchFragment fragment = new MeetingSearchFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public MeetingSearchFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        try {
            meetingSearcher = new OngoingMeetingSearcher((LobbyActivity)getActivity(),this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_meeting_search, container, false);
        if (nearbyMeetingList == null) {
            nearbyMeetingList = (ListView) v.findViewById(R.id.nearby_meeting_list);
        }
        handleButtons(v);
        requestNearbyMeetings();

        return v;
    }
    private void handleButtons(View v) {
        enterMeetingButton = (Button)v.findViewById(R.id.enter_a_meeting_button);
        enterMeetingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });

        showNearByMeetingButton=(Button)v.findViewById(R.id.show_nearby_meeting_button);
        showNearByMeetingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //showNearbyList
                try {
                    meetingSearcher.requestNearbyMeetingList();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    private void requestNearbyMeetings(){
        try {
            meetingSearcher.requestNearbyMeetingList();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void handleList() {
        nearbyMeetingList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Map.Entry<String,String> item = (Map.Entry<String,String>)parent.getAdapter().getItem(position);
                String meetingId = item.getKey();
                try {
                    enterMeetingRoom(meetingId);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }
    private void enterMeetingRoom(String meetingId) throws IOException {
        meetingSearcher.enterMeetingRoom(meetingId);
        //wait for some time....
    }
    public void createMeetingRoomActivity(){
        Intent intent = new Intent(getActivity(),MeetingRoomActivity.class);
        startActivity(intent);
    }
    private void showDialog(){
        DialogFragment dialog = new EnterByIdFragment();
        dialog.show(getFragmentManager(),"tag??");
    }
    public void showNearbyMeetings(final HashMap meetingList) throws IOException {

        //meetingSearcher.requestNearbyMeetingList();
        /*ArrayList<String> nearbyMeetings = new ArrayList<String>(meetingList.values());
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                getActivity(),
                android.R.layout.simple_list_item_1,
                nearbyMeetings );
        nearbyMeetingList.setAdapter(arrayAdapter);*/
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                HashMapAdapter adaptor = new HashMapAdapter(meetingList, getActivity());
                nearbyMeetingList.setAdapter(adaptor);
                handleList();
//stuff that updates ui

            }
        });
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

}
