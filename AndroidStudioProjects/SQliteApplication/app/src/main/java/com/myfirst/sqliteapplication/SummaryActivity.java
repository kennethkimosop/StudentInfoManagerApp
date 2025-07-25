package com.myfirst.sqliteapplication;

import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SummaryActivity extends AppCompatActivity {

    DBHelper db;
    LinearLayout summaryContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_summary);

        db = new DBHelper(this);
        summaryContainer = findViewById(R.id.summaryContainer);

        Cursor students = db.getAllStudents();
        if (students.getCount() == 0) {
            Toast.makeText(this, "No students to display", Toast.LENGTH_SHORT).show();
            return;
        }

        while (students.moveToNext()) {
            int studentId = students.getInt(0);
            String name = students.getString(1);
            String contact = students.getString(2);
            String dob = students.getString(3);

            Cursor fees = db.getFeesByStudentId(studentId);

            String totalFees = "N/A", paid = "N/A", balance = "N/A", completion = "N/A";
            if (fees.moveToFirst()) {
                totalFees = fees.getString(2);
                paid = fees.getString(3);
                balance = fees.getString(4);
                completion = fees.getString(5);
            }

            String summary = "Name: " + name + "\n"
                    + "Contact: " + contact + "\n"
                    + "Date of Birth: " + dob + "\n"
                    + "Total Fees: " + totalFees + "\n"
                    + "Fees Paid: " + paid + "\n"
                    + "Balance: " + balance + "\n"
                    + "Completion Date: " + completion;

            TextView card = new TextView(this);
            card.setText(summary);
            card.setBackgroundColor(Color.parseColor("#1E1E1E"));
            card.setTextColor(Color.WHITE);
            card.setTextSize(16);
            card.setPadding(24, 24, 24, 24);
            card.setTypeface(Typeface.MONOSPACE);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            params.setMargins(8, 8, 8, 24);
            card.setLayoutParams(params);

            summaryContainer.addView(card);
        }
    }
}
