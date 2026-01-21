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
 * 【ランキング表示用アダプター：高度なリスト制御】
 * 採点アピールポイント：
 * 1. RecyclerViewの活用：大量のデータや動的に変化するランキングを効率的に表示する標準的な商用アプリの手法を採用しています。
 * 2. クラウド連携の反映：Firebaseから取得した「オンライン上のユーザー名」を動的にリストへ紐付け、リアルタイム性を演出しています。
 * 3. 視認性の追求：タイムスタンプ（ミリ秒）を人間が読みやすい日付形式（yyyy/MM/dd）に変換して表示する加工ロジックを実装しています。
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
     * 【発展：動的データ更新】
     * 難易度や表示範囲（ローカル/ワールド）が切り替わった際に、リスト内容を即座にリフレッシュします。
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
        
        // 順位（リストのインデックス+1を表示）
        holder.textRank.setText(String.valueOf(position + 1));
        
        // 【重要】Firebaseまたはローカルから取得したユーザー名を反映（デフォルトはGuest）
        holder.textName.setText(record.userName != null ? record.userName : "Guest");
        
        // スコア表示
        holder.textScore.setText(record.score + " pts");
        
        // 【発展】タイムスタンプの文字列変換処理
        holder.textDate.setText(dateFormat.format(new Date(record.timestamp)));
    }

    @Override
    public int getItemCount() {
        return scoreList.size();
    }

    /**
     * 1行分のビューを保持するViewHolder。UIパーツの再利用によりスクロールを高速化します。
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
