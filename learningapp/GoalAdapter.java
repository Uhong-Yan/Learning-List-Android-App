package com.example.learningapp;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.learningapp.models.Goal;
import com.example.learningapp.repositories.GoalDatabaseHelper;

import java.util.ArrayList;

public class GoalAdapter extends RecyclerView.Adapter<GoalAdapter.GoalViewHolder> {

    private ArrayList<Goal> goalList;
    private Runnable onAllGoalsCompleted;
    private GoalDatabaseHelper dbHelper;
    private Context context;

    public GoalAdapter(ArrayList<Goal> goalList, Runnable onAllGoalsCompleted, GoalDatabaseHelper dbHelper, Context context) {
        this.goalList = goalList;
        this.onAllGoalsCompleted = onAllGoalsCompleted;
        this.dbHelper = dbHelper;
        this.context = context;
    }

    @NonNull
    @Override
    public GoalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_goal, parent, false);
        return new GoalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GoalViewHolder holder, int position) {
        Goal goal = goalList.get(position);

        // 更新目標名稱與進度條
        holder.tvGoalName.setText(goal.getName());
        holder.progressBar.setProgress(goal.getProgress());

        // 檢查是否完成目標
        if (goal.getProgress() >= 100) {
            holder.btnIncreaseProgress.setVisibility(View.GONE);
            holder.animationView.setVisibility(View.VISIBLE);
            holder.animationView.playAnimation();
        } else {
            holder.btnIncreaseProgress.setVisibility(View.VISIBLE);
            holder.animationView.setVisibility(View.GONE);
        }

        // 增加進度
        holder.btnIncreaseProgress.setOnClickListener(v -> {
            goal.increaseProgress();
            holder.progressBar.setProgress(goal.getProgress());
            dbHelper.updateGoal(goal); // 更新資料庫中的進度

            if (goal.getProgress() >= 100) {
                holder.btnIncreaseProgress.setVisibility(View.GONE);
                holder.animationView.setVisibility(View.VISIBLE);
                holder.animationView.playAnimation();
            }

            // 新增檢查，觸發星星動畫
            ((GoalListActivity) context).checkGoalCompletion();

            onAllGoalsCompleted.run();
            notifyItemChanged(position);
        });

        // 編輯目標
        holder.ivEditGoal.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditGoalActivity.class);
            intent.putExtra("goalId", goal.getId()); // 傳遞目標 ID
            intent.putExtra("goalName", goal.getName()); // 傳遞目標名稱
            intent.putExtra("goalProgress", goal.getProgress()); // 傳遞目標進度
            ((GoalListActivity) context).startActivityForResult(intent, 2);
        });
    }

    @Override
    public int getItemCount() {
        return goalList.size();
    }

    /**
     * 刪除目標
     * @param position 目標位置
     */
    public void deleteGoal(int position) {
        Goal goal = goalList.get(position);
        dbHelper.deleteGoal(goal.getId()); // 從資料庫中刪除目標
        goalList.remove(position); // 從清單中刪除目標
        notifyItemRemoved(position); // 通知 RecyclerView 更新
    }

    static class GoalViewHolder extends RecyclerView.ViewHolder {
        TextView tvGoalName;
        ProgressBar progressBar;
        Button btnIncreaseProgress;
        LottieAnimationView animationView;
        ImageView ivEditGoal;

        public GoalViewHolder(@NonNull View itemView) {
            super(itemView);
            tvGoalName = itemView.findViewById(R.id.tvGoalName);
            progressBar = itemView.findViewById(R.id.progressBar);
            btnIncreaseProgress = itemView.findViewById(R.id.btnIncreaseProgress);
            animationView = itemView.findViewById(R.id.animationView);
            ivEditGoal = itemView.findViewById(R.id.ivEditGoal);
        }
    }
}