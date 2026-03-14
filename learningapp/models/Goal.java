package com.example.learningapp.models;
import android.os.Parcel;
import android.os.Parcelable;

public class Goal implements Parcelable {
    private int id; // 目標的唯一標識
    private String name; // 目標名稱
    private int progress; // 目標進度
    private String description; // 目標描述
    private String status; // 目標狀態
    private String createdAt; // 創建時間
    private boolean isCompleted; // 標記是否已完成動畫播放

    // 帶所有參數的構造函數
    public Goal(int id, String name, int progress, String description, String status, String createdAt, boolean isCompleted) {
        this.id = id;
        this.name = name;
        this.progress = progress;
        this.description = description;
        this.status = status;
        this.createdAt = createdAt;
        this.isCompleted = isCompleted;
    }

    // 簡化構造函數（只帶 name 和 progress）
    public Goal(String name, int progress) {
        this.name = name;
        this.progress = progress;
        this.description = ""; // 默認描述為空
        this.status = "未開始"; // 默認狀態
        this.createdAt = String.valueOf(System.currentTimeMillis()); // 當前時間戳
        this.isCompleted = false; // 默認未播放動畫
    }

    // 增加進度的方法
    public void increaseProgress() {
        if (progress < 100) {
            progress = Math.min(100, progress + 10); // 每次增加 10%，最大值為 100%
            if (progress == 100) {
                this.status = "已完成"; // 進度達到 100 時設置狀態為 "已完成"
            }
        }
    }

    // Getter 和 Setter 方法
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = Math.min(100, progress); // 確保進度不超過 100
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void setCompleted(boolean completed) {
        isCompleted = completed;
    }
    private boolean isAnimationShown = false;

    public boolean isAnimationShown() {
        return isAnimationShown;
    }

    public void setAnimationShown(boolean animationShown) {
        isAnimationShown = animationShown;
    }


    // Parcelable 實現
    protected Goal(Parcel in) {
        id = in.readInt();
        name = in.readString();
        progress = in.readInt();
        description = in.readString();
        status = in.readString();
        createdAt = in.readString();
        isCompleted = in.readByte() != 0; // 讀取布林值
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
        dest.writeInt(progress);
        dest.writeString(description);
        dest.writeString(status);
        dest.writeString(createdAt);
        dest.writeByte((byte) (isCompleted ? 1 : 0)); // 保存布林值
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Goal> CREATOR = new Creator<Goal>() {
        @Override
        public Goal createFromParcel(Parcel in) {
            return new Goal(in);
        }

        @Override
        public Goal[] newArray(int size) {
            return new Goal[size];
        }
    };
}
