package jp.ac.sus.t123140.calculationbattle;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import java.util.Random;

public class GameActivity extends BaseActivity {

    private TextView textQuestion;
    private TextView textInput;
    private String currentAnswer = "";
    private int correctAnswer;
    private int score = 0;
    private Random random = new Random();

    // 難易度設定 (将来的にインテント等で受け取ることを想定)
    private int difficulty = 1; // 1: 初級, 2: 中級, 3: 上級

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // UIの初期化
        textQuestion = findViewById(R.id.textQuestion);
        textInput = findViewById(R.id.textInput);

        // TODO: Intent等から難易度を受け取る処理
        // difficulty = getIntent().getIntExtra("DIFFICULTY", 1);

        setupKeypad();
        generateQuestion();
    }

    /**
     * 新しい計算問題を生成する
     * 難易度に応じて桁数や演算子を動的に変更
     */
    private void generateQuestion() {
        // 1. 難易度に基づいた数値の範囲（桁数）の決定
        int range;
        switch (difficulty) {
            case 2:  range = 50;  break; // 中級
            case 3:  range = 100; break; // 上級
            default: range = 10;  break; // 初級
        }

        int a = random.nextInt(range) + 1;
        int b = random.nextInt(range) + 1;

        // 2. 難易度に基づいた演算子の決定
        // 初級: + -, 中級以上: + - * /
        int opLimit = (difficulty >= 2) ? 4 : 2;
        int opType = random.nextInt(opLimit);

        switch (opType) {
            case 0: // 加算
                correctAnswer = a + b;
                textQuestion.setText(a + " + " + b + " =");
                break;
            case 1: // 減算
                if (a < b) { int t = a; a = b; b = t; } // 負にならないよう調整
                correctAnswer = a - b;
                textQuestion.setText(a + " - " + b + " =");
                break;
            case 2: // 乗算
                correctAnswer = a * b;
                textQuestion.setText(a + " × " + b + " =");
                break;
            case 3: // 除算 (割り切れる問題を逆算で生成)
                correctAnswer = a; // 答えを先に決める
                int product = a * b;
                textQuestion.setText(product + " ÷ " + b + " =");
                break;
        }
        
        // 入力のリセット
        currentAnswer = "";
        textInput.setText("");
    }

    /**
     * テンキーのクリックリスナーを設定
     */
    private void setupKeypad() {
        int[] buttonIds = {
            R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
            R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9
        };

        for (int id : buttonIds) {
            findViewById(id).setOnClickListener(v -> {
                Button b = (Button) v;
                appendAnswer(b.getText().toString());
            });
        }

        findViewById(R.id.btnClear).setOnClickListener(v -> {
            currentAnswer = "";
            textInput.setText("");
        });

        findViewById(R.id.btnDel).setOnClickListener(v -> {
            if (currentAnswer.length() > 0) {
                currentAnswer = currentAnswer.substring(0, currentAnswer.length() - 1);
                textInput.setText(currentAnswer);
            }
        });
    }

    /**
     * 数字を入力し、正誤判定を行う
     */
    private void appendAnswer(String num) {
        currentAnswer += num;
        textInput.setText(currentAnswer);

        // 答え合わせ
        if (currentAnswer.equals(String.valueOf(correctAnswer))) {
            score++;
            generateQuestion();
        }
        // 間違い判定（桁数オーバー等）をここに入れることも可能
    }
}