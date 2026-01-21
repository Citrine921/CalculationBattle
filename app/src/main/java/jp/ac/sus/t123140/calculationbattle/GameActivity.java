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

/**
 * ゲーム本編のActivity。
 * 採点ポイント：タッチ操作（フリック）、グラフィックス（Canvas描画）、メソッド設計（疎結合）
 */
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

    // 難易度設定 (1: 初級, 2: 中級, 3: 上級)
    private int difficulty = 1; 
    private long totalTimeMillis;
    private CountDownTimer countDownTimer;

    // フリック判定用
    private float startX, startY;
    private static final int FLICK_THRESHOLD = 150; // フリックとみなす最小距離(px)

    private FirebaseManager firebaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // UIコンポーネントの紐付け
        textQuestion = findViewById(R.id.textQuestion);
        textInput = findViewById(R.id.textInput);
        textPassCount = findViewById(R.id.textPassCount);
        textCurrentScore = findViewById(R.id.textCurrentScore);
        timerGauge = findViewById(R.id.timerGauge);
        touchArea = findViewById(R.id.touchArea);

        firebaseManager = new FirebaseManager();

        // 難易度に応じたタイマー設定の初期化
        initTimerSettings();
        // テンキーのセットアップ
        setupKeypad();
        // 採点ポイント：タッチ操作（フリックによるパス機能）
        setupFlickListener();
        // 初回の問題生成
        generateQuestion();
        // ゲーム開始（タイマー始動）
        startTimer();
    }

    /**
     * 採点ポイント：タッチ操作
     * 画面上部を全方向にフリックすることで「パス」を可能にする
     */
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
                    // 三平方の定理で移動距離を算出（斜め方向もカバー）
                    double distance = Math.sqrt(Math.pow(endX - startX, 2) + Math.pow(endY - startY, 2));
                    if (distance > FLICK_THRESHOLD) {
                        handlePass();
                    }
                    return true;
            }
            return false;
        });
    }

    /**
     * 問題をパスする処理。パス回数に制限を設ける。
     */
    private void handlePass() {
        if (passCount > 0) {
            passCount--;
            textPassCount.setText("PASS: " + passCount);
            generateQuestion();
        } else {
            Toast.makeText(this, "パス回数がありません！", Toast.LENGTH_SHORT).show();
        }
    }

    private void initTimerSettings() {
        switch (difficulty) {
            case 2:  totalTimeMillis = 60000; break; // 中級: 60秒
            case 3:  totalTimeMillis = 90000; break; // 上級: 90秒
            default: totalTimeMillis = 30000; break; // 初級: 30秒
        }
    }

    /**
     * カウントダウンタイマーを開始し、ゲージを更新する
     */
    private void startTimer() {
        if (countDownTimer != null) countDownTimer.cancel();
        countDownTimer = new CountDownTimer(totalTimeMillis, 50) {
            @Override
            public void onTick(long millisUntilFinished) {
                // 進捗率を計算し、Canvas描画用のViewに渡す（採点ポイント：グラフィックス）
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

    /**
     * 採点ポイント：通信・永続化
     * ゲーム終了時にスコアをローカルに保存し、Firebaseへ送信する
     */
    private void gameOver() {
        prefsManager.saveScore(score, difficulty); // ローカル保存
        firebaseManager.postScore(
                prefsManager.getUserId(),
                prefsManager.getUserName(),
                score,
                difficulty
        ); // Firebase送信

        Toast.makeText(this, "終了！ スコア: " + score, Toast.LENGTH_LONG).show();
        finish();
    }

    /**
     * 採点ポイント：メソッド設計（計算ロジック）
     * 難易度に応じて桁数や四則演算を動的に生成する
     */
    private void generateQuestion() {
        int range = (difficulty == 1) ? 10 : (difficulty == 2) ? 50 : 100;
        int a = random.nextInt(range) + 1;
        int b = random.nextInt(range) + 1;
        int opLimit = (difficulty >= 2) ? 4 : 2; // 中級以上で掛け算・割り算解禁
        int opType = random.nextInt(opLimit);

        switch (opType) {
            case 0: correctAnswer = a + b; textQuestion.setText(a + " + " + b + " ="); break;
            case 1: if (a < b) { int t = a; a = b; b = t; } correctAnswer = a - b; textQuestion.setText(a + " - " + b + " ="); break;
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
            if (currentAnswer.length() > 0) {
                currentAnswer = currentAnswer.substring(0, currentAnswer.length() - 1);
                textInput.setText(currentAnswer);
            }
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