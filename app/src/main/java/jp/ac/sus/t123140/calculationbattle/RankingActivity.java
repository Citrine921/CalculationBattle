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

        tabDifficulty = findViewById(R.id.tabDifficulty);
        tabScope = findViewById(R.id.tabScope);
        recyclerRanking = findViewById(R.id.recyclerRanking);
        textNoData = findViewById(R.id.textNoData);

        firebaseManager = new FirebaseManager();

        // RecyclerViewのセットアップ
        adapter = new RankingAdapter(displayList);
        recyclerRanking.setLayoutManager(new LinearLayoutManager(this));
        recyclerRanking.setAdapter(adapter);

        // タブ切り替え時のリスナー設定
        TabLayout.OnTabSelectedListener tabListener = new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                updateRankingDisplay();
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        };

        tabDifficulty.addOnTabSelectedListener(tabListener);
        tabScope.addOnTabSelectedListener(tabListener);

        // 初期表示
        updateRankingDisplay();
    }

    private void updateRankingDisplay() {
        int difficulty = tabDifficulty.getSelectedTabPosition() + 1;
        int scope = tabScope.getSelectedTabPosition();

        if (scope == 0) {
            loadLocalRanking(difficulty);
        } else {
            loadWorldRanking(difficulty);
        }
    }

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

    private void loadWorldRanking(int difficulty) {
        firebaseManager.getTopRankings(difficulty, new FirebaseManager.RankingListener() {
            @Override
            public void onDataLoaded(List<PrefsManager.ScoreRecord> ranking) {
                updateUI(ranking);
            }

            @Override
            public void onError(String message) {
                Toast.makeText(RankingActivity.this, "Error: " + message, Toast.LENGTH_SHORT).show();
                updateUI(new ArrayList<>());
            }
        });
    }

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