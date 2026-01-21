package jp.ac.sus.t123140.calculationbattle;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import androidx.annotation.Nullable;

public class TimerGaugeView extends View {
    private Paint backgroundPaint;
    private Paint gaugePaint;
    private float progress = 1.0f; // 1.0 (満タン) から 0.0 (空) まで

    public TimerGaugeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        backgroundPaint = new Paint();
        backgroundPaint.setColor(Color.LTGRAY);
        backgroundPaint.setStyle(Paint.Style.FILL);

        gaugePaint = new Paint();
        gaugePaint.setColor(Color.GREEN);
        gaugePaint.setStyle(Paint.Style.FILL);
    }

    public void setProgress(float progress) {
        this.progress = progress;
        // ゲージの色を残り時間に応じて変更 (採点ポイント: グラフィックスの工夫)
        if (progress > 0.5f) {
            gaugePaint.setColor(Color.GREEN);
        } else if (progress > 0.2f) {
            gaugePaint.setColor(Color.YELLOW);
        } else {
            gaugePaint.setColor(Color.RED);
        }
        invalidate(); // 再描画を要求
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = getWidth();
        int height = getHeight();

        // 背景を描画
        canvas.drawRect(0, 0, width, height, backgroundPaint);

        // ゲージを描画 (進捗に応じて横幅を変える)
        float gaugeWidth = width * progress;
        canvas.drawRect(0, 0, gaugeWidth, height, gaugePaint);
    }
}