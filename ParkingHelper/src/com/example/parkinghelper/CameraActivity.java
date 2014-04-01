package com.example.parkinghelper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.AlarmClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

public class CameraActivity extends Activity {

	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
	private Uri fileUri;
	private static final int MEDIA_TYPE_IMAGE = 1;
	private String alarmHour = "12";
	private String alarmMinute = "30";
	private String fileName = "AlarmSetting.txt";
	private static final String TAG = "MEDIA";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_camera);

		readFile();
		startCamera();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.camera, menu);
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
			if (resultCode == RESULT_OK) {
				// Image captured and saved to fileUri specified in the Intent
				Toast.makeText(this, "Image saved!", Toast.LENGTH_LONG).show();
				setAlarm();
				setBuiltInAlarm();
			} else if (resultCode == RESULT_CANCELED) {
				// User cancelled the image capture
			} else {
				// Image capture failed, advise user
			}
		}

		// Kill your current Activity Here:
		super.onDestroy();
		this.finish();
	}

	/** Create a file Uri for saving an image or video */
	private static Uri getOutputMediaFileUri(int type) {
		return Uri.fromFile(getOutputMediaFile(type));
	}

	/** Create a File for saving an image or video */
	private static File getOutputMediaFile(int type) {
		// To be safe, you should check that the SDCard is mounted
		// using Environment.getExternalStorageState() before doing this.

		File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
				"ParkingHelper");
		// This location works best if you want the created images to be shared
		// between applications and persist after your app has been uninstalled.

		// Create the storage directory if it does not exist
		if (!mediaStorageDir.exists()) {
			if (!mediaStorageDir.mkdirs()) {
				Log.d("ParkingHelper", "failed to create directory");
				return null;
			}
		}

		// Create a media file name
		// String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new
		// Date());
		File mediaFile;
		if (type == MEDIA_TYPE_IMAGE) {
			mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_ParkingHelper" + ".jpg");
			/*
			 * } else if(type == MEDIA_TYPE_VIDEO) { mediaFile = new
			 * File(mediaStorageDir.getPath() + File.separator + "VID_"+
			 * timeStamp + ".mp4");
			 */
		} else {
			return null;
		}

		return mediaFile;
	}

	private void startCamera() {
		Toast.makeText(this, "Starting camera", Toast.LENGTH_LONG).show();

		// create Intent to take a picture and return control to the calling
		// application
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE); // create a file to
														   // save the image
		intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file
														   // name

		// start the image capture Intent
		startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
	}

	private void setAlarm() {

		/**
		 * This intent invokes the activity DemoActivity, which in turn opens
		 * the AlertDialog window
		 */
		Intent i = new Intent("android.intent.action.AlertAlarm");

		/** Creating a Pending Intent */
		PendingIntent operation = PendingIntent.getActivity(getBaseContext(), 0, i, Intent.FLAG_ACTIVITY_NEW_TASK);

		/** Getting a reference to the System Service ALARM_SERVICE */
		AlarmManager alarmManager = (AlarmManager) getBaseContext().getSystemService(ALARM_SERVICE);

		/**
		 * Creating a calendar object corresponding to the date and time set by
		 * the user
		 */
		GregorianCalendar gregCalendar = new GregorianCalendar();
		gregCalendar.add(Calendar.HOUR_OF_DAY, Integer.parseInt(alarmHour));
		gregCalendar.add(Calendar.MINUTE, Integer.parseInt(alarmMinute));
		// gregCalendar.add(Calendar.SECOND, 5);

		/** Converting the date and time in to milliseconds elapsed since epoch */
		long alarm_time = gregCalendar.getTimeInMillis();

		/** Setting an alarm, which invokes the operation at alart_time */
		alarmManager.set(AlarmManager.RTC_WAKEUP, alarm_time, operation);

		/** Alert is set successfully */
		Toast.makeText(getBaseContext(), "Alarm is set successfully", Toast.LENGTH_SHORT).show();
	}

	private void setBuiltInAlarm() {
		Calendar calendar = Calendar.getInstance();
		Intent openNewAlarm = new Intent(AlarmClock.ACTION_SET_ALARM);
		openNewAlarm.putExtra(AlarmClock.EXTRA_MESSAGE, "ParkingHelper");
		openNewAlarm.putExtra(AlarmClock.EXTRA_RINGTONE, AlarmClock.VALUE_RINGTONE_SILENT);
		openNewAlarm.putExtra(AlarmClock.EXTRA_VIBRATE, true);
		openNewAlarm.putExtra(AlarmClock.EXTRA_HOUR, calendar.get(Calendar.HOUR_OF_DAY) + Integer.parseInt(alarmHour));
		openNewAlarm.putExtra(AlarmClock.EXTRA_MINUTES, calendar.get(Calendar.MINUTE) + Integer.parseInt(alarmMinute));
		openNewAlarm.putExtra(AlarmClock.EXTRA_SKIP_UI, true);
		startActivity(openNewAlarm);
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
			Intent intent = new Intent(this, SettingActivity.class);
			startActivity(intent);
			readFile();
		} catch (IOException e) {
			// You'll need to add proper error handling here
		}
	}
}
