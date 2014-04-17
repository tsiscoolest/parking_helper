package com.example.parkinghelper;

import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

public class AlertActivity extends Activity implements GestureDetector.OnGestureListener,
		GestureDetector.OnDoubleTapListener {

	// TS: Initialize private global variables.
	private GestureDetector mDetector;

	Vibrator v;

	/** The view to show the ad. */
	private AdView adView;

	/* Your ad unit id. Replace with your actual ad unit id. */
	private static final String AD_UNIT_ID = "INSERT_YOUR_AD_UNIT_ID_HERE";
	private static final String HASHED_DEVICE_ID = "42583930324B4E345848";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Remove title bar
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		// Remove notification bar
		// this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
		// WindowManager.LayoutParams.FLAG_FULLSCREEN);

		// set content view AFTER ABOVE sequence (to avoid crash)
		setContentView(R.layout.activity_alert);

		// Create an ad.
		adView = new AdView(this);
		adView.setAdSize(AdSize.BANNER);
		adView.setAdUnitId(AD_UNIT_ID);

		// Add the AdView to the view hierarchy. The view will have no size
		// until the ad is loaded.
		RelativeLayout layout = (RelativeLayout) findViewById(R.id.relativeLayout);
		layout.addView(adView);

		// Create an ad request. Check logcat output for the hashed device ID to
		// get test ads on a physical device.
		AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
				.addTestDevice(HASHED_DEVICE_ID).build();

		// Start loading the ad in the background.
		adView.loadAd(adRequest);

		try {
			showPic();
		} catch (IOException e) {
			e.printStackTrace();
		}
		vibrate();

		// TS: Initialize Gesture Detector.
		// Instantiate the gesture detector with the
		// application context and an implementation of
		// GestureDetector.OnGestureListener
		mDetector = new GestureDetector(this, this);
		// Set the gesture detector as the double tap
		// listener.
		mDetector.setOnDoubleTapListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.alert, menu);
		return true;
	}

	@Override
	public void onResume() {
		super.onResume();
		if (adView != null) {
			adView.resume();
		}
	}

	@Override
	public void onPause() {
		if (adView != null) {
			adView.pause();
		}
		super.onPause();
	}

	/** Called before the activity is destroyed. */
	@Override
	public void onDestroy() {
		// Destroy the AdView.
		if (adView != null) {
			adView.destroy();
		}
		super.onDestroy();
	}

	private void vibrate() {
		/*
		 * Intent intent = new Intent(Intent.ACTION_VIEW);
		 * intent.setDataAndType(
		 * Uri.parse(Environment.getExternalStorageDirectory() +
		 * "/Pictures/ParkingHelper/IMG_ParkingHelper.jpg"),"image/*");
		 * startActivity(intent);
		 */

		v = (Vibrator) this.getSystemService(Context.VIBRATOR_SERVICE);

		// This example will cause the phone to vibrate "SOS" in Morse Code
		// In Morse Code, "s" = "dot-dot-dot", "o" = "dash-dash-dash"
		// There are pauses to separate dots/dashes, letters, and words
		// The following numbers represent millisecond lengths
		int dot = 200; // Length of a Morse Code "dot" in milliseconds
		int dash = 500; // Length of a Morse Code "dash" in milliseconds
		int short_gap = 200; // Length of Gap Between dots/dashes
		int medium_gap = 500; // Length of Gap Between Letters
		int long_gap = 1000; // Length of Gap Between Words
		long[] pattern = { 0, // Start immediately
				dot, short_gap, dot, short_gap, dot, // s
				medium_gap, dash, short_gap, dash, short_gap, dash, // o
				medium_gap, dot, short_gap, dot, short_gap, dot, // s
				long_gap };

		// Only perform this pattern one time (-1 means "do not repeat")
		v.vibrate(pattern, 0);
	}

	private void showPic() throws IOException {
		String selectedImagePath;
		ImageView imageView;

		/**
		 * Turn Screen On and Unlock the keypad when this alert dialog is
		 * displayed
		 */
		this.getWindow().addFlags(LayoutParams.FLAG_TURN_SCREEN_ON | LayoutParams.FLAG_DISMISS_KEYGUARD);

		imageView = (ImageView) findViewById(R.id.parkingImageView);

		selectedImagePath = Environment.getExternalStorageDirectory() + "/Pictures/ParkingHelper/IMG_ParkingHelper.jpg";

		ExifInterface exif = new ExifInterface(selectedImagePath);
		int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
		int rotationInDegrees = exifToDegrees(rotation);

		Matrix matrix = new Matrix();
		if (rotation != 0f) {
			matrix.preRotate(rotationInDegrees);
		}

		Bitmap bitmap = BitmapFactory.decodeFile(selectedImagePath);

		Bitmap adjustedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

		imageView.setImageBitmap(adjustedBitmap);
	}

	private static int exifToDegrees(int exifOrientation) {
		if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
			return 90;
		} else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
			return 180;
		} else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
			return 270;
		}
		return 0;
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		this.mDetector.onTouchEvent(event);
		// Be sure to call the superclass implementation
		return super.onTouchEvent(event);
	}

	@Override
	public boolean onDoubleTap(MotionEvent e) {
		return true;
	}

	@Override
	public boolean onDoubleTapEvent(MotionEvent e) {
		return true;
	}

	@Override
	public boolean onSingleTapConfirmed(MotionEvent e) {
		return true;
	}

	@Override
	public boolean onDown(MotionEvent e) {
		v.cancel();
		return true;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		return true;
	}

	@Override
	public void onLongPress(MotionEvent e) {
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		return true;
	}

	@Override
	public void onShowPress(MotionEvent e) {
	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return true;
	}
}
