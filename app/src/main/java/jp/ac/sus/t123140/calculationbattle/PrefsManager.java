package jp.ac.sus.t123140.calculationbattle;

import android.content.Context;
import android.content.SharedPreferences;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public class PrefsManager {
    private static final String PREF_NAME = "CalcBattlePrefs";
    private SharedPreferences prefs;

    // キー定数
    public static final String KEY_USER_NAME = "user_name";
    public static final String KEY_THEME_COLOR = "theme_color"; // 0:White, 1:Black, 2:Blue, 3:Purple
    public static final String KEY_BGM_VOLUME = "bgm_volume";
    public static final String KEY_SE_VOLUME = "se_volume";
    public static final String KEY_USER_ID = "user_id";
    public static final String KEY_LOCAL_RANKING = "local_ranking";

    public PrefsManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    // --- 基本設定 ---

    // ユーザーID (初回取得時に生成)
    public String getUserId() {
        String id = prefs.getString(KEY_USER_ID, null);
        if (id == null) {
            id = UUID.randomUUID().toString();
            prefs.edit().putString(KEY_USER_ID, id).apply();
        }
        return id;
    }

    public void saveSettings(String name, int themeColor, int bgmVol, int seVol) {
        prefs.edit()
                .putString(KEY_USER_NAME, name)
                .putInt(KEY_THEME_COLOR, themeColor)
                .putInt(KEY_BGM_VOLUME, bgmVol)
                .putInt(KEY_SE_VOLUME, seVol)
                .apply();
    }

    public String getUserName() { return prefs.getString(KEY_USER_NAME, "Guest"); }
    public int getThemeColor() { return prefs.getInt(KEY_THEME_COLOR, 0); } // デフォルト0(白)
    public int getBgmVolume() { return prefs.getInt(KEY_BGM_VOLUME, 50); }
    public int getSeVolume() { return prefs.getInt(KEY_SE_VOLUME, 50); }

    // --- スコア履歴管理 (JSON形式で保存) ---

    // スコア情報の内部クラス
    public static class ScoreRecord {
        public int score;
        public int difficulty; // 1~10
        public long timestamp;

        public ScoreRecord(int score, int difficulty, long timestamp) {
            this.score = score;
            this.difficulty = difficulty;
            this.timestamp = timestamp;
        }
    }

    // スコアを保存し、上位10件を保持する
    public void saveScore(int score, int difficulty) {
        List<ScoreRecord> records = getLocalRanking();
        records.add(new ScoreRecord(score, difficulty, System.currentTimeMillis()));

        // スコア降順でソート
        Collections.sort(records, (o1, o2) -> Integer.compare(o2.score, o1.score));

        // 上位10件に絞る
        if (records.size() > 10) {
            records = records.subList(0, 10);
        }

        // JSON配列に変換して保存
        JSONArray jsonArray = new JSONArray();
        try {
            for (ScoreRecord record : records) {
                JSONObject obj = new JSONObject();
                obj.put("score", record.score);
                obj.put("diff", record.difficulty);
                obj.put("time", record.timestamp);
                jsonArray.put(obj);
            }
            prefs.edit().putString(KEY_LOCAL_RANKING, jsonArray.toString()).apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 保存されたランキングを取得
    public List<ScoreRecord> getLocalRanking() {
        List<ScoreRecord> list = new ArrayList<>();
        String jsonStr = prefs.getString(KEY_LOCAL_RANKING, "");
        if (!jsonStr.isEmpty()) {
            try {
                JSONArray jsonArray = new JSONArray(jsonStr);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject obj = jsonArray.getJSONObject(i);
                    list.add(new ScoreRecord(
                            obj.getInt("score"),
                            obj.getInt("diff"),
                            obj.getLong("time")
                    ));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return list;
    }
}