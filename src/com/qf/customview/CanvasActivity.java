package com.qf.customview;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.View;

public class CanvasActivity extends Activity {
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_main);
        setContentView(new CanvasView(this));
    }
    
    private static class CanvasView extends View {
        
        int mw;
        int mh;
        private Paint mp = new Paint();
        
        public CanvasView(Context context) {
            super(context);
            mp.setColor(Color.BLACK);
            mp.setStyle(Paint.Style.STROKE);
        }
        
        @Override
        public void onSizeChanged(int w, int h, int oldw, int oldh) {
            mw = w;
            mh = h;
        }

        @Override
        protected void onDraw(Canvas canvas) {
            canvas.translate(mw/2, mh/2);
            float l = (float)Math.min(mw, mh)/2*0.9f;
            // rect
//            RectF rect = new RectF(-l, -l, l, l);
//            for(int i=0; i<20; i++) {
//                canvas.scale(0.9f, 0.9f);
//                canvas.drawRect(rect, mp);
//            }
            
            //circle
            canvas.drawCircle(0, 0, l, mp);
            canvas.drawCircle(0, 0, l-20, mp);
            for (int i=0; i<360; i+=10) {
                canvas.drawLine(0, l, 0, l-20, mp);
                canvas.rotate(10);
            }
            canvas.drawLine(0, 0, l, 0, mp);
            canvas.rotate(15);
        }
    }

}
