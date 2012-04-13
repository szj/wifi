package com.redinput.wifi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class Splash extends Activity {
	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.splash);

		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(2000);

					Intent i = new Intent(Splash.this, Main.class);

					Splash.this.finish();
					Splash.this.startActivity(i);

				} catch (InterruptedException e) {
					Log.e("IP切换异常!", e.getMessage());
				}

			}
		}).start();
	}

}
