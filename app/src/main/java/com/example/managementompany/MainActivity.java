package com.example.managementompany;

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

public class MainActivity extends AppCompatActivity {

    private TextView tvWelcome;
    private TextView tvPersonalAccount;
    private TextView tvBalance;
    private TextView tvLastMeterReadings;
    private TextView tvAddress;
    private Button btnSubmitReadings;
    private Button btnRequests;
    private Button btnLogout;
    private SharedPreferences sharedPrefs;
    private String personalAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();

        personalAccount = getIntent().getStringExtra("personal_account");
        if (personalAccount == null) {
            sharedPrefs = getSharedPreferences("uk_app_prefs", Context.MODE_PRIVATE);
            personalAccount = sharedPrefs.getString("personal_account", "Не указан");
        }

        loadUserData();
        setupClickListeners();
    }

    private void initViews() {
        tvWelcome = findViewById(R.id.tvWelcome);
        tvPersonalAccount = findViewById(R.id.tvPersonalAccount);
        tvBalance = findViewById(R.id.tvBalance);
        tvLastMeterReadings = findViewById(R.id.tvLastMeterReadings);
        tvAddress = findViewById(R.id.tvAddress);
        btnSubmitReadings = findViewById(R.id.btnSubmitReadings);
        btnRequests = findViewById(R.id.btnRequests);
        btnLogout = findViewById(R.id.btnLogout);
    }

    private void loadUserData() {
        tvWelcome.setText("Здравствуйте!");
        tvPersonalAccount.setText("Лицевой счет: " + personalAccount);
        tvAddress.setText("ул. Центральная, д. 15, кв. 42");

        double balance = 1250.75;
        if (balance < 0) {
            tvBalance.setText(String.format("%.2f ₽ (долг)", Math.abs(balance)));
            tvBalance.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
        } else {
            tvBalance.setText(String.format("%.2f ₽", balance));
            tvBalance.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
        }

        tvLastMeterReadings.setText(
                "Электроэнергия: 1 245 кВт·ч\n" +
                        "Холодная вода: 45 м³\n" +
                        "Горячая вода: 32 м³"
        );
    }

    private void setupClickListeners() {
        btnSubmitReadings.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MeterReadingsActivity.class);
            startActivity(intent);
        });

        btnRequests.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RequestsActivity.class);
            startActivity(intent);
        });

        btnLogout.setOnClickListener(v -> {
            SharedPreferences.Editor editor = sharedPrefs.edit();
            editor.clear();
            editor.apply();

            Toast.makeText(MainActivity.this, "Вы вышли из системы", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}