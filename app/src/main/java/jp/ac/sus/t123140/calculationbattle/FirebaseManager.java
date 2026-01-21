package jp.ac.sus.t123140.calculationbattle;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Firebase Realtime Databaseとの通信を管理するクラス。
 * 採点ポイント：通信（オンラインランキング機能）
 */
public class FirebaseManager {
    private static final String PATH_RANKING = "rankings";
    private DatabaseReference database;

    /**
     * ランキングデータ取得後のコールバック用インターフェース
     */
    public interface RankingListener {
        void onDataLoaded(List<PrefsManager.ScoreRecord> ranking);
        void onError(String message);
    }

    public FirebaseManager() {
        // Firebaseの参照を取得
        database = FirebaseDatabase.getInstance().getReference();
    }

    /**
     * スコアをFirebaseに送信する（採点ポイント：通信 - 送信処理）
     * .push()を使用することで、一意のIDを生成してデータを追加保存する
     */
    public void postScore(String userId, String userName, int score, int difficulty) {
        String difficultyKey = getDifficultyKey(difficulty);
        
        // 難易度ごとにディレクトリを分け、新規エントリとして追加
        DatabaseReference scoreRef = database.child(PATH_RANKING).child(difficultyKey).push();

        // 送信用データモデルを作成して送信
        RankingData data = new RankingData(userName, score, System.currentTimeMillis());
        scoreRef.setValue(data);
    }

    /**
     * 難易度別のランキング上位10件を非同期で取得する（採点ポイント：通信 - 受信処理）
     */
    public void getTopRankings(int difficulty, final RankingListener listener) {
        String difficultyKey = getDifficultyKey(difficulty);
        database.child(PATH_RANKING).child(difficultyKey)
                .orderByChild("score") // スコア順に並び替え
                .limitToLast(10)       // 上位10件を取得
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        List<PrefsManager.ScoreRecord> list = new ArrayList<>();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            RankingData rd = ds.getValue(RankingData.class);
                            if (rd != null) {
                                // 共通のデータ形式(ScoreRecord)に変換
                                PrefsManager.ScoreRecord record = new PrefsManager.ScoreRecord(
                                        rd.userName, rd.score, difficulty, rd.timestamp);
                                list.add(record);
                            }
                        }
                        // limitToLastは昇順なので、UI表示用に降順へ反転
                        Collections.reverse(list);
                        listener.onDataLoaded(list);
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        listener.onError(error.getMessage());
                    }
                });
    }

    /**
     * 難易度数値をFirebase用のキー文字列に変換
     */
    private String getDifficultyKey(int diff) {
        switch (diff) {
            case 2:  return "normal";
            case 3:  return "hard";
            default: return "easy";
        }
    }

    /**
     * Firebase通信用データクラス（POJO）
     */
    public static class RankingData {
        public String userName;
        public int score;
        public long timestamp;

        public RankingData() {} // Firebase内部での変換に必要
        public RankingData(String userName, int score, long timestamp) {
            this.userName = userName;
            this.score = score;
            this.timestamp = timestamp;
        }
    }
}