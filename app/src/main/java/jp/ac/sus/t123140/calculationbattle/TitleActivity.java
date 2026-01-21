package jp.ac.sus.t123140.calculationbattle;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.List;

public class TitleActivity extends BaseActivity {

    private TextView textBestScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_title);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        textBestScore = findViewById(R.id.textBestScore);

        // ゲームスタートボタン
        findViewById(R.id.buttonStart).setOnClickListener(v -> {
            Intent intent = new Intent(TitleActivity.this, GameActivity.class);
            startActivity(intent);
        });

        // ランキングボタン
        findViewById(R.id.buttonRanking).setOnClickListener(v -> {
            Intent intent = new Intent(TitleActivity.this, RankingActivity.class);
            startActivity(intent);
        });

        // 設定ボタン
        findViewById(R.id.buttonSetting).setOnClickListener(v -> {
            Intent intent = new Intent(TitleActivity.this, SettingActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateBestScore();
    }

    /**
     * ローカルランキングから自己ベストを取得して表示を更新する
     */
    private void updateBestScore() {
        List<PrefsManager.ScoreRecord> records = prefsManager.getLocalRanking();
        if (!records.isEmpty()) {
            // リストは降順ソートされているため、最初の要素がベストスコア
            int best = records.get(0).score;
            textBestScore.setText("Best Score: " + best);
        } else {
            textBestScore.setText("Best Score: 0");
        }
    }
}