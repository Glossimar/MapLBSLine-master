package com.bignerdranch.android.maplbsline.Tools;

import android.content.Context;
import android.content.Intent;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.bignerdranch.android.maplbsline.R;
import com.bignerdranch.android.maplbsline.TrackMap;


/**
 * Created by glossimar on 2017/8/21.
 */

public class ExpandAdapter extends BaseExpandableListAdapter {
    private Context context;
    private SetNameListener listener;

    private String groupName = "轨迹动态";
    private String[] childName = {"今日轨迹", "昨日轨迹"};

    public ExpandAdapter(Context context, SetNameListener listener) {
        this.context = context;
        this.listener = listener;
    }
    @Override
    public int getGroupCount() {
        return 1;
    }

    @Override
    public int getChildrenCount(int i) {
        return 2;
    }

    @Override
    public Object getGroup(int i) {
        return groupName;
    }

    @Override
    public Object getChild(int i, int i1) {
        return childName[i1];
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        View v= LayoutInflater.from(context).inflate(R.layout.expand_group, null);
        TextView groupNameView = (TextView) v.findViewById(R.id.expand_group_groupName);
        final ImageView selectedImage = (ImageView) v.findViewById(R.id.expand_group_toMap);
        groupNameView.setText(groupName);
        if (b) {
            selectedImage.setBackgroundResource(R.drawable.expanded_indicator);
        } else {
            selectedImage.setBackgroundResource(R.drawable.go_map);
        }
        return v;
    }

    @Override
    public View getChildView(int i, final int i1, boolean b, View view, ViewGroup viewGroup) {
        final View v = LayoutInflater.from(context).inflate(R.layout.expand_item, null);
        TextView childView = (TextView) v.findViewById(R.id.expand_item_text);
        TextView dividerLine = (TextView) v.findViewById(R.id.expand_item_divider);
        childView.setText(childName[i1]);
        if (i1 == 1) {
            dividerLine.setVisibility(View.VISIBLE);
        }
        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, TrackMap.class);
                if (i1 == 0) {
                    intent.putExtra("Date", "today");
                } else if (i1 == 1){
                    intent.putExtra("Date", "yesterday");
                }
                listener.onFinish(intent);
                context.startActivity(intent);
            }
        });
        return v;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }
}
