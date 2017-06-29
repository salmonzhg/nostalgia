package com.salmonzhg.nostalgia;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.salmonzhg.nostalgia.core.Nostalgia;

public class Main2Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
    }

    public void send(View view) {
        Nostalgia.post("wer", 12);
    }
}
