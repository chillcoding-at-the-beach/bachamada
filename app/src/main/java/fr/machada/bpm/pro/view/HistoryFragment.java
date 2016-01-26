package fr.machada.bpm.pro.view;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;


import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;

import fr.machada.bpm.pro.R;
import fr.machada.bpm.pro.model.CustomExpandableListAdapter;
import fr.machada.bpm.pro.model.Group;
import fr.machada.bpm.pro.model.RegisteredBpm;
import fr.machada.bpm.pro.utils.SomeKeys;

public class HistoryFragment extends Fragment {
    // more efficient than HashMap for mapping integers to objects
    SparseArray<Group> mGroups;
    CustomExpandableListAdapter mAdapter;
    ExpandableListView mListView;


    public interface ItemSelectedListener {
        public void onDeleteBpmClick(int id, int groupPosition, int childPosition);
    }

    // Use this instance of the interface to deliver action events
    ItemSelectedListener mListener;


    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (ItemSelectedListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement ItemSelectedListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        List<RegisteredBpm> listOfBpm;
        if (bundle != null && bundle.containsKey(SomeKeys.BUNDLE_BPM_LIST)) {
            listOfBpm = (List<RegisteredBpm>) bundle.get(SomeKeys.BUNDLE_BPM_LIST);
            createData(listOfBpm);
        }
        mAdapter = new CustomExpandableListAdapter(getActivity(), mGroups);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_layout_history, container, false);
        //createData();
        mListView = (ExpandableListView) rootView.findViewById(R.id.listView);


        mListView.setAdapter(mAdapter);


        mListView.setOnChildClickListener(mListItemClicked);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mGroups.size() > 0)
            mListView.expandGroup(0);
    }

    public void createData(List<RegisteredBpm> bpmList) {

        mGroups = null;
        mGroups = new SparseArray<Group>();
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM");
        for (int i = 0; i < bpmList.size(); i = i + 5) {
            // to get a nice title for expandable list
            Date d1 = new Date(bpmList.get(i).getDate());
            Group group = new Group(sdf.format(d1));
            if (i + 5 < bpmList.size()) {
                //there is another EL after this one
                Date d2 = new Date(bpmList.get(i + 5).getDate());
                if (d1.getMonth() == d2.getMonth())
                    //the BPM of the 2 followed EL was taken in same month
                    if (d1.getDay() == d2.getDay()) {
                        //the BPM of the 2 followed EL was taken in same day
                        if (i > 0)// it's not the first
                            group = new Group(mGroups.get(i - 5).getLabel() + " Bis");
                        else
                            group = new Group(sdf.format(d1) + " Bis");
                    } else
                        group = new Group("" + d1.getDay() + "-" + d2.getDay() + " " + sdf.format(d1));
            }

            for (int ii = 0; ii < 5 && i + ii < bpmList.size(); ii++) {
                group.children.add(bpmList.get(i + ii));
            }
            mGroups.append(i / 5, group);
        }


    }

    public void removeData(int gp, int cp) {
        mAdapter.removeBpm(gp, cp);
    }

    public void addData(RegisteredBpm bpm) {
        if (mAdapter != null)
            mAdapter.addBpm(bpm);
    }


    private OnChildClickListener mListItemClicked = new OnChildClickListener() {

        @Override
        public boolean onChildClick(ExpandableListView parent, View v,
                                    int groupPosition, int childPosition, long id) {
            mListener.onDeleteBpmClick(mGroups.get(groupPosition).children.get(childPosition).getId(), groupPosition, childPosition);
            return false;
        }

    };


    public void refresh() {
        if (mAdapter != null)
            mAdapter.notifyDataSetChanged();
    }


}