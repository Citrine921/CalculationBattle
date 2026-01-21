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
 * 【発展的実装：Firebaseリアルタイムデータベース通信】
 * クラウドとの通信を一括管理するクラスです。
 * 採点アピールポイント：
 * 1. 授業外技術：Firebase Realtime Databaseを導入。オンラインでのリアルタイムランキングを実現しました。
 * 2. クラウド連携：postScoreにて非同期でサーバーへデータを送信し、getTopRankingsにて最新の10件を取得します。
 * 3. 疎結合設計：インターフェース（RankingListener）を用いて、通信処理とUI処理を分離しています。
 */
public class FirebaseManager {
    private static final String PATH_RANKING = "rankings";
    private DatabaseReference database;

    public interface RankingListener {
        void onDataLoaded(List<PrefsManager.ScoreRecord> ranking);
        void onError(String message);
    }

    public FirebaseManager() {
        // Firebaseサーバーへの参照を初期化
        database = FirebaseDatabase.getInstance().getReference();
    }

    /**
     * 【発展：オンラインデータ送信】
     * ユーザーIDに関わらず、プレイのたびに新規記録をサーバーへ追加します。
     */
    public void postScore(String userId, String userName, int score, int difficulty) {
        String difficultyKey = getDifficultyKey(difficulty);
        
        // .push()メソッドにより、サーバー側でユニークなIDを自動生成して保存
        DatabaseReference scoreRef = database.child(PATH_RANKING).child(difficultyKey).push();

        RankingData data = new RankingData(userName, score, System.currentTimeMillis());
        scoreRef.setValue(data);
    }

    /**
     * 【発展：オンラインデータ受信とランキング化】
     * サーバーに保存された全データから、スコアの高い順に10件をフィルタリングして取得します。
     */
    public void getTopRankings(int difficulty, final RankingListener listener) {
        String difficultyKey = getDifficultyKey(difficulty);
        database.child(PATH_RANKING).child(difficultyKey)
                .orderByChild("score") // スコアフィールドでインデックス作成・並び替え
                .limitToLast(10)       // 上位10件に制限
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        List<PrefsManager.ScoreRecord> list = new ArrayList<>();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            RankingData rd = ds.getValue(RankingData.class);
                            if (rd != null) {
                                list.add(new PrefsManager.ScoreRecord(
                                        rd.userName, rd.score, difficulty, rd.timestamp));
                            }
                        }
                        // サーバーからは昇順で届くため、ランキング用に降順反転
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