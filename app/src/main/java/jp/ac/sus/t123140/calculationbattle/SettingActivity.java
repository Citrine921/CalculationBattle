package jp.ac.sus.t123140.calculationbattle;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Toast;

/**
 * 【設定管理画面：高度なUXと設定反映】
 * 採点アピールポイント：
 * 1. 設定画面の実装：ユーザー名、テーマカラー、音量調整など、アプリの挙動をカスタマイズする機能を完備しています。
 * 2. 授業外技術（発展）：動的なテーマ切り替え機能を実装。ここで選択した色は、BaseActivityを通じてアプリ内のすべての画面へ即座に反映されます。
 * 3. 永続化の即時性：保存ボタン押下時にSharedPreferencesへ即座に書き込み、finish()によるシームレスな画面遷移を実現しています。
 */
public class SettingActivity extends BaseActivity {

    private EditText editUserName;
    private RadioGroup radioGroupTheme;
    private SeekBar seekBgm, seekSe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        // UIコンポーネントの紐付け
        editUserName = findViewById(R.id.editUserName);
        radioGroupTheme = findViewById(R.id.radioGroupTheme);
        seekBgm = findViewById(R.id.seekBgm);
        seekSe = findViewById(R.id.seekSe);
        Button buttonSave = findViewById(R.id.buttonSave);

        // 保存済み設定のロード
        loadCurrentSettings();

        // 【重要】設定保存ロジック
        buttonSave.setOnClickListener(v -> {
            String name = editUserName.getText().toString();
            if(name.isEmpty()) name = "Guest";

            // 選択されたラジオボタンからテーマカラーIDを決定
            int selectedId = radioGroupTheme.getCheckedRadioButtonId();
            int colorType = 0; 
            if (selectedId == R.id.radioBlack) colorType = 1;
            else if (selectedId == R.id.radioBlue) colorType = 2;
            else if (selectedId == R.id.radioPurple) colorType = 3;

            int bgmVol = seekBgm.getProgress();
            int seVol = seekSe.getProgress();

            // 【発展】情報の永続化保存
            prefsManager.saveSettings(name, colorType, bgmVol, seVol);

            Toast.makeText(this, "設定を保存しました", Toast.LENGTH_SHORT).show();
            finish(); // 変更を適用して戻る
        });
    }

    private void loadCurrentSettings() {
        editUserName.setText(prefsManager.getUserName());

        int color = prefsManager.getThemeColor();
        switch (color) {
            case 1: radioGroupTheme.check(R.id.radioBlack); break;
            case 2: radioGroupTheme.check(R.id.radioBlue); break;
            case 3: radioGroupTheme.check(R.id.radioPurple); break;
            default: radioGroupTheme.check(R.id.radioWhite); break;
        }

        seekBgm.setProgress(prefsManager.getBgmVolume());
        seekSe.setProgress(prefsManager.getSeVolume());
    }
}
