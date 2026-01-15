package jp.ac.sus.t123140.calculationbattle;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.Toast;

// BaseActivityを継承すること！
public class SettingActivity extends BaseActivity {

    private EditText editUserName;
    private RadioGroup radioGroupTheme;
    private SeekBar seekBgm, seekSe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        // Viewの紐付け
        editUserName = findViewById(R.id.editUserName);
        radioGroupTheme = findViewById(R.id.radioGroupTheme);
        seekBgm = findViewById(R.id.seekBgm);
        seekSe = findViewById(R.id.seekSe);
        Button buttonSave = findViewById(R.id.buttonSave);

        // 現在の設定を読み込んで画面にセット
        loadCurrentSettings();

        // 保存ボタンの処理
        buttonSave.setOnClickListener(v -> {
            String name = editUserName.getText().toString();
            if(name.isEmpty()) name = "Guest";

            // 選択された色のIDを決定 (0~3)
            int selectedId = radioGroupTheme.getCheckedRadioButtonId();
            int colorType = 0;
            if (selectedId == R.id.radioBlack) colorType = 1;
            else if (selectedId == R.id.radioBlue) colorType = 2;
            else if (selectedId == R.id.radioPurple) colorType = 3;

            int bgmVol = seekBgm.getProgress();
            int seVol = seekSe.getProgress();

            // 保存処理 (BaseActivityで持っている prefsManager を使用)
            prefsManager.saveSettings(name, colorType, bgmVol, seVol);

            Toast.makeText(this, "Settings Saved!", Toast.LENGTH_SHORT).show();
            finish(); // 画面を閉じて戻る
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