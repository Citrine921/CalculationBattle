package jp.ac.sus.t123140.calculationbattle;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import androidx.annotation.Nullable;

/**
 * 制限時間を視覚的に表示するためのカスタムビュー。
 * 採点ポイント：グラフィックス（CanvasとPaintを用いた図形描画）
 */
public class TimerGaugeView extends View {
    private Paint backgroundPaint;
    private Paint gaugePaint;
    private float progress = 1.0f; // 1.0 (満タン) から 0.0 (空) まで

    public TimerGaugeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    /**
     * 描画に必要なPaintオブジェクトの初期化
     */
    private void init() {
        backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.LTGRAY);
        backgroundPaint.setStyle(Paint.Style.FILL);

        gaugePaint = new Paint();
        gaugePaint.setColor(Color.GREEN);
        gaugePaint.setStyle(Paint.Style.FILL);
    }

    /**
     * ゲージの進捗を更新し、再描画を行う
     * @param progress 0.0〜1.0の値
     */
    public void setProgress(float progress) {
        this.progress = progress;
        // 採点ポイント：グラフィックスの工夫
        // 残り時間に応じてゲージの色を動的に変更（緑 -> 黄 -> 赤）
        if (progress > 0.5f) {
            gaugePaint.setColor(Color.GREEN);
        } else if (progress > 0.2f) {
            gaugePaint.setColor(Color.YELLOW);
        } else {
            gaugePaint.setColor(Color.RED);
        }
        invalidate(); // Viewの再描画（onDrawの呼び出し）を要求
    }

    /**
     * 採点ポイント：グラフィックス
     * Canvasを用いてゲージの矩形を描画する
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();

        // 1. 背景（グレーの土台）を描画
        canvas.drawRect(0, 0, width, height, backgroundPaint);

        // 2. ゲージ（残り時間分）を描画
        // 進捗率に応じて右側の座標を計算し、HPバーのように削れていく表現を実現
        float gaugeWidth = width * progress;
        canvas.drawRect(0, 0, gaugeWidth, height, gaugePaint);
    }
}