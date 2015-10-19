package com.example.yangbryan.dotaheros;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;

/**
 * Created by yangbryan on 15/10/19.
 */
public class Equip extends Activity {

    @Override
    public void onCreate( Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.equip_detail);

        WebView wView = (WebView)findViewById(R.id.wv1);
        WebSettings wSet = wView.getSettings();
        wSet.setJavaScriptEnabled(true);

        wView.loadUrl("file:///android_asset/equips.html");
    }

}
