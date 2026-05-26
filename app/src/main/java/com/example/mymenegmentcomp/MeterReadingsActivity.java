package com.example.mymenegmentcomp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MeterReadingsActivity extends AppCompatActivity {

    private EditText etElectricity;
    private EditText etColdWater;
    private EditText etHotWater;
    private Button btnSubmit;
    private Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meter_readings);

        initViews();
        setupClickListeners();
    }

    private void initViews() {
        etElectricity = findViewById(R.id.etElectricity);
        etColdWater = findViewById(R.id.etColdWater);
        etHotWater = findViewById(R.id.etHotWater);
        btnSubmit = findViewById(R.id.btnSubmit);
        btnBack = findViewById(R.id.btnBack);
    }

    private void setupClickListeners() {
        btnSubmit.setOnClickListener(v -> {
            String electricity = etElectricity.getText().toString().trim();
            String coldWater = etColdWater.getText().toString().trim();
            String hotWater = etHotWater.getText().toString().trim();

            if (electricity.isEmpty() || coldWater.isEmpty() || hotWater.isEmpty()) {
                Toast.makeText(this, "Заполните все поля", Toast.LENGTH_SHORT).show();
                return;
            }

            try {
                double eValue = Double.parseDouble(electricity);
                double cValue = Double.parseDouble(coldWater);
                double hValue = Double.parseDouble(hotWater);

                Toast.makeText(this,
                        "Показания успешно переданы!\n" +
                                "Электроэнергия: " + eValue + " кВт·ч\n" +
                                "Холодная вода: " + cValue + " м³\n" +
                                "Горячая вода: " + hValue + " м³",
                        Toast.LENGTH_LONG).show();

                finish();
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Введите корректные числа", Toast.LENGTH_SHORT).show();
            }
        });

        btnBack.setOnClickListener(v -> finish());
    }
}