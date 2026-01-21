package jp.ac.sus.t123140.calculationbattle;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Random;

public class GameActivity extends BaseActivity {

    private TextView textQuestion;
    private TextView textInput;
    private TextView textPassCount;
    private TextView textCurrentScore;
    private TimerGaugeView timerGauge;
    private View touchArea;
    
    private String currentAnswer = "";
    private int correctAnswer;
    private int score = 0;
    private int passCount = 3;
    private Random random = new Random();

    private int difficulty = 1; 
    private long totalTimeMillis;
    private CountDownTimer countDownTimer;

    private float startX, startY;
    private static final int FLICK_THRESHOLD = 150;

    private FirebaseManager firebaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        textQuestion = findViewById(R.id.textQuestion);
        textInput = findViewById(R.id.textInput);
        textPassCount = findViewById(R.id.textPassCount);
        textCurrentScore = findViewById(R.id.textCurrentScore);
        timerGauge = findViewById(R.id.timerGauge);
        touchArea = findViewById(R.id.touchArea);

        firebaseManager = new FirebaseManager();

        initTimerSettings();
        setupKeypad();
        setupFlickListener();
        generateQuestion();
        startTimer();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setupFlickListener() {
        touchArea.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    startX = event.getX();
                    startY = event.getY();
                    return true;
                case MotionEvent.ACTION_UP:
                    float endX = event.getX();
                    float endY = event.getY();
                    double distance = Math.sqrt(Math.pow(endX - startX, 2) + Math.pow(endY - startY, 2));
                    if (distance > FLICK_THRESHOLD) {
                        handlePass();
                    }
                    return true;
            }
            return false;
        });
    }

    private void handlePass() {
        if (passCount > 0) {
            passCount--;
            textPassCount.setText("PASS: " + passCount);
            generateQuestion();
        } else {
            Toast.makeText(this, "No more passes!", Toast.LENGTH_SHORT).show();
        }
    }

    private void initTimerSettings() {
        switch (difficulty) {
            case 2:  totalTimeMillis = 60000; break;
            case 3:  totalTimeMillis = 90000; break;
            default: totalTimeMillis = 30000; break;
        }
    }

    private void startTimer() {
        if (countDownTimer != null) countDownTimer.cancel();
        countDownTimer = new CountDownTimer(totalTimeMillis, 50) {
            @Override
            public void onTick(long millisUntilFinished) {
                float progress = (float) millisUntilFinished / totalTimeMillis;
                timerGauge.setProgress(progress);
            }
            @Override
            public void onFinish() {
                timerGauge.setProgress(0f);
                gameOver();
            }
        }.start();
    }

    private void gameOver() {
        // スコアを保存 (ローカル)
        prefsManager.saveScore(score, difficulty);

        // スコアを送信 (Firebase)
        firebaseManager.postScore(
                prefsManager.getUserId(),
                prefsManager.getUserName(),
                score,
                difficulty
        );

        Toast.makeText(this, "Game Over! Score: " + score, Toast.LENGTH_LONG).show();
        finish();
    }

    private void generateQuestion() {
        int range;
        switch (difficulty) {
            case 2:  range = 50;  break;
            case 3:  range = 100; break;
            default: range = 10;  break;
        }

        int a = random.nextInt(range) + 1;
        int b = random.nextInt(range) + 1;
        int opLimit = (difficulty >= 2) ? 4 : 2;
        int opType = random.nextInt(opLimit);

        switch (opType) {
            case 0: correctAnswer = a + b; textQuestion.setText(a + " + " + b + " ="); break;
            case 1: if (a < b) { int t = a; a = b; b = t; } correctAnswer = a - b; textQuestion.setText((correctAnswer + b) + " - " + b + " ="); break;
            case 2: correctAnswer = a * b; textQuestion.setText(a + " × " + b + " ="); break;
            case 3: correctAnswer = a; int product = a * b; textQuestion.setText(product + " ÷ " + b + " ="); break;
        }
        currentAnswer = "";
        textInput.setText("");
    }

    private void setupKeypad() {
        int[] buttonIds = {R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4, R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9};
        for (int id : buttonIds) {
            findViewById(id).setOnClickListener(v -> appendAnswer(((Button) v).getText().toString()));
        }
        findViewById(R.id.btnClear).setOnClickListener(v -> { currentAnswer = ""; textInput.setText(""); });
        findViewById(R.id.btnDel).setOnClickListener(v -> {
            if (currentAnswer.length() > 0) { currentAnswer = currentAnswer.substring(0, currentAnswer.length() - 1); textInput.setText(currentAnswer); }
        });
    }

    private void appendAnswer(String num) {
        currentAnswer += num;
        textInput.setText(currentAnswer);
        if (currentAnswer.equals(String.valueOf(correctAnswer))) {
            score++;
            textCurrentScore.setText("SCORE: " + score);
            generateQuestion();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) countDownTimer.cancel();
    }
}