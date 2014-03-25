package com.example.parkinghelper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
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

	private String alarmHour = "12";
	private String alarmMinute = "30";
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

		TextView text = (TextView) findViewById(R.id.hourTextView);
		text.setText("Hour: " + alarmHour);
		text = (TextView) findViewById(R.id.minuteTextView);
		text.setText("Minute: " + alarmMinute);
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
		readFile();

		SeekBar hourSeekBar = (SeekBar) findViewById(R.id.hourSeekBar);
		SeekBar minuteSeekBar = (SeekBar) findViewById(R.id.minuteSeekBar);

		hourSeekBar.setMax(23);
		hourSeekBar.setProgress(Integer.parseInt(alarmHour));
		minuteSeekBar.setMax(59);
		minuteSeekBar.setProgress(Integer.parseInt(alarmMinute));

		hourSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				TextView text = (TextView) findViewById(R.id.hourTextView);
				text.setText("Hour: " + Integer.toString(progress));
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
				TextView text = (TextView) findViewById(R.id.minuteTextView);
				text.setText("Minute: " + Integer.toString(progress));
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}

		});
	}

	private void readFile() {
		File root = android.os.Environment.getExternalStorageDirectory();

		File dir = new File(root.getAbsolutePath() + "/Pictures/ParkingHelper");
		dir.mkdirs();
		File file = new File(dir, fileName);

		// Read text from file
		BufferedReader br = null;

		try {
			br = new BufferedReader(new FileReader(file));
			String line;

			if ((line = br.readLine()) != null) alarmHour = line;

			if ((line = br.readLine()) != null) alarmMinute = line;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			Log.i(TAG, "******File not found. Did you" + " add a READ_EXTERNAL_STORAGE permission to the manifest?");
			// writeFile();
		} catch (IOException e) {
			// You'll need to add proper error handling here
		}
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

		alarmHour = hourText.getText().toString().replaceAll("Hour: ", "");
		alarmMinute = minuteText.getText().toString().replaceAll("Minute: ", "");

		writeFile();

		this.finish();
	}

	private void writeFile() {
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
	}
}
