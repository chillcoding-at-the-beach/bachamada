package fr.machada.bpm.pro.view;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import fr.machada.bpm.pro.R;
import fr.machada.bpm.pro.model.CustomExpandableListAdapter;
import fr.machada.bpm.pro.model.RegisteredFC;
import fr.machada.bpm.pro.utils.SomeKeys;

public class HistoryFragment extends Fragment {
    // more efficient than HashMap for mapping integers to objects
    CustomExpandableListAdapter mAdapter;
    ExpandableListView mListView;
    private TextView mTextView;
    private boolean mNoData;

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
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAdapter != null)
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

            /**AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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
             builder.create().show();**/


            Toast.makeText(getContext(), R.string.text_default, Toast.LENGTH_LONG).show();
            return false;
        }

    };


    public void refresh() {
        if (mAdapter != null)
            mAdapter.notifyDataSetChanged();
    }


}