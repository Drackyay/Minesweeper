package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ResultPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_result_page);
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setCustomView(R.layout.custom_bar);
        }
        Intent intent = getIntent();
        String result = intent.getStringExtra("Result");
//        long timeTaken = intent.getLongExtra("TIME_TAKEN", 0);

        TextView resultTextView = findViewById(R.id.result);
        //TextView timeTextView = findViewById(R.id.timeTextView);
        resultTextView.setText(result);
        //timeTextView.setText("Time taken: " + timeTaken + " seconds");

        // Set up the button to restart the game
        Button restartButton = findViewById(R.id.restartButton);
        restartButton.setOnClickListener(v -> {
            Intent restartIntent = new Intent(ResultPage.this, MainActivity.class);
            startActivity(restartIntent);
            finish();
        });
    }
}