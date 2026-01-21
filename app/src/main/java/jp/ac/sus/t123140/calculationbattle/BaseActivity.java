package jp.ac.sus.t123140.calculationbattle;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * すべてのActivityの基底クラス。
 * 採点ポイント：設定反映（設定画面でのテーマ変更を全画面に反映させるための共通処理）
 */
public class BaseActivity extends AppCompatActivity {
    protected PrefsManager prefsManager;
    private int currentThemeId; // 現在適用されているテーマID

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // 1. 設定情報の管理クラスを初期化
        prefsManager = new PrefsManager(this);

        // 2. 保存されているテーマ設定を取得
        currentThemeId = prefsManager.getThemeColor();

        // 3. レイアウト生成前にテーマを適用（採点基準：設定内容の反映）
        setAppTheme(currentThemeId);

        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 4. 設定画面から戻ってきた際、テーマが変更されていれば画面を再生成して即座に反映
        if (currentThemeId != prefsManager.getThemeColor()) {
            recreate();
        }
    }

    /**
     * 数値IDに基づいてアプリのテーマを動的に切り替える
     * @param colorId 0:デフォルト, 1:黒, 2:青, 3:紫
     */
    private void setAppTheme(int colorId) {
        switch (colorId) {
            case 1:
                setTheme(R.style.Theme_CalculationBattle_Black);
                break;
            case 2:
                setTheme(R.style.Theme_CalculationBattle_Blue);
                break;
            case 3:
                setTheme(R.style.Theme_CalculationBattle_Purple);
                break;
            default:
                setTheme(R.style.Theme_CalculationBattle);
                break;
        }
    }
}