package com.example.prodigy_ad_03;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView timeTextView;
    private Button startButton, pauseButton, resetButton, lapButton;
    private ListView lapsListView;

    private boolean running = false;
    private long startTime = 0L;
    private long timeInMillis = 0L;
    private long pauseTime = 0L;

    private Handler handler = new Handler();
    private ArrayList<String> laps = new ArrayList<>();
    private ArrayAdapter<String> adapter;

    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            if (running) {
                timeInMillis = System.currentTimeMillis() - startTime + pauseTime;
                updateTimer();
                handler.postDelayed(this, 10);
            }
        }
    };

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        timeTextView = findViewById(R.id.timeTextView);
        startButton = findViewById(R.id.startButton);
        pauseButton = findViewById(R.id.pauseButton);
        resetButton = findViewById(R.id.resetButton);
        lapButton = findViewById(R.id.lapButton);
        lapsListView = findViewById(R.id.lapsListView);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, laps);
        lapsListView.setAdapter(adapter);

        startButton.setOnClickListener(v -> startStopwatch());
        pauseButton.setOnClickListener(v -> pauseStopwatch());
        resetButton.setOnClickListener(v -> resetStopwatch());
        lapButton.setOnClickListener(v -> recordLap());

        updateTimer();
    }

    private void startStopwatch() {
        if (!running) {
            startTime = System.currentTimeMillis();
            running = true;
            handler.post(timerRunnable);
        }
    }

    private void pauseStopwatch() {
        if (running) {
            pauseTime += System.currentTimeMillis() - startTime;
            running = false;
            handler.removeCallbacks(timerRunnable);
        }
    }

    private void resetStopwatch() {
        running = false;
        handler.removeCallbacks(timerRunnable);
        timeInMillis = 0L;
        startTime = 0L;
        pauseTime = 0L;
        laps.clear();
        adapter.notifyDataSetChanged();
        updateTimer();
    }

    private void recordLap() {
        if (running) {
            laps.add(formatTime(timeInMillis));
            adapter.notifyDataSetChanged();
        }
    }

    private void updateTimer() {
        timeTextView.setText(formatTime(timeInMillis));
    }

    private String formatTime(long millis) {
        int minutes = (int) (millis / 60000);
        int seconds = (int) ((millis / 1000) % 60);
        int milliseconds = (int) (millis % 1000) / 10;
        return String.format(Locale.getDefault(), "%02d:%02d:%02d", minutes, seconds, milliseconds);
    }
}