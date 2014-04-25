package com.limtzushian.parkinghelper;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.widget.Toast;

public class MainActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Remove notification bar
		// this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		// WindowManager.LayoutParams.FLAG_FULLSCREEN);

		// set content view AFTER ABOVE sequence (to avoid crash)
		setContentView(R.layout.activity_main);

		if (isExternalStorageWritable()) {
			Intent intent = new Intent(this, CameraActivity.class);
			startActivity(intent);
		} else {
			Toast.makeText(getBaseContext(), "External storage is not available!", Toast.LENGTH_SHORT).show();
		}

		// Kill your current Activity Here:
		this.finish();
		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	/* Checks if external storage is available for read and write */
	public boolean isExternalStorageWritable() {
		String state = Environment.getExternalStorageState();
		if (Environment.MEDIA_MOUNTED.equals(state)) {
			return true;
		}
		return false;
	}
}
