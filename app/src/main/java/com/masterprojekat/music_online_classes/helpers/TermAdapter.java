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
import com.masterprojekat.music_online_classes.APIs.RetrofitService;
import com.masterprojekat.music_online_classes.APIs.TermAPI;
import com.masterprojekat.music_online_classes.R;
import com.masterprojekat.music_online_classes.VideoCall;
import com.masterprojekat.music_online_classes.models.CourseProgress;
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
    private final CourseProgressAPI courseProgressAPI = retrofitService.getRetrofit().create(CourseProgressAPI.class);
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
            classHeld(holder, term);
        });

        holder.classNotHeld.setOnClickListener(v -> {
            classNotHeld(holder, term);
        });


        holder.videoCallButton.setOnClickListener(v -> {
            Intent videoCallIntent = new Intent(holder.itemView.getContext(), VideoCall.class);
            videoCallIntent.putExtra("loggedInUser", loggedInUser);
            videoCallIntent.putExtra("selectedTerm", term);
            holder.itemView.getContext().startActivity(videoCallIntent);
        });
    }

    private void classHeld(@NonNull TermViewHolder holder, Term term) {
        courseProgressAPI.markClassHeld(term.getTermId()).enqueue(new Callback<CourseProgress>() {
            @Override
            public void onResponse(@NonNull Call<CourseProgress> call, @NonNull Response<CourseProgress> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(holder.itemView.getContext(), "Odgovor uspesno sacuvan!", Toast.LENGTH_SHORT).show();
                    removeTermFromList(holder.getBindingAdapterPosition());
                }
                else {
                    Toast.makeText(holder.itemView.getContext(), "Odgovor nije uspesno sacuvan", Toast.LENGTH_SHORT).show();
                    Log.w(TAG, "Greska! Odgovor nije uspesno sacuvan!" + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<CourseProgress> call, @NonNull Throwable throwable) {
                Log.e(TAG, "Greska! Odgovor nije uspesno sacuvan!", throwable);
            }
        });
    }

    private void classNotHeld(@NonNull TermViewHolder holder, Term term) {
        courseProgressAPI.markClassNotHeld(term.getTermId()).enqueue(new Callback<Term>() {
            @Override
            public void onResponse(@NonNull Call<Term> call, @NonNull Response<Term> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(holder.itemView.getContext(), "Odgovor uspesno sacuvan!", Toast.LENGTH_SHORT).show();
                    removeTermFromList(holder.getBindingAdapterPosition());
                }
                else {
                    Toast.makeText(holder.itemView.getContext(), "Odgovor nije uspesno sacuvan", Toast.LENGTH_SHORT).show();
                    Log.w(TAG, "Greska! Odgovor nije uspesno sacuvan!" + response.code());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Term> call, @NonNull Throwable throwable) {
                Log.e(TAG, "Greska! Odgovor nije uspesno sacuvan!", throwable);
            }
        });
    }

    private void removeTermFromList(int position) {
        termList.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public int getItemCount() {
        return termList.size();
    }

    public static class TermViewHolder extends RecyclerView.ViewHolder {
        TextView courseName, professorName, studentName, classesNumber, classDate, classTime;
        Button classHeld, classNotHeld;
        ImageButton videoCallButton;

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
            videoCallButton = itemView.findViewById(R.id.video_call_image);
        }
    }
}

