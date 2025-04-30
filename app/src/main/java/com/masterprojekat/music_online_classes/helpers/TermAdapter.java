package com.masterprojekat.music_online_classes.helpers;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.masterprojekat.music_online_classes.R;
import com.masterprojekat.music_online_classes.models.Term;

import java.util.List;

public class TermAdapter extends RecyclerView.Adapter<TermAdapter.TermViewHolder> {

    private List<Term> termList;

    public TermAdapter(List<Term> termList) {
        System.out.println("Adapter configured");
        this.termList = termList;
    }

    @NonNull
    @Override
    public TermViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        System.out.println("Added items view");
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

    }

    @Override
    public int getItemCount() {
        return termList.size();
    }

    public static class TermViewHolder extends RecyclerView.ViewHolder {
        TextView courseName, professorName, studentName, classesNumber, classDate, classTime;

        public TermViewHolder(@NonNull View itemView) {
            super(itemView);
            courseName = itemView.findViewById(R.id.course_name);
            professorName = itemView.findViewById(R.id.professor_name);
            studentName = itemView.findViewById(R.id.student_name);
            classesNumber = itemView.findViewById(R.id.classes_number);
            classDate = itemView.findViewById(R.id.term_date);
            classTime = itemView.findViewById(R.id.term_time);
        }
    }
}

