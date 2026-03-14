package com.example.learningapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CourseActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_IMPORT_PDF = 1;

    private ProgressBar progressBar;
    private TextView tvProgress;
    private ListView pdfListView;
    private Button importButton, deleteButton, backToHomeButton;

    private final List<String> pdfNames = new ArrayList<>();
    private final List<String> pdfUris = new ArrayList<>();
    private final Set<String> clickedPdfUris = new HashSet<>();

    private int selectedPosition = -1; // 記錄選取的檔案位置
    private CustomPdfAdapter adapter;

    private static final String PREFS_NAME = "CoursePrefs";
    private static final String KEY_PDF_NAMES = "pdfNames";
    private static final String KEY_PDF_URIS = "pdfUris";
    private static final String KEY_CLICKED_URIS = "clickedPdfUris";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course);

        // 綁定視圖
        progressBar = findViewById(R.id.progressBar);
        tvProgress = findViewById(R.id.tv_progress);
        pdfListView = findViewById(R.id.pdfListView);
        importButton = findViewById(R.id.btnAdd);
        deleteButton = findViewById(R.id.btnDelete);
        backToHomeButton = findViewById(R.id.btnBackToHome);

        // 初始化 ListView 的 Adapter
        adapter = new CustomPdfAdapter();
        pdfListView.setAdapter(adapter);
        pdfListView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

        loadSavedData();

        pdfListView.setOnItemClickListener((parent, view, position, id) -> {
            selectedPosition = position;
            openPdfFile(Uri.parse(pdfUris.get(position)));
        });

        importButton.setOnClickListener(v -> openFilePicker());
        deleteButton.setOnClickListener(v -> deleteSelectedFile());
        backToHomeButton.setOnClickListener(v -> finish());
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("application/pdf");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, REQUEST_CODE_IMPORT_PDF);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_IMPORT_PDF && resultCode == RESULT_OK && data != null) {
            Uri uri = data.getData();
            if (uri != null) {
                String fileName = getFileName(uri);
                if (!pdfUris.contains(uri.toString())) {
                    pdfNames.add(fileName);
                    pdfUris.add(uri.toString());
                    saveData();
                    updateProgressBar();
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(this, "檔案已存在", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private String getFileName(Uri uri) {
        String fileName = "未命名檔案";
        try (android.database.Cursor cursor = getContentResolver().query(uri, null, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                fileName = cursor.getString(cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileName;
    }

    private void openPdfFile(Uri pdfUri) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(pdfUri, "application/pdf");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        try {
            startActivity(intent);
            if (clickedPdfUris.add(pdfUri.toString())) {
                saveData();
                updateProgressBar();
                adapter.notifyDataSetChanged();
            }
        } catch (Exception e) {
            Toast.makeText(this, "無法找到 PDF 應用程式", Toast.LENGTH_SHORT).show();
        }
    }

    private void deleteSelectedFile() {
        if (selectedPosition >= 0 && selectedPosition < pdfUris.size()) {
            String removedUri = pdfUris.get(selectedPosition);
            pdfNames.remove(selectedPosition);
            pdfUris.remove(selectedPosition);
            clickedPdfUris.remove(removedUri);

            saveData();
            updateProgressBar();
            adapter.notifyDataSetChanged();
            selectedPosition = -1;

            Toast.makeText(this, "檔案已刪除", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "請選取要刪除的檔案", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateProgressBar() {
        int totalFiles = pdfUris.size();
        int clickedFiles = clickedPdfUris.size();
        int progress = totalFiles > 0 ? (clickedFiles * 100 / totalFiles) : 0;

        progressBar.setProgress(progress);
        tvProgress.setText(String.format("進度: %d%%", progress));
    }

    private void saveData() {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putStringSet(KEY_PDF_NAMES, new HashSet<>(pdfNames));
        editor.putStringSet(KEY_PDF_URIS, new HashSet<>(pdfUris));
        editor.putStringSet(KEY_CLICKED_URIS, clickedPdfUris);
        editor.apply();
    }

    private void loadSavedData() {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        Set<String> savedNames = preferences.getStringSet(KEY_PDF_NAMES, null);
        Set<String> savedUris = preferences.getStringSet(KEY_PDF_URIS, null);
        Set<String> savedClickedUris = preferences.getStringSet(KEY_CLICKED_URIS, null);

        if (savedNames != null) pdfNames.addAll(savedNames);
        if (savedUris != null) pdfUris.addAll(savedUris);
        if (savedClickedUris != null) clickedPdfUris.addAll(savedClickedUris);

        adapter.notifyDataSetChanged();
        updateProgressBar();
    }

    // 自定義 Adapter 顯示灰色標記
    private class CustomPdfAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return pdfNames.size();
        }

        @Override
        public Object getItem(int position) {
            return pdfNames.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            if (view == null) {
                LayoutInflater inflater = LayoutInflater.from(CourseActivity.this);
                view = inflater.inflate(android.R.layout.simple_list_item_single_choice, parent, false);
            }
            TextView textView = view.findViewById(android.R.id.text1);
            textView.setText(pdfNames.get(position));
            if (clickedPdfUris.contains(pdfUris.get(position))) {
                textView.setTextColor(Color.GRAY);
            } else {
                textView.setTextColor(Color.BLACK);
            }
            return view;
        }
    }
}
