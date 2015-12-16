package com.silogood.s_permissions;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TabHost;

/**
 * Created by Family on 2015-12-03.
 */
public class Applications extends TabActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.applications_t);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        TabHost mTab = getTabHost();
        TabHost.TabSpec spec;
        Intent intent;

        intent = new Intent(this, Applications_SYSTEM.class);
        spec = mTab.newTabSpec("-_-").setIndicator("USER APP")
                .setContent(intent);
        mTab.addTab(spec);

        intent = new Intent(this, Applications_NO_SYSTEM.class);
        spec = mTab.newTabSpec("-_-").setIndicator("SYSTEM APP")
                .setContent(intent);
        mTab.addTab(spec);
    }    //텝구간 나누는거   유저퍼미션이랑 시스템앱 나눠서 구현
}
