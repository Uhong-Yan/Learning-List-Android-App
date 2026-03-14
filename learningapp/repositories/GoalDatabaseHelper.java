package com.example.learningapp.repositories;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.learningapp.models.Goal;

import java.util.ArrayList;

public class GoalDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "goals.db";
    private static final int DATABASE_VERSION = 2;

    // Table and Columns
    private static final String TABLE_GOALS = "goals";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_PROGRESS = "progress";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_STATUS = "status";
    private static final String COLUMN_CREATED_AT = "created_at";

    public GoalDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_GOALS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_NAME + " TEXT, " +
                COLUMN_PROGRESS + " INTEGER, " +
                COLUMN_DESCRIPTION + " TEXT, " +
                COLUMN_STATUS + " TEXT, " +
                COLUMN_CREATED_AT + " TEXT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + TABLE_GOALS + " ADD COLUMN " + COLUMN_DESCRIPTION + " TEXT");
            db.execSQL("ALTER TABLE " + TABLE_GOALS + " ADD COLUMN " + COLUMN_STATUS + " TEXT");
            db.execSQL("ALTER TABLE " + TABLE_GOALS + " ADD COLUMN " + COLUMN_CREATED_AT + " TEXT");
        }
    }

    public long addGoal(Goal goal) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, goal.getName());
        values.put(COLUMN_PROGRESS, goal.getProgress());
        values.put(COLUMN_DESCRIPTION, goal.getDescription());
        values.put(COLUMN_STATUS, goal.getStatus());
        values.put(COLUMN_CREATED_AT, goal.getCreatedAt());

        long id = db.insert(TABLE_GOALS, null, values); // 插入資料並獲取 ID
        db.close();

        if (id != -1) {
            goal.setId((int) id); // 將返回的 ID 設置回 Goal 物件
        }
        return id;
    }

    // 取得所有目標
    public ArrayList<Goal> getAllGoals() {
        ArrayList<Goal> goalList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_GOALS, null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME));
                int progress = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PROGRESS));
                String description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION));
                String status = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STATUS));
                String createdAt = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CREATED_AT));
                // 建構 Goal 物件，加入 isCompleted = false
                goalList.add(new Goal(id, name, progress, description, status, createdAt, false));

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return goalList;
    }

    // 更新目標
    public void updateGoal(Goal updatedGoal) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, updatedGoal.getName());
        values.put(COLUMN_PROGRESS, updatedGoal.getProgress());
        values.put(COLUMN_DESCRIPTION, updatedGoal.getDescription());
        values.put(COLUMN_STATUS, updatedGoal.getStatus());
        values.put(COLUMN_CREATED_AT, updatedGoal.getCreatedAt());
        db.update(TABLE_GOALS, values, COLUMN_ID + " = ?", new String[]{String.valueOf(updatedGoal.getId())});
        db.close();
    }

    // 刪除目標
    public void deleteGoal(int goalId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_GOALS, COLUMN_ID + " = ?", new String[]{String.valueOf(goalId)});
        db.close();
    }

    // 取得特定目標
    public Goal getGoalById(int goalId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_GOALS, null, COLUMN_ID + " = ?", new String[]{String.valueOf(goalId)}, null, null, null);
        Goal goal = null;

        if (cursor.moveToFirst()) {
            String name = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NAME));
            int progress = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_PROGRESS));
            String description = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRIPTION));
            String status = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_STATUS));
            String createdAt = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CREATED_AT));
            goal = new Goal(goalId, name, progress, description, status, createdAt, false); // 預設為 false

        }

        cursor.close();
        db.close();
        return goal;
    }

    // 清除所有目標
    public int deleteAllGoals() {
        SQLiteDatabase db = this.getWritableDatabase();
        int rowsDeleted = db.delete(TABLE_GOALS, null, null);
        db.close();
        return rowsDeleted;
    }
}
