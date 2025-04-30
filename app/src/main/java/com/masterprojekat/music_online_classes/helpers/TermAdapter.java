package com.masterprojekat.music_online_classes.helpers;

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

import com.masterprojekat.music_online_classes.APIs.RetrofitService;
import com.masterprojekat.music_online_classes.APIs.TermAPI;
import com.masterprojekat.music_online_classes.R;
import com.masterprojekat.music_online_classes.models.Term;
import com.masterprojekat.music_online_classes.models.User;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TermAdapter extends RecyclerView.Adapter<TermAdapter.TermViewHolder> {
    private static final String TAG = "TermAdapter";
    private final RetrofitService retrofitService = new RetrofitService();
    private final TermAPI termApi = retrofitService.getRetrofit().create(TermAPI.class);
    private final List<Term> termList;
    private final User loggedInUser;

    public TermAdapter(List<Term> termList, User loggedInUser) {
        this.termList = termList;
        this.loggedInUser = loggedInUser;
    }

    @NonNull
    @Override
    public TermViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.scheduled_term_item, parent, false);
        return new TermViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull TermViewHolder holder, int position) {
        Term term = termList.get(position);
        holder.courseName.setText(term.getCourse().getName());
        holder.professorName.setText(term.getCourse().getProfessor().getName() + " " + term.getCourse().getProfessor().getSurname());
        holder.studentName.setText(term.getStudent().getName() + " " + term.getStudent().getSurname());
        holder.classesNumber.setText(String.valueOf(term.getCourse().getNumberOfClasses()));
        String displayDateFormat = DateTimeFormatParser.changeDateFormatTo(term.getDate(),"yyyy-MM-dd", "dd-MM-yyyy");
        holder.classDate.setText(displayDateFormat);
        String displayTimeFormat = DateTimeFormatParser.changeTimeFormatTo(term.getTime(), "HH:mm:ss", "HH:mm");
        holder.classTime.setText(displayTimeFormat);

        if(loggedInUser.getType().equals("Profesor")) {
            holder.classHeld.setVisibility(View.VISIBLE);
            holder.classNotHeld.setVisibility(View.VISIBLE);
        }
        else {
            holder.classHeld.setVisibility(View.GONE);
            holder.classNotHeld.setVisibility(View.GONE);
        }

        holder.classHeld.setOnClickListener(v -> {
            acceptClassTerm(holder, term);
        });

        holder.classNotHeld.setOnClickListener(v -> {
            declineClassTerm(holder, term);
        });
    }

    private void acceptClassTerm(@NonNull TermViewHolder holder, Term term) {
        termApi.acceptTerm(term.getTermId()).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful())
                    Toast.makeText(holder.itemView.getContext(), "Odgovor uspesno sacuvan!", Toast.LENGTH_SHORT).show();
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

    private void declineClassTerm(@NonNull TermViewHolder holder, Term term) {
        termApi.rejectTerm(term.getTermId()).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful())
                    Toast.makeText(holder.itemView.getContext(), "Odgovor uspesno sacuvan!", Toast.LENGTH_SHORT).show();
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

    @Override
    public int getItemCount() {
        return termList.size();
    }

    public static class TermViewHolder extends RecyclerView.ViewHolder {
        TextView courseName, professorName, studentName, classesNumber, classDate, classTime;
        Button classHeld, classNotHeld;

        public TermViewHolder(@NonNull View itemView) {
            super(itemView);
            courseName = itemView.findViewById(R.id.course_name);
            professorName = itemView.findViewById(R.id.professor_name);
            studentName = itemView.findViewById(R.id.student_name);
            classesNumber = itemView.findViewById(R.id.classes_number);
            classDate = itemView.findViewById(R.id.term_date);
            classTime = itemView.findViewById(R.id.term_time);
            classHeld = itemView.findViewById(R.id.class_held);
            classNotHeld = itemView.findViewById(R.id.class_not_held);
        }
    }
}

