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

public class RankingAdapter extends RecyclerView.Adapter<RankingAdapter.ViewHolder> {

    private List<PrefsManager.ScoreRecord> scoreList;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());

    public RankingAdapter(List<PrefsManager.ScoreRecord> scoreList) {
        this.scoreList = scoreList;
    }

    public void updateList(List<PrefsManager.ScoreRecord> newList) {
        this.scoreList = newList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_ranking, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PrefsManager.ScoreRecord record = scoreList.get(position);
        holder.textRank.setText(String.valueOf(position + 1));
        holder.textName.setText("Player"); // ローカル時は固定、世界ランキング時はFirebaseの値をセット
        holder.textScore.setText(record.score + " pts");
        holder.textDate.setText(dateFormat.format(new Date(record.timestamp)));
    }

    @Override
    public int getItemCount() {
        return scoreList.size();
    }

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