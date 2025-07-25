package com.myfirst.sqliteapplication;

import android.app.DatePickerDialog;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Calendar;

public class FeesActivity extends AppCompatActivity {

    EditText etName, etTotalFees, etFeesPaid;
    TextView tvBalance, tvCompletionDate;
    Button btnPickDate, btnSave;
    DBHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fees);

        etName = findViewById(R.id.etName);
        etTotalFees = findViewById(R.id.etTotalFees);
        etFeesPaid = findViewById(R.id.etFeesPaid);
        tvBalance = findViewById(R.id.tvBalance);
        tvCompletionDate = findViewById(R.id.tvCompletionDate);
        btnPickDate = findViewById(R.id.btnPickDate);
        btnSave = findViewById(R.id.btnSaveFees);
        db = new DBHelper(this);

        TextWatcher balanceWatcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                calculateBalance();
            }
            @Override public void afterTextChanged(Editable s) {}
        };

        etTotalFees.addTextChangedListener(balanceWatcher);
        etFeesPaid.addTextChangedListener(balanceWatcher);

        btnPickDate.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dpd = new DatePickerDialog(FeesActivity.this,
                    (view, year1, month1, dayOfMonth) -> {
                        String date = String.format("%02d/%02d/%d", dayOfMonth, month1 + 1, year1);
                        tvCompletionDate.setText(date);
                    }, year, month, day);

            dpd.getDatePicker().setMinDate(System.currentTimeMillis());
            dpd.show();
        });

        btnSave.setOnClickListener(v -> {
            String name = etName.getText().toString().trim();
            String total = etTotalFees.getText().toString().trim();
            String paid = etFeesPaid.getText().toString().trim();
            String balance = tvBalance.getText().toString().replace("Balance: ", "").trim();
            String date = tvCompletionDate.getText().toString().trim();

            if (name.isEmpty() || total.isEmpty() || paid.isEmpty() || balance.isEmpty() || date.isEmpty()) {
                Toast.makeText(this, "Fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            Cursor cursor = db.getStudentByName(name);
            if (cursor.getCount() == 0) {
                Toast.makeText(this, "Student not found. Please add them first.", Toast.LENGTH_LONG).show();
                return;
            }

            cursor.moveToFirst();
            int studentId = cursor.getInt(0);

            boolean inserted = db.upsertFeeInfo(name, total, paid, balance, date);

            Toast.makeText(this, inserted ? "Fees info saved" : "Failed to save fees info", Toast.LENGTH_SHORT).show();
        });
    }

    private void calculateBalance() {
        String totalStr = etTotalFees.getText().toString().trim();
        String paidStr = etFeesPaid.getText().toString().trim();
        try {
            int total = Integer.parseInt(totalStr.isEmpty() ? "0" : totalStr);
            int paid = Integer.parseInt(paidStr.isEmpty() ? "0" : paidStr);
            int balance = Math.max(total - paid, 0);
            tvBalance.setText("Balance: " + balance);
        } catch (NumberFormatException e) {
            tvBalance.setText("Balance: -");
        }
    }
}
