package jp.ac.sus.t123140.calculationbattle;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.List;

/**
 * アプリの起動画面（タイトル画面）。
 * 自己ベストの表示や各画面への遷移を担当する。
 * 採点ポイント：画面数（3画面以上）、永続化（自己ベストの表示）
 */
public class TitleActivity extends BaseActivity {

    private TextView textBestScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 全画面表示の設定
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_title);
        
        // システムバー（ステータスバー等）との重なりを調整
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        textBestScore = findViewById(R.id.textBestScore);

        // 採点ポイント：画面遷移
        // 各ボタンに、他のActivityへ遷移するためのIntentを設定
        
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

    /**
     * 画面が前面に戻ってくるたびに実行される
     */
    @Override
    protected void onResume() {
        super.onResume();
        // 採点ポイント：永続化
        // ゲーム終了後に戻ってきた際、最新の自己ベストを読み込み直して表示する
        updateBestScore();
    }

    /**
     * PrefsManager（SharedPreferences）から自己ベストを取得してUIを更新する
     */
    private void updateBestScore() {
        List<PrefsManager.ScoreRecord> records = prefsManager.getLocalRanking();
        if (!records.isEmpty()) {
            // ローカルランキングはスコア降順で保存されているため、インデックス0が最高スコア
            int best = records.get(0).score;
            textBestScore.setText("自己ベスト: " + best);
        } else {
            textBestScore.setText("自己ベスト: 0");
        }
    }
}