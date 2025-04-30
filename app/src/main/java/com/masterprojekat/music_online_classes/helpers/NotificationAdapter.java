package com.masterprojekat.music_online_classes.helpers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.masterprojekat.music_online_classes.R;
import com.masterprojekat.music_online_classes.models.Notification;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>{
    private final List<Notification> notificationsList;
    private Context context;

    public NotificationAdapter(Context context, List<Notification> notifications) {
        this.context = context;
        this.notificationsList = notifications;
    }

    @NonNull
    @Override
    public NotificationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.notification_item, parent, false);
        return new NotificationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotificationViewHolder holder, int position) {
        Notification item = notificationsList.get(position);
        holder.notificationMessage.setText(item.getMessage());
        holder.notificationTime.setText(item.getTime());

        if (position == 0 || !item.getDate().equals(notificationsList.get(position - 1).getDate())) {
            holder.notificationDate.setVisibility(View.VISIBLE);
            holder.notificationDate.setText(item.getDate());
        } else {
            holder.notificationDate.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return notificationsList.size();
    }

    public static class NotificationViewHolder extends RecyclerView.ViewHolder {
        TextView notificationDate, notificationMessage, notificationTime;

        public NotificationViewHolder(@NonNull View itemView) {
            super(itemView);
            notificationDate = itemView.findViewById(R.id.notification_date);
            notificationMessage = itemView.findViewById(R.id.notification_message);
            notificationTime = itemView.findViewById(R.id.notification_time);
        }
    }
}
