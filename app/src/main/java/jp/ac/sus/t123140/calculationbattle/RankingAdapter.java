package jp.ac.sus.t123140.calculationbattle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * ランキング画面のRecyclerView用アダプター。
 * 採点ポイント：プログラムの説明（RecyclerViewを用いた動的なリスト更新の仕組み）
 */
public class RankingAdapter extends RecyclerView.Adapter<RankingAdapter.ViewHolder> {

    private List<PrefsManager.ScoreRecord> scoreList;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());

    /**
     * コンストラクタ
     * @param scoreList 表示対象のスコアリスト
     */
    public RankingAdapter(List<PrefsManager.ScoreRecord> scoreList) {
        this.scoreList = scoreList;
    }

    /**
     * データを更新し、リスト表示をリフレッシュする
     * @param newList 新しいスコアリスト
     */
    public void updateList(List<PrefsManager.ScoreRecord> newList) {
        this.scoreList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // item_ranking.xmlを1行分のレイアウトとして生成
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ranking, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // データの割り当て処理
        PrefsManager.ScoreRecord record = scoreList.get(position);
        
        // 順位（1から開始）
        holder.textRank.setText(String.valueOf(position + 1));
        
        // ユーザー名（Firebaseまたはローカルから取得した名前）
        holder.textName.setText(record.userName != null ? record.userName : "Guest");
        
        // スコア
        holder.textScore.setText(record.score + " pts");
        
        // タイムスタンプを日付文字列に変換
        holder.textDate.setText(dateFormat.format(new Date(record.timestamp)));
    }

    @Override
    public int getItemCount() {
        return scoreList.size();
    }

    /**
     * 1行分のビューを保持するViewHolderクラス
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textRank, textName, textScore, textDate;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textRank = itemView.findViewById(R.id.textRank);
            textName = itemView.findViewById(R.id.textName);
            textScore = itemView.findViewById(R.id.textScore);
            textDate = itemView.findViewById(R.id.textDate);
        }
    }
}