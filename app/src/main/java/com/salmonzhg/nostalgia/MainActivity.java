package com.salmonzhg.nostalgia;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.salmonzhg.nostalgia.core.Nostalgia;
import com.salmonzhg.nostalgia.core.Unbinder;
import com.salmonzhg.nostalgia.core.annotation.LifecycleFilter;
import com.salmonzhg.nostalgia.core.annotation.Receive;
import com.salmonzhg.nostalgia.core.annotation.Scheduler;
import com.salmonzhg.nostalgia.core.annotation.Take;
import com.salmonzhg.nostalgia.core.lifecycleadapter.ActivityLifecycle;

public class MainActivity extends AppCompatActivity {

    Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        unbinder = Nostalgia.bind(this);

    }

    @Receive(tag = "tag" , scheduler = Scheduler.MAINTHREAD)
    public void onReceived(String contentStr) {
        Log.d("asd", "onReceived: " + contentStr);
    }

    @Receive(tag = "wer")
    public void onEmptyParam() {
        Log.d("asd", "onEmptyParam: ");
    }


    @Receive(tag = "wer")
    public void onBaseTypeParam(int i) {
        Log.d("asd", "onBaseTypeParam: " + i);
    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbinder.unBind();
    }

    public void second(View view) {
        startActivity(new Intent(this, Main2Activity.class));
    }

    public void send(View view) {
        Nostalgia.post("wer", "a content");
    }
}
