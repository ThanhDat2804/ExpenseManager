package com.example.expense.Tools;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.expense.Model.UserModel;

public class DBHelper extends SQLiteOpenHelper {
    public DBHelper(Context context) {
        super(context, "daytodayexpenses.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE Users(" +
                "user_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT, " +
                "password TEXT, " +
                "email TEXT, " +
                "phone TEXT, " +
                "address TEXT, " +
                "created_at TEXT, " +
                "updated_at TEXT)");
        db.execSQL("CREATE TABLE Notes(note_ID TEXT, user_ID TEXT, note_date TEXT, note_Description TEXT, note_DateMonthYear TEXT, note_WeekDay TEXT)");
        db.execSQL("CREATE TABLE Transactions(UID TEXT , transID TEXT, date TEXT, month TEXT, year TEXT, transammount TEXT, title TEXT, description TEXT, category TEXT, type TEXT)");
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop Table if exists Users");
        db.execSQL("drop Table if exists Transactions");
        db.execSQL("drop Table if exists Notes");

    }
    public boolean registerUser(UserModel user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("username", user.getUsername());
        contentValues.put("password", user.getPassword());
        contentValues.put("email", user.getEmail());
        contentValues.put("phone", user.getPhone());
        contentValues.put("address", user.getAddress());
        contentValues.put("created_at", user.getCreated_at());
        contentValues.put("updated_at", user.getUpdated_at());

        long result = db.insert("Users", null, contentValues);
        return result != -1;
    }
    public boolean validateLoginWithUsernameOrEmail(String usernameOrEmail, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM Users WHERE (username = ? OR email = ?) AND password = ?";
        Cursor cursor = db.rawQuery(query, new String[]{usernameOrEmail, usernameOrEmail, password});
        boolean result = cursor.getCount() > 0;
        cursor.close();
        return result;
    }

    public boolean isUsernameExists(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Users WHERE username = ?", new String[]{username});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exists;
    }
    public boolean isEmailExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Users WHERE email = ?", new String[]{email});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exists;
    }
    public boolean isUsernameOrEmailExists(String username, String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Users WHERE username = ? OR email = ?", new String[]{username, email});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exists;
    }
    public boolean updatePassword(String username, String newPassword) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("password", newPassword);

        int result = db.update("Users", contentValues, "username = ?", new String[]{username});
        return result > 0;
    }

    // Method to validate login credentials
    public boolean validateLogin(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Users WHERE username = ? AND password = ?", new String[]{username, password});
        boolean result = cursor.getCount() > 0;
        cursor.close();
        return result;
    }
    public Boolean insertNote(String noteID, String userID, String date, String notedescription, String MonthYear, String weekday) {
        SQLiteDatabase DB = this.getWritableDatabase();

        // Kiểm tra trùng lặp
        Cursor cursor = DB.rawQuery("SELECT * FROM Notes WHERE user_ID = ? AND note_date = ? AND note_Description = ?",
                new String[]{userID, date, notedescription});
        if (cursor.getCount() > 0) {
            cursor.close();
            return false; // Dữ liệu đã tồn tại
        }
        cursor.close();

        // Thêm dữ liệu mới
        ContentValues contentValues = new ContentValues();
        contentValues.put("note_ID", noteID);
        contentValues.put("user_ID", userID);
        contentValues.put("note_date", date);
        contentValues.put("note_Description", notedescription);
        contentValues.put("note_DateMonthYear", MonthYear);
        contentValues.put("note_WeekDay", weekday);

        long result = DB.insert("Notes", null, contentValues);
        return result != -1;
    }

    public Cursor getNotesfrom(String monthYear) {
        String query = "SELECT* FROM Notes WHERE note_DateMonthYear"+"='" + monthYear + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query, null);
        }
        return cursor;

    }
    public Boolean insertTransaction(String UID, String transID, String date, String month, String year, String transammount, String title, String description, String category, String type) {
        SQLiteDatabase DB = this.getWritableDatabase();

        // Kiểm tra trùng lặp
        Cursor cursor = DB.rawQuery("SELECT * FROM Transactions WHERE UID = ? AND date = ? AND title = ? AND description = ?",
                new String[]{UID, date, title, description});
        if (cursor.getCount() > 0) {
            cursor.close();
            return false; // Dữ liệu đã tồn tại
        }
        cursor.close();

        // Thêm dữ liệu mới
        ContentValues contentValues = new ContentValues();
        contentValues.put("UID", UID);
        contentValues.put("transID", transID);
        contentValues.put("date", date);
        contentValues.put("month", month);
        contentValues.put("year", year);
        contentValues.put("transammount", transammount);
        contentValues.put("title", title);
        contentValues.put("description", description);
        contentValues.put("category", category);
        contentValues.put("type", type);

        long result = DB.insert("Transactions", null, contentValues);
        return result != -1;
    }


    public Cursor getAllTransactions() {
        String query = "SELECT* FROM Transactions";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query, null);
        }
        return cursor;

    }
    public Cursor countTransactions(String month,String year) {
        String query = "SELECT DISTINCT date FROM Transactions WHERE month"+"='" + month+ "'"+ " AND  year"+"='" + year + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query, null);
        }
        return cursor;

    }
    public Cursor countTransactions(String year) {
        String query = "SELECT DISTINCT month FROM Transactions WHERE year"+"='" + year + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query, null);
        }
        return cursor;

    }

    public Cursor getIncomeTransaction(String date,String type) {
        String query = "SELECT* FROM Transactions WHERE date"+"='" + date + "'" + " AND  type"+"='" + type + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query, null);
        }
        return cursor;

    }
    public Cursor getInTransaction(String type) {
        String query = "SELECT* FROM Transactions WHERE type"+"='" + type + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query, null);
        }
        return cursor;

    }
    public Cursor getTransaction(String month,String type) {
        String query = "SELECT* FROM Transactions WHERE month"+"='" + month + "'" + " AND  type"+"='" + type + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query, null);
        }
        return cursor;

    }
    public Cursor getIncomeMonthly(String numberMonth,String numberYear,String type) {
        String query = "SELECT * FROM Transactions WHERE month"+"='" + numberMonth + "'" + " AND  type"+"='" + type + "'"+ " AND  year"+"='" + numberYear + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        if (db != null) {
            cursor = db.rawQuery(query, null);
        }
        return cursor;

    }
    public void deletefromNotes(String value) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + "Notes" + " WHERE " + "note_ID" + "='" + value + "'");
        db.close();
    }


    public void deletefromTransaction(String ID) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + "Transactions" + " WHERE " + "transID" + "='" + ID + "'");
        db.close();
    }

    public void editTransaction(String UID, String transID, String date, String month, String year, String transammount, String title, String description, String category, String type) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "UPDATE Transactions SET date = ?, month = ?, year = ?, transammount = ?, title = ?, description = ?, category = ?, type = ? WHERE transID = ?";
        db.execSQL(query, new String[]{date, month, year, transammount, title, description, category, type, transID});
    }

    public void editNote(String noteID ,String date , String notedescription ,String MonthYear , String weekday) {
        SQLiteDatabase db = this.getWritableDatabase();
        String s1 = null;

        String Query = "UPDATE Notes SET "+ "note_date" + "='" + date + "',note_Description"+"='"+notedescription  + "',note_DateMonthYear"+"='"+MonthYear  + "',note_WeekDay"+"='"+weekday+"'" + " WHERE " + "note_ID" +"='" + noteID + "'" ;
        db.execSQL(Query);

    }
}