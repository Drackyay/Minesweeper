package com.example.myapplication;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import androidx.gridlayout.widget.GridLayout;

import android.view.ViewGroup;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.widget.Button;
import android.widget.ToggleButton;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;


public class MainActivity extends AppCompatActivity {

    private HashMap<Cell, String> cellStates = new HashMap<>();
    private ToggleButton toggleButton;
    private Set<Cell> mines = new HashSet<>();
    private int flagCount = 4;
    private TextView flagCountView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setCustomView(R.layout.custom_bar);
        }

        GridLayout gridLayout = findViewById(R.id.gridLayout);
        gridLayout.setRowCount(12);
        gridLayout.setColumnCount(10);
        toggleButton = findViewById(R.id.toggleButton);
        for (int i = 0; i < 12; i++) {
            for (int j = 0; j < 10; j++) {
                Button cellButton = new Button(this);
                cellButton.setBackground(getResources().getDrawable(R.drawable.button_background, getTheme()));
                cellButton.setPadding(0, 0, 0, 0);
                GridLayout.LayoutParams cellScale = new GridLayout.LayoutParams();
                cellScale.rowSpec = GridLayout.spec(i);
                cellScale.columnSpec = GridLayout.spec(j);
                cellScale.width = dpToPixel(30);
                cellScale.height = dpToPixel(30);
                cellScale.setMargins(1, 1, 1, 1);
                cellButton.setLayoutParams(cellScale);
                gridLayout.addView(cellButton);
                Cell newCell = new Cell(i, j);
                cellStates.put(newCell, "hidden");
                cellButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        handleCellClick(newCell, (Button) v);
                    }
                });
            }
        }
        // Implement 4 mines
        Random random = new Random();
        while(mines.size() < 4){
            int x = random.nextInt(12);
            int y = random.nextInt(10);
            mines.add(new Cell(x, y));
        }
        flagCountView = findViewById(R.id.mine_count);
    }


    private void handleCellClick (Cell cell, Button cellButton){
        String currentState = cellStates.get(cell);
        if(toggleButton.isChecked()){
            if(currentState.equals("hidden")){
                cellStates.put(cell, "flagged");
                cellButton.setText(R.string.flag);
                flagCount -=1;
                flagCountView.setText(String.valueOf(flagCount));
            }else if(currentState.equals("flagged")){
                cellStates.put(cell, "hidden");
                cellButton.setText("");
                flagCount += 1;
                flagCountView.setText(String.valueOf(flagCount));
            }
        }else{
            if(currentState.equals("hidden") && !mines.contains(cell)){
                cellStates.put(cell, "revealed");
                int adjacentMines = adjacentMines(cell);
                cellButton.setText(String.valueOf(adjacentMines));
                cellButton.setBackgroundColor(Color.LTGRAY);
                if (adjacentMines == 0) {
                    revealAdjacentCells(cell);
                }
            }else if(mines.contains(cell)){
                revealAllMines();
            }
        }
        checkWinCondition();
    }

    private void checkWinCondition() {
        boolean win = true;
        for(Cell cell : cellStates.keySet()){
            String state = cellStates.get(cell);
            if(!mines.contains(cell) && !state.equals("revealed")){
                win = false;
                break;
            }
        }
        if(win && flagCount == 0 ){
            Intent intent = new Intent(MainActivity.this, ResultPage.class);
            intent.putExtra("Result", "You Win!");
//            intent.putExtra("Time", timeTaken);
            startActivity(intent);
            finish();
        }
    }

    private void revealAllMines() {
        for(Cell mine : mines){
            Button mineButton = findButtonByCell(mine);
            if (mineButton != null) {
                mineButton.setText(R.string.mine);
                mineButton.setBackgroundColor(Color.RED);
            }
        }
        
    }

    private Button findButtonByCell(Cell mine) {

    }

    private void revealAdjacentCells(Cell cell) {
        
    }

    private int adjacentMines(Cell cell) {
        int count =0;
        for(int i = -1; i <= 1; i++){
            for(int j = -1; j <= 1; j++){
                if (i == 0 && j == 0) continue;
                Cell adjacent = new Cell(cell.getRow() + i, cell.getColumn() + j);
                if(mines.contains(adjacent)){
                    count++;
                }
            }
        }
        return count;
    }

    private int dpToPixel(int dp) {
        float density = Resources.getSystem().getDisplayMetrics().density;
        return Math.round(dp * density);
    }


}