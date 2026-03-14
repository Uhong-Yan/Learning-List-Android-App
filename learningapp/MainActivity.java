package com.example.learningapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.learningapp.models.Goal;
import com.example.learningapp.repositories.GoalDatabaseHelper;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private TextView tvPlaceholder, tvEncouragement;
    private LinearLayout llGoalsContainer;
    private ProgressBar progressBar;
    private Button btnEnterGoals, btnCoursePage;

    private ArrayList<Goal> goalList; // 存儲學習目標
    private GoalDatabaseHelper dbHelper; // 資料庫幫助類

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 綁定視圖
        tvPlaceholder = findViewById(R.id.tvPlaceholder);
        llGoalsContainer = findViewById(R.id.llGoalsContainer);
        progressBar = findViewById(R.id.progressBar);
        tvEncouragement = findViewById(R.id.tvEncouragement);
        btnEnterGoals = findViewById(R.id.btnEnterGoals);
        btnCoursePage = findViewById(R.id.btnCoursePage);

        // 初始化資料庫
        dbHelper = new GoalDatabaseHelper(this);

        // 從 SQLite 加載目標
        goalList = dbHelper.getAllGoals();

        // 加載目標
        loadGoals();

        // 進入學習頁面
        btnEnterGoals.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, GoalListActivity.class);
            intent.putParcelableArrayListExtra("goalList", new ArrayList<>(goalList));
            // 傳遞當前目標列表
            startActivityForResult(intent, 1); // 開啟頁面並等待返回結果
        });

        // 跳轉到課程頁面
        btnCoursePage.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CourseActivity.class);
            startActivity(intent);
        });
    }

    // 加載學習目標到便條紙
    private void loadGoals() {
        if (goalList.isEmpty()) {
            // 如果沒有目標，顯示提示
            tvPlaceholder.setVisibility(View.VISIBLE);
            llGoalsContainer.setVisibility(View.GONE);
            progressBar.setProgress(0); // 進度條清零
            tvEncouragement.setText("還沒有目標，快新增一個吧！");
        } else {
            // 如果有目標，顯示目標列表
            tvPlaceholder.setVisibility(View.GONE);
            llGoalsContainer.setVisibility(View.VISIBLE);

            llGoalsContainer.removeAllViews(); // 清空現有目標
            for (Goal goal : goalList) {
                TextView goalView = new TextView(this);
                goalView.setText(goal.getName()); // 顯示目標名稱
                goalView.setTextSize(16);

                // 自定義顏色邏輯
                if (goal.getProgress() >= 100) {
                    goalView.setTextColor(Color.parseColor("#D2B48C"));
                    // 已完成目標變成淡色
                } else {
                    goalView.setTextColor(Color.parseColor("#6B4F4F"));
                    // 未完成目標維持深色
                }

                llGoalsContainer.addView(goalView);
            }

            // 更新進度條
            updateProgressBar();
        }
    }

    // 計算進度條的平均進度並更新
    private void updateProgressBar() {
        if (goalList.isEmpty()) {
            progressBar.setProgress(0); // 如果列表為空，設置為 0
            return;
        }

        int totalProgress = 0;
        for (Goal goal : goalList) {
            totalProgress += goal.getProgress();
        }
        int averageProgress = totalProgress / goalList.size(); // 計算平均進度
        progressBar.setProgress(averageProgress); // 更新進度條
        updateEncouragement(averageProgress);
    }

    // 更新鼓勵文字
    private void updateEncouragement(int progress) {
        if (progress == 0) {
            tvEncouragement.setText("還沒開始，快來行動吧！");
        } else if (progress < 50) {
            tvEncouragement.setText("已經邁出第一步，加油！");
        } else if (progress < 100) {
            tvEncouragement.setText("快完成了，繼續努力！");
        } else {
            tvEncouragement.setText("太棒了！全部完成！");
        }
    }

    // 接收從 GoalListActivity 返回的結果
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            // 獲取更新後的目標列表
            ArrayList<Goal> updatedGoalList = data.getParcelableArrayListExtra("goalList");
            if (updatedGoalList != null) {
                goalList.clear();
                goalList.addAll(updatedGoalList);

                // 更新 SQLite
                dbHelper.deleteAllGoals();
                for (Goal goal : goalList) {
                    dbHelper.addGoal(goal);
                }

                loadGoals(); // 重新加載目標
            }
        }
    }
}

