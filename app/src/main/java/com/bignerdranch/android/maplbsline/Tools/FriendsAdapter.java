package com.bignerdranch.android.maplbsline.Tools;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bignerdranch.android.maplbsline.R;
import com.bignerdranch.android.maplbsline.UsersInfo;

import java.util.List;

/**
 * Created by glossimar on 2017/8/20.
 */

public class FriendsAdapter extends RecyclerView.Adapter<FriendsAdapter.ViewHolder> {
    private Context context;
    private List<FriendsInfo> friendsInfoList;

    public FriendsAdapter (Context context, List<FriendsInfo> friendsInfoList) {
        this.context = context;
        this.friendsInfoList = friendsInfoList;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        View clientView;
        ImageView followImage;
        TextView name;
        TextView phoneNumber;

        public ViewHolder (View itemView) {
            super(itemView);
            clientView = itemView;
            followImage = (ImageView) itemView.findViewById(R.id.friends_item_follow);
            name = (TextView) itemView.findViewById(R.id.friends_item_result_name);
            phoneNumber = (TextView) itemView.findViewById(R.id.friends_item_result_number);
        }
    }
    @Override
    public FriendsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.friends_item, parent, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.clientView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = holder.getAdapterPosition();
                FriendsInfo friendsInfo = friendsInfoList.get(position);
                Intent intent = new Intent(context, UsersInfo.class);
                intent.putExtra("PhoneNumber", friendsInfo.getPhoneNumber());
                intent.putExtra("Name", friendsInfo.getName());
                context.startActivity(intent);
            }
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(FriendsAdapter.ViewHolder holder, int position) {
        holder.phoneNumber.setText(friendsInfoList.get(position).getPhoneNumber());
        holder.name.setText(friendsInfoList.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return friendsInfoList.size();
    }
}
