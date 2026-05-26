package com.example.mymenegmentcomp;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class CreateRequestActivity extends AppCompatActivity {

    private Spinner spinnerCategory;
    private EditText etDescription;
    private Button btnSubmit;
    private Button btnBack;

    private String currentUserAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_request);

        initViews();
        setupSpinner();
        setupClickListeners();

        // Получаем текущего пользователя
        SharedPreferences prefs = getSharedPreferences("uk_app_prefs", Context.MODE_PRIVATE);
        currentUserAccount = prefs.getString("personal_account", "default_user");
    }

    private void initViews() {
        spinnerCategory = findViewById(R.id.spinnerCategory);
        etDescription = findViewById(R.id.etDescription);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnBack = findViewById(R.id.btnBack);
    }

    private void setupSpinner() {
        String[] categories = {
                "📌 Сантехника",
                "⚡ Электрика",
                "🛗 Лифт",
                "🧹 Уборка",
                "🔨 Ремонт подъезда",
                "📝 Другое"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);
    }

    private void setupClickListeners() {
        btnSubmit.setOnClickListener(v -> {
            String category = spinnerCategory.getSelectedItem().toString();
            String description = etDescription.getText().toString().trim();

            if (description.isEmpty()) {
                etDescription.setError("Опишите проблему");
                etDescription.requestFocus();
                return;
            }

            // Получаем текущую дату и время
            String date = new java.text.SimpleDateFormat("dd.MM.yyyy HH:mm")
                    .format(new java.util.Date());

            // Формируем текст заявки
            String requestText = "🆕 " + category + "\n   " + description + "\n   📅 " + date;

            // Сохраняем заявку в файл пользователя
            saveRequestToFile(requestText);

            Toast.makeText(CreateRequestActivity.this,
                    "✅ Заявка создана!",
                    Toast.LENGTH_LONG).show();

            // Возвращаем результат в RequestsActivity
            Intent resultIntent = new Intent();
            resultIntent.putExtra("request_created", true);
            setResult(RESULT_OK, resultIntent);

            finish();
        });

        btnBack.setOnClickListener(v -> finish());
    }

    private void saveRequestToFile(String requestText) {
        // Используем уникальное имя файла для каждого пользователя
        String prefsName = "requests_" + currentUserAccount;
        SharedPreferences requestsPrefs = getSharedPreferences(prefsName, Context.MODE_PRIVATE);

        // Получаем существующие заявки
        int count = requestsPrefs.getInt("count", 0);

        // Сохраняем новую заявку
        SharedPreferences.Editor editor = requestsPrefs.edit();
        editor.putString("request_" + count, requestText);
        editor.putInt("count", count + 1);
        editor.apply();
    }
}