package jp.ac.sus.t123140.calculationbattle;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FirebaseManager {
    private static final String PATH_RANKING = "rankings";
    private DatabaseReference database;

    public interface RankingListener {
        void onDataLoaded(List<PrefsManager.ScoreRecord> ranking);
        void onError(String message);
    }

    public FirebaseManager() {
        database = FirebaseDatabase.getInstance().getReference();
    }

    /**
     * スコアをFirebaseに送信する
     */
    public void postScore(String userId, String userName, int score, int difficulty) {
        String difficultyKey = getDifficultyKey(difficulty);
        DatabaseReference scoreRef = database.child(PATH_RANKING).child(difficultyKey).child(userId);

        // 送信データ構造
        RankingData data = new RankingData(userName, score, System.currentTimeMillis());
        scoreRef.setValue(data);
    }

    /**
     * 難易度別のランキング上位10件を取得する
     */
    public void getTopRankings(int difficulty, final RankingListener listener) {
        String difficultyKey = getDifficultyKey(difficulty);
        database.child(PATH_RANKING).child(difficultyKey)
                .orderByChild("score")
                .limitToLast(10)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        List<PrefsManager.ScoreRecord> list = new ArrayList<>();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            RankingData rd = ds.getValue(RankingData.class);
                            if (rd != null) {
                                // ScoreRecordのコンストラクタ引数を修正 (userNameを追加)
                                PrefsManager.ScoreRecord record = new PrefsManager.ScoreRecord(
                                        rd.userName, rd.score, difficulty, rd.timestamp);
                                list.add(record);
                            }
                        }
                        Collections.reverse(list);
                        listener.onDataLoaded(list);
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        listener.onError(error.getMessage());
                    }
                });
    }

    private String getDifficultyKey(int diff) {
        switch (diff) {
            case 2:  return "normal";
            case 3:  return "hard";
            default: return "easy";
        }
    }

    public static class RankingData {
        public String userName;
        public int score;
        public long timestamp;

        public RankingData() {}
        public RankingData(String userName, int score, long timestamp) {
            this.userName = userName;
            this.score = score;
            this.timestamp = timestamp;
        }
    }
}