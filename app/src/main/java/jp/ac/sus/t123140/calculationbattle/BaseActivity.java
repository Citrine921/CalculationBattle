package jp.ac.sus.t123140.calculationbattle;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {
    protected PrefsManager prefsManager;
    private int currentThemeId; // 今適用されているテーマの色ID

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // 1. PrefsManagerの初期化
        prefsManager = new PrefsManager(this);

        // 2. 設定されているテーマIDを取得
        currentThemeId = prefsManager.getThemeColor();

        // 3. テーマの適用 (必ず super.onCreate や setContentView の前に！)
        setAppTheme(currentThemeId);

        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 4. 設定画面から戻ってきた時、色が変更されていたら画面を再生成する
        if (currentThemeId != prefsManager.getThemeColor()) {
            recreate();
        }
    }

    // IDに応じたスタイルをセットするメソッド
    private void setAppTheme(int colorId) {
        switch (colorId) {
            case 1: // 黒
                setTheme(R.style.Theme_CalculationBattle_Black);
                break;
            case 2: // 青
                setTheme(R.style.Theme_CalculationBattle_Blue);
                break;
            case 3: // 紫
                setTheme(R.style.Theme_CalculationBattle_Purple);
                break;
            default: // 0: 白 (デフォルト)
                setTheme(R.style.Theme_CalculationBattle);
                break;
        }
    }
}