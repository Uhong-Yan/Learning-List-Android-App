package com.example.learningapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.animation.Animator;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.learningapp.models.Goal;
import com.example.learningapp.repositories.GoalDatabaseHelper;

import java.util.ArrayList;

public class GoalListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private GoalAdapter adapter;
    private ArrayList<Goal> goalList;
    private View darkOverlay;
    private LottieAnimationView starAnimationView;
    private GoalDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_goal_list);

        recyclerView = findViewById(R.id.recyclerView);
        darkOverlay = findViewById(R.id.darkOverlay);
        starAnimationView = findViewById(R.id.starAnimationView);

        dbHelper = new GoalDatabaseHelper(this);

        // 接收從 MainActivity 傳遞的目標列表
        goalList = getIntent().getParcelableArrayListExtra("goalList");
        if (goalList == null) {
            goalList = new ArrayList<>();
        }

        adapter = new GoalAdapter(goalList, this::checkGoalCompletion, dbHelper, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        findViewById(R.id.btnAddGoal).setOnClickListener(v -> {
            Intent intent = new Intent(GoalListActivity.this, AddGoalActivity.class);
            startActivityForResult(intent, 1);
        });

        findViewById(R.id.btnBackToMain).setOnClickListener(v -> {
            Intent resultIntent = new Intent();
            resultIntent.putParcelableArrayListExtra("goalList", goalList); // 傳回更新的目標列表
            setResult(RESULT_OK, resultIntent);
            finish(); // 返回主頁
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == 1) {
                // 新增目標
                String newGoalName = data.getStringExtra("newGoal");
                if (newGoalName != null) {
                    Goal newGoal = new Goal(newGoalName, 0);
                    goalList.add(newGoal);
                    dbHelper.addGoal(newGoal);
                    adapter.notifyDataSetChanged();
                }
            } else if (requestCode == 2) {
                // 更新目標
                if (data.hasExtra("updatedGoalName")) {
                    int goalId = data.getIntExtra("goalId", -1);
                    String updatedGoalName = data.getStringExtra("updatedGoalName");
                    int updatedProgress = data.getIntExtra("updatedProgress", 0);

                    for (Goal goal : goalList) {
                        if (goal.getId() == goalId) {
                            goal.setName(updatedGoalName);
                            goal.setProgress(updatedProgress);
                            dbHelper.updateGoal(goal); // 更新資料庫
                            break;
                        }
                    }
                    adapter.notifyDataSetChanged();
                }

                // 刪除目標
                if (data.hasExtra("deletedGoalId")) {
                    int deletedGoalId = data.getIntExtra("deletedGoalId", -1);
                    if (deletedGoalId != -1) {
                        dbHelper.deleteGoal(deletedGoalId); // 從資料庫刪除
                        for (int i = 0; i < goalList.size(); i++) {
                            if (goalList.get(i).getId() == deletedGoalId) {
                                goalList.remove(i); // 從列表中刪除
                                break;
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        }
    }

    public void checkGoalCompletion() {
        for (Goal goal : goalList) {
            // 每個目標單獨檢查，如果達成 100% 並且尚未觸發過動畫，則顯示動畫
            if (goal.getProgress() == 100 && !goal.isCompleted()) {
                goal.setCompleted(true); // 標記該目標已完成，避免重複觸發動畫
                showStarAnimation();     // 播放星星特效
            }
        }
    }

    // showStarAnimation 方法放在這裡，並確保不在其他方法內
    private void showStarAnimation() {
        darkOverlay.setVisibility(View.VISIBLE);
        starAnimationView.setVisibility(View.VISIBLE);
        starAnimationView.playAnimation();

        starAnimationView.addAnimatorListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                darkOverlay.setVisibility(View.GONE);
                starAnimationView.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
    }
}
