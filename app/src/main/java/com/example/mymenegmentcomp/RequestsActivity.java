package com.example.mymenegmentcomp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class RequestsActivity extends AppCompatActivity {

    private LinearLayout llRequestsList;
    private Button btnAddRequest;
    private Button btnBack;
    private TextView tvNoRequests;

    private ArrayList<String> requestsList = new ArrayList<>();
    private String currentUserAccount;
    private SharedPreferences sharedPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requests);

        initViews();
        setupClickListeners();

        // Получаем текущего пользователя
        sharedPrefs = getSharedPreferences("uk_app_prefs", Context.MODE_PRIVATE);
        currentUserAccount = sharedPrefs.getString("personal_account", "default_user");

        // Загружаем заявки для этого пользователя
        loadRequestsFromStorage();

        // Отображаем заявки
        displayRequests();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // При возвращении на экран перезагружаем заявки
        loadRequestsFromStorage();
        displayRequests();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            boolean requestCreated = data.getBooleanExtra("request_created", false);
            if (requestCreated) {
                // Заявка создана, перезагружаем список
                loadRequestsFromStorage();
                displayRequests();
                Toast.makeText(this, "Список заявок обновлен", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void initViews() {
        llRequestsList = findViewById(R.id.llRequestsList);
        btnAddRequest = findViewById(R.id.btnAddRequest);
        btnBack = findViewById(R.id.btnBack);
        tvNoRequests = findViewById(R.id.tvNoRequests);
    }

    private void setupClickListeners() {
        btnAddRequest.setOnClickListener(v -> {
            Intent intent = new Intent(RequestsActivity.this, CreateRequestActivity.class);
            startActivityForResult(intent, 1);
        });

        btnBack.setOnClickListener(v -> finish());
    }

    // Загрузка заявок из SharedPreferences ТОЛЬКО ТЕКУЩЕГО пользователя
    private void loadRequestsFromStorage() {
        String prefsName = "requests_" + currentUserAccount;
        SharedPreferences requestsPrefs = getSharedPreferences(prefsName, Context.MODE_PRIVATE);

        int count = requestsPrefs.getInt("count", 0);
        requestsList.clear();

        for (int i = 0; i < count; i++) {
            String request = requestsPrefs.getString("request_" + i, null);
            if (request != null) {
                requestsList.add(request);
            }
        }

        // Если заявок нет, добавляем демо-заявки только для первого входа
        if (requestsList.isEmpty()) {
            boolean hasDemo = requestsPrefs.getBoolean("has_demo", false);
            if (!hasDemo) {
                // Проверяем, не загружали ли уже демо-заявки для этого пользователя
                requestsList.add("❌ Не работает лифт в подъезде");
                requestsList.add("💧 Течет кран на кухне");
                saveRequestsToStorage();

                SharedPreferences.Editor editor = requestsPrefs.edit();
                editor.putBoolean("has_demo", true);
                editor.apply();
            }
        }
    }

    // Сохранение заявок
    private void saveRequestsToStorage() {
        String prefsName = "requests_" + currentUserAccount;
        SharedPreferences requestsPrefs = getSharedPreferences(prefsName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = requestsPrefs.edit();

        editor.putInt("count", requestsList.size());
        for (int i = 0; i < requestsList.size(); i++) {
            editor.putString("request_" + i, requestsList.get(i));
        }
        editor.apply();
    }

    // Удаление заявки
    private void deleteRequest(int position) {
        if (position >= 0 && position < requestsList.size()) {
            requestsList.remove(position);
            saveRequestsToStorage();
            displayRequests();
            Toast.makeText(this, "Заявка удалена", Toast.LENGTH_SHORT).show();
        }
    }

    // Отображение заявок на экране
    private void displayRequests() {
        // Очищаем список перед загрузкой
        llRequestsList.removeAllViews();

        if (requestsList.isEmpty()) {
            tvNoRequests.setVisibility(View.VISIBLE);
        } else {
            tvNoRequests.setVisibility(View.GONE);
            for (int i = 0; i < requestsList.size(); i++) {
                addRequestItem(requestsList.get(i), i);
            }
        }
    }

    private void addRequestItem(String requestText, int position) {
        View itemView = getLayoutInflater().inflate(R.layout.item_request, null);
        TextView tvRequestText = itemView.findViewById(R.id.tvRequestText);
        Button btnDelete = itemView.findViewById(R.id.btnDelete);

        tvRequestText.setText(requestText);

        // Кнопка удаления
        btnDelete.setOnClickListener(v -> {
            deleteRequest(position);
        });

        llRequestsList.addView(itemView);
    }
}