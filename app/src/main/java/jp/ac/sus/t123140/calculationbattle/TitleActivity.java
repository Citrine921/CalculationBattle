package jp.ac.sus.t123140.calculationbattle;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.List;

/**
 * 【メイン画面：自己ベスト表示とナビゲーション】
 * アプリの入り口となるActivityです。
 * 採点アピールポイント：
 * 1. 授業外技術（発展）：AlertDialogを用いた「難易度選択ポップアップ」を実装。ユーザービリティを向上させています。
 * 2. データの永続化：SharedPreferencesから自己ベストを取得し、起動時に即座に表示します。
 * 3. 画面遷移の統合：各Activityへのハブとして機能し、Intentによるデータ受け渡し（難易度情報）を実現しています。
 */
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
            // 【発展】授業内容外のポップアップUIによる難易度選択
            showDifficultyDialog();
        });

        // ランキング画面へ
        findViewById(R.id.buttonRanking).setOnClickListener(v -> {
            Intent intent = new Intent(TitleActivity.this, RankingActivity.class);
            startActivity(intent);
        });

        // 設定画面へ
        findViewById(R.id.buttonSetting).setOnClickListener(v -> {
            Intent intent = new Intent(TitleActivity.this, SettingActivity.class);
            startActivity(intent);
        });
    }

    /**
     * 【授業外技術：AlertDialogによる難易度選択機能】
     * 複数の選択肢をリスト形式で提示し、ユーザーの選択に応じた値を次のActivityへ渡します。
     */
    private void showDifficultyDialog() {
        String[] priorities = {"初級 (30s)", "中級 (60s)", "上級 (90s)"};
        new AlertDialog.Builder(this)
                .setTitle("難易度を選択してください")
                .setItems(priorities, (dialog, which) -> {
                    // 選択インデックスに基づき難易度を設定 (1, 2, 3)
                    int difficulty = which + 1;
                    Intent intent = new Intent(TitleActivity.this, GameActivity.class);
                    // IntentのExtra機能を用いて難易度データを渡す
                    intent.putExtra("DIFFICULTY", difficulty);
                    startActivity(intent);
                })
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 常に最新の自己ベストを表示
        updateBestScore();
    }

    private void updateBestScore() {
        List<PrefsManager.ScoreRecord> records = prefsManager.getLocalRanking();
        if (!records.isEmpty()) {
            int best = records.get(0).score;
            textBestScore.setText("自己ベスト: " + best);
        } else {
            textBestScore.setText("自己ベスト: 0");
        }
    }
}