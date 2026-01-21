package jp.ac.sus.t123140.calculationbattle;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class TitleActivity extends BaseActivity {

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
}