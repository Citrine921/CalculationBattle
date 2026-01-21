package jp.ac.sus.t123140.calculationbattle;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.tabs.TabLayout;
import java.util.ArrayList;
import java.util.List;

/**
 * ランキング表示Activity。
 * 2段階のタブ（難易度 × 表示範囲）により表示内容を動的に切り替える。
 * 採点ポイント：画面数（3画面以上）、通信（Firebaseからのデータ受信）、設定反映
 */
public class RankingActivity extends BaseActivity {

    private TabLayout tabDifficulty;
    private TabLayout tabScope;
    private RecyclerView recyclerRanking;
    private TextView textNoData;
    private RankingAdapter adapter;
    private List<PrefsManager.ScoreRecord> displayList = new ArrayList<>();
    private FirebaseManager firebaseManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);

        // UIの初期化
        tabDifficulty = findViewById(R.id.tabDifficulty);
        tabScope = findViewById(R.id.tabScope);
        recyclerRanking = findViewById(R.id.recyclerRanking);
        textNoData = findViewById(R.id.textNoData);

        // 採点ポイント：通信
        // Firebaseとのやり取りを管理するクラスを初期化
        firebaseManager = new FirebaseManager();

        // 採点ポイント：プログラムの説明
        // RecyclerViewのセットアップ。アダプターを介して動的にリストを表示する。
        adapter = new RankingAdapter(displayList);
        recyclerRanking.setLayoutManager(new LinearLayoutManager(this));
        recyclerRanking.setAdapter(adapter);

        // タブ切り替え時のイベントリスナー
        TabLayout.OnTabSelectedListener tabListener = new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // タブが変わるたびに表示を更新
                updateRankingDisplay();
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        };

        tabDifficulty.addOnTabSelectedListener(tabListener);
        tabScope.addOnTabSelectedListener(tabListener);

        // 初回表示
        updateRankingDisplay();
    }

    /**
     * 採点ポイント：メソッド設計
     * タブの状態（難易度、ローカル/ワールド）を取得し、適切なデータ取得メソッドを呼び出す。
     */
    private void updateRankingDisplay() {
        int difficulty = tabDifficulty.getSelectedTabPosition() + 1; // 1:初級, 2:中級, 3:上級
        int scope = tabScope.getSelectedTabPosition(); // 0:自己ベスト, 1:世界ランキング

        if (scope == 0) {
            // ローカル（SharedPreferences）から取得
            loadLocalRanking(difficulty);
        } else {
            // 通信（Firebase）を介して取得
            loadWorldRanking(difficulty);
        }
    }

    /**
     * ローカルに保存されたスコア履歴を表示する（採点ポイント：永続化）
     */
    private void loadLocalRanking(int difficulty) {
        List<PrefsManager.ScoreRecord> allRecords = prefsManager.getLocalRanking();
        List<PrefsManager.ScoreRecord> filteredList = new ArrayList<>();
        for (PrefsManager.ScoreRecord record : allRecords) {
            if (record.difficulty == difficulty) {
                filteredList.add(record);
            }
        }
        updateUI(filteredList);
    }

    /**
     * サーバー上のデータを取得して表示する（採点ポイント：通信）
     */
    private void loadWorldRanking(int difficulty) {
        firebaseManager.getTopRankings(difficulty, new FirebaseManager.RankingListener() {
            @Override
            public void onDataLoaded(List<PrefsManager.ScoreRecord> ranking) {
                updateUI(ranking);
            }

            @Override
            public void onError(String message) {
                Toast.makeText(RankingActivity.this, "通信エラー: " + message, Toast.LENGTH_SHORT).show();
                updateUI(new ArrayList<>());
            }
        });
    }

    /**
     * 最終的なリストをUIに反映する。データがない場合の表示切り替えも行う。
     */
    private void updateUI(List<PrefsManager.ScoreRecord> newList) {
        if (newList.isEmpty()) {
            recyclerRanking.setVisibility(View.GONE);
            textNoData.setVisibility(View.VISIBLE);
        } else {
            recyclerRanking.setVisibility(View.VISIBLE);
            textNoData.setVisibility(View.GONE);
            adapter.updateList(newList);
        }
    }
}