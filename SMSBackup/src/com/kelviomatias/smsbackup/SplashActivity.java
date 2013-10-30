package com.kelviomatias.smsbackup;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.Window;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash);
        
        new Thread(new Runnable() {
        	
        	public void run() {
        		try {
        			Thread.sleep(500);
        			Intent i = new Intent();
        			i.setClass(SplashActivity.this, MainActivity.class);
        			startActivity(i);
        			finish();
        		} catch (Exception e) {
        			
        		}
        	}
        	
        }).start();
        
        
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.splash, menu);
        return true;
    }
    
}
