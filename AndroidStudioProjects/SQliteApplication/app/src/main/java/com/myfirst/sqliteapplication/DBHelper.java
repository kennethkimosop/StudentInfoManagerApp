package com.myfirst.sqliteapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "UserData.db";
    private static final int DATABASE_VERSION = 3;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create students table
        db.execSQL("CREATE TABLE Students(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "contact TEXT UNIQUE, " +
                "dateOfBirth TEXT)");

        // Create fees table
        db.execSQL("CREATE TABLE Fees(" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "student_id INTEGER, " +
                "totalFees TEXT, " +
                "feesPaid TEXT, " +
                "feesBalance TEXT, " +
                "completionDate TEXT, " +
                "FOREIGN KEY(student_id) REFERENCES Students(id))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Fees");
        db.execSQL("DROP TABLE IF EXISTS Students");
        onCreate(db);
    }

    // Insert student record
    public boolean insertStudent(String name, String contact, String dob) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("contact", contact);
        values.put("dateOfBirth", dob);
        long result = db.insert("Students", null, values);
        return result != -1;
    }

    // Fetch all students
    public Cursor getAllStudents() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM Students", null);
    }

    // Get student ID
    public int getStudentIdByName(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id FROM Students WHERE name = ?", new String[]{name});
        int id = -1;
        if (cursor.moveToFirst()) {
            id = cursor.getInt(0);
        }
        cursor.close();
        return id;
    }

    // Update student info
    public boolean updateStudent(String name, String contact, String dob) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("contact", contact);
        values.put("dateOfBirth", dob);

        Cursor cursor = db.rawQuery("SELECT * FROM Students WHERE name = ?", new String[]{name});
        boolean success = false;
        if (cursor.getCount() > 0) {
            long result = db.update("Students", values, "name = ?", new String[]{name});
            success = result != -1;
        }
        cursor.close();
        return success;
    }

    // Delete student and their fees
    public boolean deleteStudent(String name) {
        SQLiteDatabase db = this.getWritableDatabase();
        int studentId = getStudentIdByName(name);
        if (studentId != -1) {
            db.delete("Fees", "student_id = ?", new String[]{String.valueOf(studentId)});
            long result = db.delete("Students", "id = ?", new String[]{String.valueOf(studentId)});
            return result != -1;
        }
        return false;
    }

    // Insert or update fee info
    public boolean upsertFeeInfo(String name, String total, String paid, String balance, String date) {
        int studentId = getStudentIdByName(name);
        if (studentId == -1) return false;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Fees WHERE student_id = ?", new String[]{String.valueOf(studentId)});
        ContentValues values = new ContentValues();
        values.put("student_id", studentId);
        values.put("totalFees", total);
        values.put("feesPaid", paid);
        values.put("feesBalance", balance);
        values.put("completionDate", date);

        boolean result;
        if (cursor.moveToFirst()) {
            long update = db.update("Fees", values, "student_id = ?", new String[]{String.valueOf(studentId)});
            result = update != -1;
        } else {
            long insert = db.insert("Fees", null, values);
            result = insert != -1;
        }
        cursor.close();
        return result;
    }

    // Summary data join
    public Cursor getSummaryData() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT s.name, s.contact, s.dateOfBirth, f.totalFees, f.feesPaid, f.feesBalance, f.completionDate " +
                "FROM Students s LEFT JOIN Fees f ON s.id = f.student_id", null);
    }

    // Get a single student
    public Cursor getStudentByName(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM Students WHERE name = ?", new String[]{name});
    }

    // Get fee details for a given student_id
    public Cursor getFeesByStudentId(int studentId) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM Fees WHERE student_id = ?", new String[]{String.valueOf(studentId)});
    }

}
