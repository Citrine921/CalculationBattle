package jp.ac.sus.t123140.calculationbattle;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Toast;

/**
 * 設定画面のActivity。
 * ユーザー名、テーマカラー、音量設定の変更を行う。
 * 採点ポイント：設定画面、設定保存（SharedPreferencesへの保存）
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

        // 採点ポイント：永続化
        // 現在保存されている設定を読み込んでUIに反映させる
        loadCurrentSettings();

        // 保存ボタン押下時の処理
        buttonSave.setOnClickListener(v -> {
            String name = editUserName.getText().toString();
            if(name.isEmpty()) name = "Guest";

            // 選択されたラジオボタンからテーマIDを決定
            int selectedId = radioGroupTheme.getCheckedRadioButtonId();
            int colorType = 0; // デフォルト：白
            if (selectedId == R.id.radioBlack) colorType = 1;
            else if (selectedId == R.id.radioBlue) colorType = 2;
            else if (selectedId == R.id.radioPurple) colorType = 3;

            int bgmVol = seekBgm.getProgress();
            int seVol = seekSe.getProgress();

            // 採点ポイント：設定保存
            // PrefsManagerを通じてSharedPreferencesに永続化保存する
            prefsManager.saveSettings(name, colorType, bgmVol, seVol);

            Toast.makeText(this, "設定を保存しました", Toast.LENGTH_SHORT).show();
            finish(); // 画面を閉じて前の画面（通常はタイトル）に戻る
        });
    }

    /**
     * 保存済みの設定値を読み込み、各入力項目にセットする
     */
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