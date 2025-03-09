package com.masterprojekat.music_online_classes.helpers;

import android.app.DatePickerDialog;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;

import com.masterprojekat.music_online_classes.R;
import com.masterprojekat.music_online_classes.Registration;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class Spinners {
    static final Calendar calendar = Calendar.getInstance();
    public static void showDateSpinner(android.content.Context context, EditText inputDate) {
        inputDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day_of_month) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, month);
                        calendar.set(Calendar.DAY_OF_MONTH, day_of_month);

                        String format = "dd-MM-yyyy";
                        SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.US);
                        inputDate.setText(dateFormat.format(calendar.getTime()));
                    }
                }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    public static void showExpertiseSpinner(android.content.Context context, Spinner inputExpertiseSpinner) {
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context, R.array.expertise_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        inputExpertiseSpinner.setAdapter(adapter);
    }
}