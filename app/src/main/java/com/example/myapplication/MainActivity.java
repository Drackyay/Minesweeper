package com.example.myapplication;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import androidx.gridlayout.widget.GridLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.widget.Button;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setCustomView(R.layout.custom_bar);
        }

        GridLayout gridLayout = findViewById(R.id.gridLayout);
        gridLayout.setRowCount(12);
        gridLayout.setColumnCount(10);
        for (int i = 0; i < 120; i++) {
            View cell = LayoutInflater.from(this).inflate(R.layout.custom_cell, gridLayout, false);
            gridLayout.addView(cell);
            Button cellButton = cell.findViewById(R.id.cellButton);
            cellButton.setTag(i);
            cellButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int cellPosition = (int) v.getTag();
                    handleCellClick(cellPosition);
                }
            });
        }
    }
    private void handleCellClick(int position) {

    }
}