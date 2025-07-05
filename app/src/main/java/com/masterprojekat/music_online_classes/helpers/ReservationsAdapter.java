package com.masterprojekat.music_online_classes.helpers;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.masterprojekat.music_online_classes.APIs.CourseProgressAPI;
import com.masterprojekat.music_online_classes.APIs.NotificationAPI;
import com.masterprojekat.music_online_classes.APIs.RetrofitService;
import com.masterprojekat.music_online_classes.APIs.TermAPI;
import com.masterprojekat.music_online_classes.R;
import com.masterprojekat.music_online_classes.VideoCall;
import com.masterprojekat.music_online_classes.models.Term;
import com.masterprojekat.music_online_classes.models.User;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReservationsAdapter extends RecyclerView.Adapter<ReservationsAdapter.ReservationsViewHolder> {
    private static final String TAG = "ReservationsAdapter";
    private final RetrofitService retrofitService = new RetrofitService();
    private final TermAPI termApi = retrofitService.getRetrofit().create(TermAPI.class);
    private final NotificationAPI notificationAPI = retrofitService.getRetrofit().create(NotificationAPI.class);
    private final List<Term> reservationsList;

    public ReservationsAdapter(List<Term> reservationsList) {
        this.reservationsList = reservationsList;
    }
    @NonNull
    @Override
    public ReservationsAdapter.ReservationsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.reservation_response_item, parent, false);
        return new ReservationsViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ReservationsAdapter.ReservationsViewHolder holder, int position) {
        Term term = reservationsList.get(position);
        holder.courseName.setText(term.getCourse().getName());
        holder.professorName.setText(term.getCourse().getProfessor().getName() + " " + term.getCourse().getProfessor().getSurname());
        holder.studentName.setText(term.getStudent().getName() + " " + term.getStudent().getSurname());
        holder.classesNumber.setText(String.valueOf(term.getCourse().getNumberOfClasses()));
        String displayDateFormat = DateTimeFormatParser.changeDateFormatTo(term.getDate(),"yyyy-MM-dd", "dd-MM-yyyy");
        holder.classDate.setText(displayDateFormat);
        String displayTimeFormat = DateTimeFormatParser.changeTimeFormatTo(term.getTime(), "HH:mm:ss", "HH:mm");
        holder.classTime.setText(displayTimeFormat);

        holder.acceptButton.setOnClickListener(view -> {
            acceptReservation(holder, term);
        });

        holder.declineButton.setOnClickListener(view -> {
            declineReservation(holder, term);
        });
    }

    private void acceptReservation(@NonNull ReservationsViewHolder holder, Term term) {
        termApi.acceptTerm(term.getTermId()).enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull retrofit2.Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(holder.itemView.getContext(), "Odgovor uspesno sacuvan!", Toast.LENGTH_SHORT).show();
                    removeReservationFromList(holder.getBindingAdapterPosition());
                    sendNotification(term, "prihvatio/la");
                }
                else {
                    Toast.makeText(holder.itemView.getContext(), "Odgovor nije uspesno sacuvan", Toast.LENGTH_SHORT).show();
                    Log.w(TAG, "Greska pri prihvatanju termina! Odgovor nije uspesno sacuvan!" + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable throwable) {
                Log.e(TAG, "Greska pri prihvatanju termina! Odgovor nije uspesno sacuvan!", throwable);
            }
        });
    }

    private void declineReservation(@NonNull ReservationsViewHolder holder, Term term) {
        termApi.rejectTerm(term.getTermId()).enqueue(new Callback<ResponseBody>() {

            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull retrofit2.Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(holder.itemView.getContext(), "Odgovor uspesno sacuvan!", Toast.LENGTH_SHORT).show();
                    removeReservationFromList(holder.getBindingAdapterPosition());
                    sendNotification(term, "odbio/la");
                }
                else {
                    Toast.makeText(holder.itemView.getContext(), "Odgovor nije uspesno sacuvan", Toast.LENGTH_SHORT).show();
                    Log.w(TAG, "Greska pri odbijanju termina! Odgovor nije uspesno sacuvan!" + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ResponseBody> call, @NonNull Throwable throwable) {
                Log.e(TAG, "Greska pri odbijanju termina! Odgovor nije uspesno sacuvan!", throwable);
            }
        });
    }

    private void sendNotification(Term term, String message) {
        notificationAPI.createNewNotification(term.getTermId(), message).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call, @NonNull Response<Void> response) {
                if (response.isSuccessful()) {
                    Log.i(TAG, "Notifikacija uspesno poslata!" + response.code());
                }
                else {
                    Log.w(TAG, "Greska pri slanju notifikacije!" + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable throwable) {
                Log.e(TAG, "Greska pri slanju notifikacije!", throwable);
            }
        });
    }

    private void removeReservationFromList(int position) {
        reservationsList.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public int getItemCount() {
        return reservationsList.size();
    }

    public static class ReservationsViewHolder extends RecyclerView.ViewHolder {
        TextView courseName, professorName, studentName, classesNumber, classDate, classTime;
        Button acceptButton, declineButton;

        public ReservationsViewHolder(@NonNull View itemView) {
            super(itemView);
            courseName = itemView.findViewById(R.id.course_name);
            professorName = itemView.findViewById(R.id.professor_name);
            studentName = itemView.findViewById(R.id.student_name);
            classesNumber = itemView.findViewById(R.id.classes_number);
            classDate = itemView.findViewById(R.id.term_date);
            classTime = itemView.findViewById(R.id.term_time);
            acceptButton = itemView.findViewById(R.id.acceptButton);
            declineButton = itemView.findViewById(R.id.declineButton);
        }
    }
}
