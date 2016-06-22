package com.qf.customview;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;

public class PieViewActivity extends Activity {

    private PieView pie;
    private ArrayList<PieData> data = new ArrayList<PieData>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        // setContentView(R.layout.activity_main);
        setContentView(pie);
    }

    public void init() {
        pie = new PieView(this);
        data.add(new PieData("1", 2.3f));
        data.add(new PieData("2", 2.3f));
        data.add(new PieData("3", 2.3f));
        data.add(new PieData("3", 5.4f));
        data.add(new PieData("3", 2.3f));
        pie.setData(data);
    }
}
