package com.masterprojekat.music_online_classes.helpers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.masterprojekat.music_online_classes.APIs.CourseAPI;
import com.masterprojekat.music_online_classes.APIs.RetrofitService;
import com.masterprojekat.music_online_classes.R;
import com.masterprojekat.music_online_classes.models.Course;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    private final RetrofitService retrofitService = new RetrofitService();
    private final CourseAPI courseApi = retrofitService.getRetrofit().create(CourseAPI.class);
    private final Context context;
    private List<Course> courseList;
    private OnSelectionChangedListener selectionChangedListener;

    public CartAdapter(Context context, List<Course> courseList) {
        this.context = context;
        this.courseList = courseList;
    }

    public void setOnSelectionChangedListener(OnSelectionChangedListener listener) {
        this.selectionChangedListener = listener;
    }

    public interface OnSelectionChangedListener {
        // Callback interface to communicate the total price change from adapter to Cart
        void onSelectionChanged(float totalPrice);
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.cart_item, parent, false);
        return new CartViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        Course course = courseList.get(position);

        holder.courseName.setText(course.getName());
        holder.professorName.setText(course.getProfessor().getName() + " " + course.getProfessor().getSurname());
        holder.coursePrice.setText("RSD" + course.getPrice());

        displayImage(holder, course.getCourseImage());

        holder.selectCourseCheckbox.setChecked(course.isSelected());
        holder.selectCourseCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            course.setSelected(isChecked);
            if (selectionChangedListener != null) {
                float total = calculateSelectedTotal();
                selectionChangedListener.onSelectionChanged(total);
            }
        });
    }

    public List<Course> getSelectedCourses() {
        List<Course> selected = new ArrayList<>();
        for (Course course : courseList) {
            if (course.isSelected()) {
                selected.add(course);
            }
        }
        return selected;
    }

    public float calculateSelectedTotal() {
        float totalPrice = 0;
        for (Course course : courseList) {
            if (course.isSelected()) {
                totalPrice += course.getPrice();
            }
        }
        return totalPrice;
    }

    public void displayImage(CartAdapter.CartViewHolder holder, String fullImagePath) {
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
                Logger.getLogger(CartAdapter.class.getName()).log(Level.SEVERE, "Greska! Zahtev za dohvatanjem slike kursa nije uspeo!", throwable);
            }
        });
    }


    @Override
    public int getItemCount() {
        return courseList.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {

        ImageView courseImage;
        TextView courseName, professorName, coursePrice;
        CheckBox selectCourseCheckbox;

        public CartViewHolder(View itemView) {
            super(itemView);
            courseImage = itemView.findViewById(R.id.course_image);
            courseName = itemView.findViewById(R.id.course_name);
            professorName = itemView.findViewById(R.id.professor_name);
            coursePrice = itemView.findViewById(R.id.course_price);
            selectCourseCheckbox = itemView.findViewById(R.id.select_course_checkbox);
        }
    }
}

