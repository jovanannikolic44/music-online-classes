package com.masterprojekat.music_online_classes.adapters.helpers;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.masterprojekat.music_online_classes.api.RetrofitService;
import com.masterprojekat.music_online_classes.api.UserAPI;
import com.masterprojekat.music_online_classes.R;
import com.masterprojekat.music_online_classes.models.User;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewUsersAdapter extends RecyclerView.Adapter<NewUsersAdapter.NewUsersViewHolder>{
    private static final String TAG = "NewUsersAdapter";
    private final RetrofitService retrofitService = new RetrofitService();
    private final UserAPI userAPI = retrofitService.getRetrofit().create(UserAPI.class);
    private final List<User> userRequestsList;

    public NewUsersAdapter(List<User> userRequestsList) {
        this.userRequestsList = userRequestsList;
    }
    @NonNull
    @Override
    public NewUsersAdapter.NewUsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.new_user_item, parent, false);
        return new NewUsersAdapter.NewUsersViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull NewUsersAdapter.NewUsersViewHolder holder, int position) {
        User user = userRequestsList.get(position);
        holder.newUserName.setText(user.getName());
        holder.newUserSurname.setText(user.getSurname());
        holder.newUserUsername.setText(user.getUsername());
        holder.newUserAccountType.setText(user.getType());

        holder.acceptButton.setOnClickListener(view -> {
            acceptRequest(holder, user);
        });

        holder.declineButton.setOnClickListener(view -> {
            declineRequest(holder, user);
        });
    }

    private void acceptRequest(@NonNull NewUsersAdapter.NewUsersViewHolder holder, User user) {
        userAPI.acceptRequest(user.getUsername()).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(holder.itemView.getContext(), "Odgovor uspesno sacuvan!", Toast.LENGTH_SHORT).show();
                    removeUserRequestFromList(holder.getBindingAdapterPosition());
                }
                else {
                    Toast.makeText(holder.itemView.getContext(), "Odgovor nije uspesno sacuvan", Toast.LENGTH_SHORT).show();
                    Log.w(TAG, "Greska pri odobravanju novog naloga! Odgovor nije uspesno sacuvan!" + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable throwable) {
                Log.e(TAG, "Greska pri odobravanju novog naloga! Odgovor nije uspesno sacuvan!", throwable);
            }
        });
    }

    private void declineRequest(@NonNull NewUsersAdapter.NewUsersViewHolder holder, User user) {
        userAPI.declineRequest(user.getUsername()).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(holder.itemView.getContext(), "Odgovor uspesno sacuvan!", Toast.LENGTH_SHORT).show();
                    removeUserRequestFromList(holder.getBindingAdapterPosition());
                }
                else {
                    Toast.makeText(holder.itemView.getContext(), "Odgovor nije uspesno sacuvan", Toast.LENGTH_SHORT).show();
                    Log.w(TAG, "Greska pri odbijanju novog naloga! Odgovor nije uspesno sacuvan!" + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable throwable) {
                Log.e(TAG, "Greska pri odbijanju novog naloga! Odgovor nije uspesno sacuvan!", throwable);
            }
        });
    }

    private void removeUserRequestFromList(int position) {
        userRequestsList.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public int getItemCount() {
        return userRequestsList.size();
    }

    public static class NewUsersViewHolder extends RecyclerView.ViewHolder {
        TextView newUserName, newUserSurname, newUserUsername, newUserAccountType;
        Button acceptButton, declineButton;

        public NewUsersViewHolder(@NonNull View itemView) {
            super(itemView);
            newUserName = itemView.findViewById(R.id.new_user_name);
            newUserSurname = itemView.findViewById(R.id.new_user_surname);
            newUserUsername = itemView.findViewById(R.id.new_user_username);
            newUserAccountType = itemView.findViewById(R.id.new_user_account_type);
            acceptButton = itemView.findViewById(R.id.acceptButton);
            declineButton = itemView.findViewById(R.id.declineButton);
        }
    }
}
