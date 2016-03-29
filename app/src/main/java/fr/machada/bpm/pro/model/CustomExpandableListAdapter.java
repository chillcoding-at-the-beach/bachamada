package fr.machada.bpm.pro.model;


import android.app.Activity;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.TextView;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;

import fr.machada.bpm.pro.R;


public class CustomExpandableListAdapter extends BaseExpandableListAdapter {

    private SparseArray<Group> mGroups;
    public LayoutInflater inflater;
    public Activity activity;

    public CustomExpandableListAdapter(Activity act, List<RegisteredFC> bpmList) {
        activity = act;
        createData(bpmList);
        inflater = act.getLayoutInflater();
    }

    public void createData(List<RegisteredFC> bpmList) {
        mGroups = null;
        mGroups = new SparseArray<Group>();
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM y");
        int c = 0;
        Date dc = null;
        Group group = null;
        if (bpmList.size() > 0) {
            dc = new Date(bpmList.get(0).getDate());
            group = new Group(sdf.format(dc));
            for (int i = 0; i < bpmList.size(); i++) {
                // to get a nice title for expandable list
                Date di = new Date(bpmList.get(i).getDate());
                if (dc.getMonth() == di.getMonth()) {
                    group.children.add(bpmList.get(i));
                } else {
                    mGroups.append(c, group);
                    dc = di;
                    group = new Group(sdf.format(dc));
                    c++;
                    group.children.add(bpmList.get(i));
                }
            }
            mGroups.append(c, group);
        }
    }

    public void removeFC(int gp, int cp) {
        mGroups.get(gp).children.remove(cp);
        if (mGroups.get(gp).children.size() < 1) {
            mGroups.remove(gp);
        }
    }

    public boolean addFC(RegisteredFC fc) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy");
        Date d1 = new Date(fc.getDate());
        if (mGroups.size() > 0) {
            int l1 = mGroups.size() - 1;
            int l2 = mGroups.get(l1).children.size() - 1;
            Date dc = new Date(mGroups.get(l1).children.get(l2).getDate());
            if (d1.getMonth() == dc.getMonth()) {
                mGroups.get(l1).children.add(fc);
            } else {
                Group group = new Group(sdf.format(d1));
                group.children.add(fc);
                mGroups.append(mGroups.size(), group);
                return true;
            }
        } else {
            mGroups = null;
            mGroups = new SparseArray<Group>();
            Group group = new Group(sdf.format(d1));
            group.children.add(fc);
            mGroups.append(mGroups.size(), group);
            return true;
        }
        return false;
    }


    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mGroups.get(groupPosition).children.get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        RegisteredFC fc = (RegisteredFC) getChild(groupPosition, childPosition);
        return fc.getId();
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        final RegisteredFC children = (RegisteredFC) getChild(groupPosition, childPosition);
        TextView text, textD, textP = null;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.listrow_details, null);
        }
        text = (TextView) convertView.findViewById(R.id.value);
        textD = (TextView) convertView.findViewById(R.id.date);
        textP = (TextView) convertView.findViewById(R.id.percent);
        ImageView imE, imH;
        imE = (ImageView) convertView.findViewById(R.id.hiseffort);
        imH = (ImageView) convertView.findViewById(R.id.hishow);
        if (children != null) {
            text.setText(String.format("%d", children.getValue()));
            if (children.getPercent() != 0)
                textP.setText(String.format("%d %%", children.getPercent()));

            SimpleDateFormat sdf = new SimpleDateFormat("EEEE dd  HH:mm");
            Date resultDate = new Date(children.getDate());
            textD.setText(sdf.format(resultDate));

            How ho = How.values()[children.getHow()];
            switch (ho) {
                case HAPPY:
                    imH.setImageResource(R.drawable.ic_happy);
                    break;
                case NEUTRAL:
                    imH.setImageResource(R.drawable.ic_neutral);
                    break;
                case SAD:
                    imH.setImageResource(R.drawable.ic_sad);
                    break;
                default:
                    imH.setImageResource(R.drawable.ic_happy);
            }
            Effort ef = Effort.values()[children.getEffort()];
            switch (ef) {
                case GURU:
                    imE.setImageResource(R.drawable.ic_guru);
                    break;
                case WALKING:
                    imE.setImageResource(R.drawable.ic_walking);
                    break;
                case INTERVAL:
                    imE.setImageResource(R.drawable.ic_interval);
                    break;
                case EXERCISE:
                    imE.setImageResource(R.drawable.ic_exercise);
                    break;
                default:
                    imE.setImageResource(R.drawable.ic_guru);
            }
        }
        return convertView;
    }

    /***
     * EventBus.getDefault().post(new OnDeleteFCEvent(mGroups.get(groupPosition).children.get(childPosition).getId()));
     * removeFC(groupPosition, childPosition);
     * notifyDataSetChanged();
     **/


    @Override
    public int getChildrenCount(int groupPosition) {
        if (mGroups != null) {
            if (mGroups.get(groupPosition) != null)
                return mGroups.get(groupPosition).children.size();
        }
        return 0;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return mGroups.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        if (mGroups != null)
            return mGroups.size();
        else return 0;
    }

    @Override
    public void onGroupCollapsed(int groupPosition) {
        super.onGroupCollapsed(groupPosition);
    }

    @Override
    public void onGroupExpanded(int groupPosition) {
        super.onGroupExpanded(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.listrow_group, null);
        }
        Group group = (Group) getGroup(groupPosition);
        ((CheckedTextView) convertView).setText(group.string);
        ((CheckedTextView) convertView).setChecked(isExpanded);
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

} 
