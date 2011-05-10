package com.ybi.wallswitcher.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.ybi.wallswitcher.R;

public class WallSwitcherConfigureActivity extends Activity implements
		SharedPreferences.OnSharedPreferenceChangeListener
{

	public static final String SHARED_PREFS_NAME = "WallSwitcherSharedPrefs";
	private static final int PICK_IMAGE = 100;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		//this.getApplicationContext().setSharedPreferencesName(SHARED_PREFS_NAME);
		this.getApplicationContext().getSharedPreferences(SHARED_PREFS_NAME, 0).registerOnSharedPreferenceChangeListener(this);

	}

	public void onAutomatique(View v)
	{
		Intent photoPickerIntent = new Intent(Intent.ACTION_GET_CONTENT);
		photoPickerIntent.setType("image/*");

		this.startActivityForResult(photoPickerIntent, PICK_IMAGE);
	}

	public void onManuel(View v)
	{
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode)
		{
		case PICK_IMAGE: // our request code
			if (resultCode == Activity.RESULT_OK)
			{
				Uri imageUri = data.getData(); // get the image URI

				// met l'url dans les preferences
				SharedPreferences.Editor editor = this.getApplicationContext().getSharedPreferences(SHARED_PREFS_NAME, 0).edit();
				editor.putString("imageUri", imageUri.toString());
				editor.commit();

				Log.d("COM.YBI.WALLSWITCHER", "An Image has been selected " + imageUri.toString());

			}
		}

	}

	@Override
	protected void onDestroy()
	{
		this.getApplicationContext().getSharedPreferences(SHARED_PREFS_NAME, 0).unregisterOnSharedPreferenceChangeListener(this);
		super.onDestroy();
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key)
	{
	}

}