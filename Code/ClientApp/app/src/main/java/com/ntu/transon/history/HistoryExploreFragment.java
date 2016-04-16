package com.ntu.transon.history;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.ntu.transon.R;
import com.ntu.transon.activities.HistoryActivity;

import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link HistoryExploreFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link HistoryExploreFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HistoryExploreFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private HistoryExplorer historyExplorer;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private ListView historyListView;
//	private TextView show_app_name;

    private HistorySearchItemAdapter historySearchItemAdapter;
    private MenuItem add_item, search_item, revert_item, share_item, delete_item;
    private int selectedCount = 0;
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HistoryExploreFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HistoryExploreFragment newInstance(String param1, String param2) {
        HistoryExploreFragment fragment = new HistoryExploreFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public HistoryExploreFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        historyExplorer = new HistoryExplorer(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_history_explore, container, false);
        processViews(v);
        //showHistoryList();
        handleList();
        requestReadableHistory();
        return v;
    }
    private void processViews(View v) {
        historyListView = (ListView) v.findViewById(R.id.item_list);
    }

    private void handleList(){
        historyListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //enterHistory();
                HistorySearchItem item = (HistorySearchItem)parent.getAdapter().getItem(position);
                historyExplorer.getHistoryPort(item.getId()); //this will get port and listen to it
            }
        });
    }
    private void requestReadableHistory(){
        historyExplorer.getReadableHistoryList();
    }
    public void createHistoryActivity(){
        Intent intent = new Intent(getActivity(),HistoryActivity.class);
        startActivity(intent);
    }
    /*private void showHistoryList(){
        historySearchItemList = new ArrayList<HistorySearchItem>();

        //add some example for demo
        historySearchItemList.add(new HistorySearchItem("123", "Meeting", "SELab"));
        historySearchItemList.add(new HistorySearchItem("124", "Meeting", "SELab"));
        historySearchItemList.add(new HistorySearchItem("125", "Meeting", "SELab"));

        historySearchItemAdapter = new HistorySearchItemAdapter(getActivity(), R.layout.activity_history_search_item, historySearchItemList);
        historyListView.setAdapter(historySearchItemAdapter);

    }*/
    public void showReadableHistory(List<HistorySearchItem> readableHistoryList){
        historySearchItemAdapter = new HistorySearchItemAdapter(getActivity(), R.layout.activity_history_search_item, readableHistoryList);
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                historyListView.setAdapter(historySearchItemAdapter);
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
