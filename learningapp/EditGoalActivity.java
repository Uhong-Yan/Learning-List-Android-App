package com.example.learningapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.learningapp.models.Goal;
import com.example.learningapp.repositories.GoalDatabaseHelper;

public class EditGoalActivity extends AppCompatActivity {

    private EditText editGoalName;
    private ProgressBar progressBar;
    private Button buttonIncrease, buttonDecrease, buttonSave, buttonDelete, buttonCancel;
    private GoalDatabaseHelper dbHelper;
    private int goalId;
    private int progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_goal);

        // 初始化資料庫
        dbHelper = new GoalDatabaseHelper(this);

        // 綁定元件
        editGoalName = findViewById(R.id.edit_goal_name);
        progressBar = findViewById(R.id.progress_bar);
        buttonIncrease = findViewById(R.id.button_increase);
        buttonDecrease = findViewById(R.id.button_decrease);
        buttonSave = findViewById(R.id.button_save);
        buttonDelete = findViewById(R.id.button_delete);
        buttonCancel = findViewById(R.id.button_cancel);

        // 接收目標資訊
        goalId = getIntent().getIntExtra("goalId", -1);
        String goalName = getIntent().getStringExtra("goalName");
        progress = getIntent().getIntExtra("goalProgress", 0);

        // 初始化頁面
        editGoalName.setText(goalName);
        progressBar.setProgress(progress);

        // 增加進度
        buttonIncrease.setOnClickListener(v -> {
            if (progress + 10 <= 100) {
                progress += 10;
                progressBar.setProgress(progress);
            } else {
                Toast.makeText(this, "進度已達到上限", Toast.LENGTH_SHORT).show();
            }
        });

        // 減少進度
        buttonDecrease.setOnClickListener(v -> {
            if (progress - 10 >= 0) {
                progress -= 10;
                progressBar.setProgress(progress);
            } else {
                Toast.makeText(this, "進度已達到下限", Toast.LENGTH_SHORT).show();
            }
        });

        // 儲存目標
        buttonSave.setOnClickListener(v -> {
            String updatedGoalName = editGoalName.getText().toString().trim();
            if (updatedGoalName.isEmpty()) {
                Toast.makeText(this, "請輸入學習目標名稱", Toast.LENGTH_SHORT).show();
                return;
            }

            // 更新目標資料
            Intent resultIntent = new Intent();
            resultIntent.putExtra("goalId", goalId);
            resultIntent.putExtra("updatedGoalName", updatedGoalName);
            resultIntent.putExtra("updatedProgress", progress);
            setResult(RESULT_OK, resultIntent);
            finish(); // 返回到上一頁
        });

        // 刪除目標
        buttonDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("刪除確認")
                    .setMessage("確定要刪除此學習目標嗎？")
                    .setPositiveButton("是", (dialog, which) -> {
                        Intent resultIntent = new Intent();
                        resultIntent.putExtra("deletedGoalId", goalId);
                        setResult(RESULT_OK, resultIntent);
                        finish();
                    })
                    .setNegativeButton("否", null)
                    .show();
        });

        // 取消變更
        buttonCancel.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("取消確認")
                    .setMessage("確定要取消變更嗎？")
                    .setPositiveButton("是", (dialog, which) -> finish())
                    .setNegativeButton("否", null)
                    .show();
        });
    }
}
