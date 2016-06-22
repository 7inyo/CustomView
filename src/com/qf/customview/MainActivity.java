package com.qf.customview;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity implements OnClickListener {

    private Button pieview, canvas_a, leaf;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // setContentView(pie);
        init();
    }

    private void init() {
        pieview = (Button) findViewById(R.id.pieview_activity);
        pieview.setOnClickListener(this);
        canvas_a = (Button) findViewById(R.id.canvas_activity);
        canvas_a.setOnClickListener(this);
        leaf = (Button) findViewById(R.id.leaf_activity);
        leaf.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.pieview_activity:
            Intent i = new Intent(this, PieViewActivity.class);
            startActivity(i);
            break;
        case R.id.canvas_activity:
            Intent i1 = new Intent(this, CanvasActivity.class);
            startActivity(i1);
            break;
        case R.id.leaf_activity:
            Intent i2 = new Intent(this, LeafActivity.class);
            startActivity(i2);
            break;
        }
    }
}
