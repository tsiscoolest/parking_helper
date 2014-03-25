package com.example.parkinghelper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class SettingActivity extends Activity {

	private String alarmHour = "";
	private String alarmMinute = "";
	private String fileName = "AlarmSetting.txt";
	private static final String TAG = "MEDIA";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);

		if (!isExternalStorageWritable()) {
			Toast.makeText(getBaseContext(), "External storage is not available!", Toast.LENGTH_SHORT).show();
			this.finish();
		}

		seekBar();
		buttonOnClick();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.setting, menu);
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

	public void seekBar() {
		SeekBar hourSeekBar = (SeekBar) findViewById(R.id.hourSeekBar);
		SeekBar minuteSeekBar = (SeekBar) findViewById(R.id.minuteSeekBar);

		hourSeekBar.setMax(23);
		hourSeekBar.setProgress(12);
		minuteSeekBar.setMax(59);
		minuteSeekBar.setProgress(30);

		hourSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				// progress = progress + 10; // Add the minimum value (10)
				TextView text = (TextView) findViewById(R.id.hourTextView);
				text.setText("Hour: " + Integer.toString(progress));
				// maxPrice = progress;
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}

		});

		minuteSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				// progress = progress + 10; // Add the minimum value (10)
				TextView text = (TextView) findViewById(R.id.minuteTextView);
				text.setText("Minute: " + Integer.toString(progress));
				// maxPrice = progress;
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}

		});
	}

	private void buttonOnClick() {
		Button btn = (Button) findViewById(R.id.setButton);

		btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				writeToSDFile(v);
			}
		});
	}

	/**
	 * Method to write ascii text characters to file on SD card. Note that you
	 * must add a WRITE_EXTERNAL_STORAGE permission to the manifest file or this
	 * method will throw a FileNotFound Exception because you won't have write
	 * permission.
	 */

	public void writeToSDFile(View v) {

		// Find the root of the external storage.
		// See http://developer.android.com/guide/topics/data/data-
		// storage.html#filesExternal\

		TextView hourText = (TextView) findViewById(R.id.hourTextView);
		TextView minuteText = (TextView) findViewById(R.id.minuteTextView);

		alarmHour = hourText.getText().toString();
		alarmMinute = minuteText.getText().toString();

		File root = android.os.Environment.getExternalStorageDirectory();

		File dir = new File(root.getAbsolutePath() + "/Pictures/ParkingHelper");
		dir.mkdirs();
		File file = new File(dir, fileName);

		try {
			FileOutputStream f = new FileOutputStream(file);
			PrintWriter pw = new PrintWriter(f);
			pw.println(alarmHour);
			pw.println(alarmMinute);
			pw.flush();
			pw.close();
			f.close();
			Toast.makeText(getBaseContext(), "New alarm setting successfully saved!", Toast.LENGTH_SHORT).show();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			Log.i(TAG, "******File not found. Did you" + " add a WRITE_EXTERNAL_STORAGE permission to the manifest?");
		} catch (IOException e) {
			e.printStackTrace();
		}

		this.finish();
	}
}
