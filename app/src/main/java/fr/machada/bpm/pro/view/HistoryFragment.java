package fr.machada.bpm.pro.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.TextView;

import java.util.List;

import de.greenrobot.event.EventBus;
import fr.machada.bpm.pro.DetailsActivity;
import fr.machada.bpm.pro.MainActivity;
import fr.machada.bpm.pro.R;
import fr.machada.bpm.pro.event.OnDeleteFCEvent;
import fr.machada.bpm.pro.model.CustomExpandableListAdapter;
import fr.machada.bpm.pro.model.RegisteredFC;
import fr.machada.bpm.pro.utils.SomeKeys;

public class HistoryFragment extends Fragment {
    // more efficient than HashMap for mapping integers to objects
    CustomExpandableListAdapter mAdapter;
    ExpandableListView mListView;
    private TextView mTextView;
    private boolean mNoData;
    private int mGroupPosition;
    private int mChildPosition;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        List<RegisteredFC> listOfBpm = null;
        if (bundle != null && bundle.containsKey(SomeKeys.BUNDLE_BPM_LIST)) {
            listOfBpm = (List<RegisteredFC>) bundle.get(SomeKeys.BUNDLE_BPM_LIST);
            if (listOfBpm.size() < 1)
                mNoData = true;
        }
        mAdapter = new CustomExpandableListAdapter(getActivity(), listOfBpm);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_layout_history, container, false);

        mTextView = (TextView) rootView.findViewById(R.id.history_empty);
        if (mNoData)
            mTextView.setVisibility(View.VISIBLE);
        mListView = (ExpandableListView) rootView.findViewById(R.id.listView);
        mListView.setAdapter(mAdapter);
        mListView.setOnChildClickListener(mListItemClicked);
        mListView.setOnItemLongClickListener(mListItemLongClick);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAdapter != null && mAdapter.getGroupCount() > 0)
            mListView.expandGroup(mAdapter.getGroupCount() - 1);
    }

    public void removeData(int gp, int cp) {
        mAdapter.removeFC(gp, cp);
    }

    public void addData(RegisteredFC bpm) {
        if (mAdapter != null) {
            if (mAdapter.addFC(bpm))
                mListView.expandGroup(mAdapter.getGroupCount() - 1);
            mAdapter.notifyDataSetChanged();
            if (mTextView.getVisibility() == View.VISIBLE)
                mTextView.setVisibility(View.GONE);
        }

    }

    private OnChildClickListener mListItemClicked = new OnChildClickListener() {
        @Override
        public boolean onChildClick(ExpandableListView parent, final View v,
                                    final int groupPosition, final int childPosition, long id) {

            if (mLongClick) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setMessage(R.string.dialog_delete)
                        .setPositiveButton(R.string.delete_text, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                EventBus.getDefault().post(new OnDeleteFCEvent((int) mAdapter.getChildId(groupPosition, childPosition)));
                                removeData(groupPosition, childPosition);
                                refresh();
                            }
                        })
                        .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });
                builder.create().show();
                mLongClick = false;
            } else {
                mGroupPosition = groupPosition;
                mChildPosition = childPosition;
                Intent intent = new Intent(getActivity(), DetailsActivity.class);
                Bundle b = new Bundle();
                b.putSerializable(SomeKeys.BUNDLE_FC, (RegisteredFC) mAdapter.getChild(groupPosition, childPosition));
                intent.putExtras(b);
                ActivityOptionsCompat activityOptions2 = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        getActivity(),


                        // Now we provide a list of Pair items which contain the view we can transitioning
                        // from, and the name of the view it is transitioning to, in the launched activity
                        new Pair<View, String>(v.findViewById(R.id.value),
                                DetailsActivity.VIEW_NAME_FC));

                // Now we can start the Activity, providing the activity options as a bundle
                ActivityCompat.startActivityForResult(getActivity(), intent, MainActivity.DETAILS_REQUEST, activityOptions2.toBundle());

            }
            return false;
        }
    };

    private boolean mLongClick;
    private OnItemLongClickListener mListItemLongClick = new OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            mLongClick = true;
            return false;
        }
    };

    public void refresh() {
        if (mAdapter != null)
            mAdapter.notifyDataSetChanged();
    }

    public void removeLastData() {
        removeData(mGroupPosition, mChildPosition);
    }
}