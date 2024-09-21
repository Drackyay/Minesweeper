package com.example.myapplication;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
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
import java.util.Vector;


public class MainActivity extends AppCompatActivity {

    private HashMap<Cell, String> cellStates = new HashMap<>();
    private ToggleButton toggleButton;
    private Set<Cell> mines = new HashSet<>();
    private int flagCount = 4;
    private TextView flagCountView;
    private Vector<Cell> cellsToReveal = new Vector<>();
    private int clock = 0;
    private boolean running = true;
    private TextView clockTextView;
    private boolean isTimerStarted = false;

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
        clockTextView = findViewById(R.id.clock_count);
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
                cellButton.setTag(newCell);
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
        if (!isTimerStarted) {
            isTimerStarted = true;
            running = true;
            runTimer();
        }
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
                revealCell(cell);
                if(adjacentMinesCount(cell)== 0){
                    cellsToReveal.add(cell);
                    revealEmptyCells();
                }
            }else if(mines.contains(cell)){
                revealAllMines();
            }
        }
        checkWinCondition();
    }

    private void revealEmptyCells() {
        while (!cellsToReveal.isEmpty()) {
            Cell cell = cellsToReveal.remove(0);
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    if (i == 0 && j == 0) continue;
                    int newRow = cell.getRow() + i;
                    int newCol = cell.getColumn() + j;
                    if (newRow >= 0 && newRow < 12 && newCol >= 0 && newCol < 10) {
                        Cell adjacentCell = new Cell(newRow, newCol);
                        if ("hidden".equals(cellStates.get(adjacentCell)) && !mines.contains(adjacentCell)) {
                            revealCell(adjacentCell);
                        }
                    }
                }
            }
        }
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
            running = false;
            int finalTime = clock;
            Intent intent = new Intent(MainActivity.this, ResultPage.class);
            intent.putExtra("Result", "You Win.");
            intent.putExtra("Result2", "Good job!");
            intent.putExtra("Time", finalTime);
            startActivity(intent);
            finish();
        }
    }

    private void revealAllMines() {
        for(Cell mine : mines){
            Button mineButton = findButtonByCell(mine);
            if(mineButton != null){
                mineButton.setText(R.string.mine);
                mineButton.setBackgroundColor(Color.RED);
            }
        }
        running = false;
        int finalTime = clock;
        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(MainActivity.this, ResultPage.class);
                intent.putExtra("Result", "You Lost.");
                intent.putExtra("Result2","Try Again!");
                intent.putExtra("Time", finalTime);
                startActivity(intent);
                finish();
            }
        }, 2000);
    }

    private Button findButtonByCell(Cell cell) {
        GridLayout gridLayout = findViewById(R.id.gridLayout);
        for(int i = 0; i < gridLayout.getChildCount(); i++){
            Button button = (Button)gridLayout.getChildAt(i);
            Cell taggedCell = (Cell)button.getTag();
            if(taggedCell != null &&  taggedCell.equals(cell)) {
                return button;
            }
        }
        return null;
    }


    private void revealCell(Cell cell) {
        Button cellButton = findButtonByCell(cell);
        if (cellButton != null && "hidden".equals(cellStates.get(cell))) {
            cellStates.put(cell, "revealed");
            int adjacentMines = adjacentMinesCount(cell);
            if (adjacentMines > 0) {
                cellButton.setText(String.valueOf(adjacentMines));
            } else {
                cellButton.setText("");
                cellsToReveal.add(cell);
            }
            cellButton.setBackgroundColor(Color.LTGRAY);
        }
    }

    private int adjacentMinesCount(Cell cell){
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

    private void runTimer() {
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                int hours =clock/3600;
                int minutes = (clock%3600) / 60;
                int seconds = clock%60;
                String time = String.format("%d:%02d:%02d", hours, minutes, seconds);
                clockTextView.setText(time);
                if (running) {
                    clock++;
                }
                handler.postDelayed(this, 1000);
            }
        });
    }
}