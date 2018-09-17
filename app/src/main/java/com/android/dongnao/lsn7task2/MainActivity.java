package com.android.dongnao.lsn7task2;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends com.lythonliu.LinkAppCompatActivity {
    @Override
    public String getAppName(){
        return BuildConfig.APP_NAME;
    }

    private MySearchView vMySearchView;
    private ViewController mController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        vMySearchView = (MySearchView) findViewById(R.id.mySearchView);

        mController = new ViewController();
        mController.setSearchView(vMySearchView);
    }

    public void open(View view) {
        mController.open();
    }

    public void close(View view) {
        mController.close();
    }
}
