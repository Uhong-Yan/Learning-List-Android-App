package com.example.learningapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AddGoalActivity extends AppCompatActivity {

    private EditText edtGoalName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_goal);
        edtGoalName = findViewById(R.id.edtGoalName); // 目標名稱輸入框
        Button btnSave = findViewById(R.id.btnSaveGoal); // 儲存按鈕
        Button btnCancel = findViewById(R.id.btnCancelGoal); // 取消按鈕
        // 儲存目標按鈕
        btnSave.setOnClickListener(v -> {
            String goalName = edtGoalName.getText().toString().trim();
            if (goalName.isEmpty()) {
                Toast.makeText(AddGoalActivity.this, "目標名稱不能為空！",
                        Toast.LENGTH_SHORT).show();
                return;
            }
            Intent resultIntent = new Intent();
            resultIntent.putExtra("newGoal", goalName); // 將目標名稱回傳
            setResult(RESULT_OK, resultIntent);
            finish(); // 結束頁面
        });
        // 取消按鈕
        btnCancel.setOnClickListener(v -> {
            setResult(RESULT_CANCELED);
            finish();
        });
    }
}