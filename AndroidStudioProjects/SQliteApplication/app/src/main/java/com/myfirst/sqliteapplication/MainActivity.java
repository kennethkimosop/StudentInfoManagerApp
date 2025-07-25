package com.myfirst.sqliteapplication;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import android.content.Intent;
import android.widget.RelativeLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    EditText name, contact, dateOfBirth;
    Button insert, update, delete, view;
    DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        name = findViewById(R.id.name);
        contact = findViewById(R.id.contact);
        dateOfBirth = findViewById(R.id.dateOfBirth);
        insert = findViewById(R.id.btnInsert);
        update = findViewById(R.id.btnUpdate);
        delete = findViewById(R.id.btnDelete);
        view = findViewById(R.id.btnView);
        db = new DBHelper(this);

        dateOfBirth.setFocusable(false);
        dateOfBirth.setOnClickListener(view -> {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dpd = new DatePickerDialog(MainActivity.this,
                    (view1, year1, month1, dayOfMonth) -> {
                        String date = String.format("%02d/%02d/%d", dayOfMonth, month1 + 1, year1);
                        dateOfBirth.setText(date);
                    }, year, month, day);
            dpd.show();
        });

        insert.setOnClickListener(view -> {
            String nameTXT = name.getText().toString().trim();
            String contactTXT = contact.getText().toString().trim();
            String dobTXT = dateOfBirth.getText().toString().trim();

            if (nameTXT.isEmpty() || contactTXT.isEmpty() || dobTXT.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (contactTXT.length() != 10 || !contactTXT.matches("\\d{10}")) {
                Toast.makeText(this, "Phone number must be exactly 10 digits", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean success = db.insertStudent(nameTXT, contactTXT, dobTXT);
            Toast.makeText(this, success ? "New student added" : "Failed to add student", Toast.LENGTH_SHORT).show();
        });

        update.setOnClickListener(view -> {
            String nameTXT = name.getText().toString().trim();
            String contactTXT = contact.getText().toString().trim();
            String dobTXT = dateOfBirth.getText().toString().trim();

            if (nameTXT.isEmpty() || contactTXT.isEmpty() || dobTXT.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean success = db.updateStudent(nameTXT, contactTXT, dobTXT);
            Toast.makeText(this, success ? "Student updated" : "Student not found or failed to update", Toast.LENGTH_SHORT).show();
        });

        delete.setOnClickListener(view -> {
            String nameTXT = name.getText().toString().trim();
            if (nameTXT.isEmpty()) {
                Toast.makeText(this, "Enter a student name to delete", Toast.LENGTH_SHORT).show();
                return;
            }
            boolean success = db.deleteStudent(nameTXT);
            Toast.makeText(this, success ? "Student deleted" : "Student not found", Toast.LENGTH_SHORT).show();
        });

        view.setOnClickListener(view -> {
            Cursor res = db.getSummaryData();
            if (res.getCount() == 0) {
                Toast.makeText(this, "No Entry exists", Toast.LENGTH_SHORT).show();
                return;
            }
            StringBuilder buffer = new StringBuilder();
            while (res.moveToNext()) {
                buffer.append("Name: ").append(res.getString(0)).append("\n");
                buffer.append("Contact: ").append(res.getString(1)).append("\n");
                buffer.append("Date Of Birth: ").append(res.getString(2)).append("\n\n");
            }
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setCancelable(true);
            builder.setTitle("Student Entries");
            builder.setMessage(buffer.toString());
            builder.show();
        });

        Button btnFees = new Button(this);
        btnFees.setText("Enter Fees Info");
        btnFees.setId(View.generateViewId());

        RelativeLayout layout = findViewById(R.id.main);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.BELOW, R.id.btnView);
        params.setMargins(0, 20, 0, 0);
        btnFees.setLayoutParams(params);
        layout.addView(btnFees);

        btnFees.setOnClickListener(v -> {
            Intent i = new Intent(MainActivity.this, FeesActivity.class);
            startActivity(i);
        });

        Button btnSummary = new Button(this);
        btnSummary.setText("View Summary");
        RelativeLayout.LayoutParams params2 = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        params2.addRule(RelativeLayout.BELOW, btnFees.getId());
        params2.setMargins(0, 20, 0, 0);
        btnSummary.setLayoutParams(params2);
        layout.addView(btnSummary);

        btnSummary.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, SummaryActivity.class);
            startActivity(intent);
        });
    }
}
