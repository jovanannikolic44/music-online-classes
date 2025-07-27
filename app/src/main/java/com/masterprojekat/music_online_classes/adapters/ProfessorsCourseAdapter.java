package com.masterprojekat.music_online_classes.adapters.helpers;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.masterprojekat.music_online_classes.api.CourseAPI;
import com.masterprojekat.music_online_classes.api.RetrofitService;
import com.masterprojekat.music_online_classes.R;
import com.masterprojekat.music_online_classes.utils.Spinners;
import com.masterprojekat.music_online_classes.models.Course;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfessorsCourseAdapter extends RecyclerView.Adapter<ProfessorsCourseAdapter.ProfessorsCourseViewHolder> {
    private static final String TAG = "ProfessorsCourseAdapter";
    private final RetrofitService retrofitService = new RetrofitService();
    private final CourseAPI courseApi = retrofitService.getRetrofit().create(CourseAPI.class);
    private final Context context;
    private final List<Course> coursesList;

    public ProfessorsCourseAdapter(Context context, List<Course> coursesList) {
        this.context = context;
        this.coursesList = (coursesList != null) ? coursesList : new ArrayList<>();
    }

    @NonNull
    @Override
    public ProfessorsCourseAdapter.ProfessorsCourseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.professors_course_item, parent, false);
        return new ProfessorsCourseAdapter.ProfessorsCourseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfessorsCourseViewHolder holder, int position) {
        Course course = coursesList.get(position);
        holder.courseName.setText(course.getName());

        displayImage(holder, course.getCourseImage());

        holder.editCourse.setOnClickListener(view -> {
            editCoursesInfo(course, position);
        });
    }

    private void editCoursesInfo(Course courseToEdit, int position) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.edit_course_information_dialog, null);

        EditText changeName = dialogView.findViewById(R.id.change_course_name);
        changeName.setText(courseToEdit.getName());
        EditText changePrice = dialogView.findViewById(R.id.change_course_price);
        changePrice.setText(String.valueOf(courseToEdit.getPrice()));
        EditText changeNumberOfClasses = dialogView.findViewById(R.id.change_course_classes_number);
        changeNumberOfClasses.setText(String.valueOf(courseToEdit.getNumberOfClasses()));
        EditText changeDescription = dialogView.findViewById(R.id.change_course_description);
        changeDescription.setText(courseToEdit.getDescription());

        EditText changeContent = dialogView.findViewById(R.id.change_course_content);
        String content = displayContentWithNewLine(String.valueOf(courseToEdit.getContent()));
        changeContent.setText(content);

        Spinner changeCourseLevelSpinner = dialogView.findViewById(R.id.change_course_level);
        Spinners.showLevelSpinner(context, changeCourseLevelSpinner);
        String currentLevel = courseToEdit.getLevel();
        setSpinnerSelectionByValue(changeCourseLevelSpinner, currentLevel);

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Promena informacija o kursu")
                .setView(dialogView)
                .setPositiveButton("Sacuvaj", null)
                .setNegativeButton("Otkazi", null);

        AlertDialog dialog = builder.create();
        dialog.show();

        Window window = dialog.getWindow();
        if (window != null) {
            window.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#D4BEE4")));
        }
        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(v -> {
            String newName = String.valueOf(changeName.getText());
            String newPrice = String.valueOf(changePrice.getText());
            String newNumberOfClasses = String.valueOf(changeNumberOfClasses.getText());
            String newDescription = String.valueOf(changeDescription.getText());
            String newContent = String.valueOf(changeContent.getText());
            String newContentWithCommas = saveStringWithCommas(newContent);
            String newLevel = String.valueOf(changeCourseLevelSpinner.getSelectedItem());

            courseToEdit.setName(newName);
            courseToEdit.setPrice(Float.parseFloat(newPrice));
            courseToEdit.setNumberOfClasses(Integer.parseInt(newNumberOfClasses));
            courseToEdit.setDescription(newDescription);
            courseToEdit.setContent(newContentWithCommas);
            courseToEdit.setLevel(newLevel);

            courseApi.updateCourseInfo(courseToEdit).enqueue(new Callback<Course>() {
                @Override
                public void onResponse(@NonNull Call<Course> call, @NonNull Response<Course> response) {
                    if(response.isSuccessful() && response.body() != null) {
                        Toast.makeText(context, "Podaci o kursu su uspesno izmenjeni!", Toast.LENGTH_SHORT).show();
                        Log.i(TAG, "Podaci o kursu su uspesno izmenjeni!" + response.code());
                        
                        coursesList.set(position, courseToEdit);
                        notifyItemChanged(position);
                        dialog.dismiss();
                    }
                    else {
                        Toast.makeText(context, "Greska pri izmeni podataka o kursu!", Toast.LENGTH_SHORT).show();
                        Log.w(TAG, "Greska pri izmeni podataka o kursu!" + response.code());
                        dialog.dismiss();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Course> call, @NonNull Throwable throwable) {
                    Log.e(TAG, "Zahtev za izmenom podataka o kursu nije uspeo!", throwable);
                }
            });
        });

    }

    private void displayImage(ProfessorsCourseAdapter.ProfessorsCourseViewHolder holder, String fullImagePath) {
        String imageFile = fullImagePath.substring(fullImagePath.lastIndexOf("/") + 1);
        courseApi.getCourseImage(imageFile).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(@NonNull Call<ResponseBody> call, @NonNull Response<ResponseBody> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Bitmap bitmap = BitmapFactory.decodeStream(response.body().byteStream());

                    Glide.with(context)
                            .load(bitmap)
                            .into(holder.courseImage);
                } else {
                    holder.courseImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.broken_image));
                }
            }

            @Override
            public void onFailure(@NonNull retrofit2.Call<ResponseBody> call, @NonNull Throwable throwable) {
                Logger.getLogger(ProfessorsCourseAdapter.class.getName()).log(Level.SEVERE, "Greska! Zahtev za dohvatanjem slike kursa nije uspeo!", throwable);
            }
        });
    }

    private String displayContentWithNewLine(String content) {
        String[] lines = content.split(",");
        StringBuilder sb = new StringBuilder();
        for (String line : lines) {
            if (!line.trim().isEmpty()) {
                sb.append(line.trim()).append("\n");
            }
        }
        return sb.toString().trim();
    }

    private String saveStringWithCommas(String content) {
        String[] lines = content.split("\\r?\\n");
        StringBuilder sb = new StringBuilder();
        for (String line : lines) {
            if (!line.trim().isEmpty()) {
                if (sb.length() > 0) sb.append(",");
                sb.append(line.trim());
            }
        }
        return sb.toString().trim();
    }

    private void setSpinnerSelectionByValue(Spinner spinner, String valueToSelect) {
        if (spinner == null || valueToSelect == null) return;
        if (!(spinner.getAdapter() instanceof ArrayAdapter<?>)) return;
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinner.getAdapter();
        int position = adapter.getPosition(valueToSelect);
        if (position >= 0) {
            spinner.setSelection(position);
        }
    }


    @Override
    public int getItemCount() {
        return coursesList.size();
    }

    public static class ProfessorsCourseViewHolder extends RecyclerView.ViewHolder {
        ImageView courseImage;
        TextView courseName;
        ImageButton editCourse;

        public ProfessorsCourseViewHolder(@NonNull View itemView) {
            super(itemView);
            courseImage = itemView.findViewById(R.id.course_image);
            courseName = itemView.findViewById(R.id.course_name);
            editCourse = itemView.findViewById(R.id.edit_courses_button);
        }
    }
}
