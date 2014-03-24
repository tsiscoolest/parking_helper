package com.example.parkinghelper;

import java.io.File;
import java.util.Calendar;
import java.util.GregorianCalendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.AlarmClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.Window;
import android.widget.Toast;

public class MainActivity extends Activity {

	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
	private Uri fileUri;
	private static final int MEDIA_TYPE_IMAGE = 1;
	private int defaultAlarmHour = 2;
	private int defaultAlarmMinute = 40;
	private int alarmHour;
	private int alarmMinute;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Remove title bar
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);

		// Remove notification bar
		// this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		// WindowManager.LayoutParams.FLAG_FULLSCREEN);

		// set content view AFTER ABOVE sequence (to avoid crash)
		this.setContentView(R.layout.activity_main);

		if (!isExternalStorageWritable()) {
			Toast.makeText(getBaseContext(), "External storage is not available!", Toast.LENGTH_SHORT).show();
			this.finish();
		}

		startCamera();
		// setAlarm();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
			if (resultCode == RESULT_OK) {
				// Image captured and saved to fileUri specified in the Intent
				Toast.makeText(this, "Image saved!", Toast.LENGTH_LONG).show();
				readSharedPreferences();
				setAlarm();
				setBuiltInAlarm();
			} else if (resultCode == RESULT_CANCELED) {
				// User cancelled the image capture
			} else {
				// Image capture failed, advise user
			}
		}

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
		gregCalendar.add(Calendar.HOUR_OF_DAY, alarmHour);
		gregCalendar.add(Calendar.MINUTE, alarmMinute);
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
		openNewAlarm.putExtra(AlarmClock.EXTRA_HOUR, calendar.get(Calendar.HOUR_OF_DAY) + alarmHour);
		openNewAlarm.putExtra(AlarmClock.EXTRA_MINUTES, calendar.get(Calendar.MINUTE) + alarmMinute);
		openNewAlarm.putExtra(AlarmClock.EXTRA_SKIP_UI, true);
		startActivity(openNewAlarm);
	}

	private void readSharedPreferences() {
		SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);

		alarmHour = sharedPref.getInt(getString(R.string.alarmHour), defaultAlarmHour);
		alarmMinute = sharedPref.getInt(getString(R.string.alarmMinute), defaultAlarmMinute);

		Toast.makeText(getBaseContext(), "Successfully read new alarm time: " + alarmHour + " " + alarmMinute,
				Toast.LENGTH_SHORT).show();
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
