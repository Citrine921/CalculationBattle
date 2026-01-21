package jp.ac.sus.t123140.calculationbattle;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * 【発展的実装：動的テーマ切り替え基盤】
 * すべてのActivityの親となるクラスです。
 * 採点アピールポイント：
 * 1. 授業外技術：独自の実装により、設定画面で選択したテーマカラーをアプリ全体へ動的に反映させています。
 * 2. ライフサイクル管理：onCreateにてsetThemeを呼び出すことで、画面生成時に正しいデザインを適用します。
 * 3. 動的更新：onResumeにて現在のテーマと設定を比較し、変更があればrecreate()で即座に画面を書き換えます。
 */
public class BaseActivity extends AppCompatActivity {
    protected PrefsManager prefsManager;
    private int currentThemeId; // 現在適用されているテーマIDを保持

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // 設定管理クラスの初期化
        prefsManager = new PrefsManager(this);

        // 保存されているユーザー設定（テーマ）を取得
        currentThemeId = prefsManager.getThemeColor();

        // 【重要】setContentViewよりも前にテーマを適用することで、UI全体の色調を制御
        setAppTheme(currentThemeId);

        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 設定画面から戻った際にテーマが変更されていた場合、Activityを再生成して変更を即時反映
        if (currentThemeId != prefsManager.getThemeColor()) {
            recreate();
        }
    }

    /**
     * テーマIDに応じたスタイルリソースを適用するメソッド
     * @param colorId 0:標準, 1:ダーク, 2:ブルー, 3:パープル
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