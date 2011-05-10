package com.ybi.wallswitcher.wallpaper;

import java.io.FileNotFoundException;
import java.io.IOException;

import com.ybi.wallswitcher.activity.WallSwitcherConfigureActivity;

import android.content.ContentResolver;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.MediaStore.Images.Media;
import android.service.wallpaper.WallpaperService;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;

public class WallSwitcherWallpaper extends WallpaperService
{

	private final Handler mHandler = new Handler();

	@Override
	public void onCreate()
	{
		super.onCreate();
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
	}

	@Override
	public Engine onCreateEngine()
	{
		return new CubeEngine(this.getContentResolver());
	}

	class CubeEngine extends Engine implements
			SharedPreferences.OnSharedPreferenceChangeListener
	{

		private final Paint mPaint = new Paint();
		private long mStartTime;
		private boolean mVisible;
		private SharedPreferences sharedPreferences;
		private ContentResolver contentResolver;
		private Bitmap bgi;

		private final Runnable mDrawCube = new Runnable()
		{
			public void run()
			{
				drawFrame(0);
			}
		};

		CubeEngine(ContentResolver cr)
		{
			// Create a Paint to draw the lines for our cube
			final Paint paint = mPaint;
			paint.setColor(0xffffffff);
			paint.setAntiAlias(true);
			paint.setStrokeWidth(2);
			paint.setStrokeCap(Paint.Cap.ROUND);
			paint.setStyle(Paint.Style.STROKE);

			mStartTime = SystemClock.elapsedRealtime();

			sharedPreferences = WallSwitcherWallpaper.this.getSharedPreferences(WallSwitcherConfigureActivity.SHARED_PREFS_NAME, 0);
			sharedPreferences.registerOnSharedPreferenceChangeListener(this);
			this.contentResolver = cr;
			onSharedPreferenceChanged(sharedPreferences, null);

		}

		@Override
		public void onCreate(SurfaceHolder surfaceHolder)
		{
			super.onCreate(surfaceHolder);
		}

		@Override
		public void onDestroy()
		{
			super.onDestroy();
			mHandler.removeCallbacks(mDrawCube);
		}

		@Override
		public void onVisibilityChanged(boolean visible)
		{
			mVisible = visible;
			if (visible)
			{
				drawFrame(0);
			} else
			{
				mHandler.removeCallbacks(mDrawCube);
			}
		}

		@Override
		public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height)
		{
			super.onSurfaceChanged(holder, format, width, height);
			// store the center of the surface, so we can draw the cube in the
			// right spot
			// drawFrame();
		}

		@Override
		public void onSurfaceCreated(SurfaceHolder holder)
		{
			super.onSurfaceCreated(holder);
		}

		@Override
		public void onSurfaceDestroyed(SurfaceHolder holder)
		{
			super.onSurfaceDestroyed(holder);
			mVisible = false;
			mHandler.removeCallbacks(mDrawCube);
		}

		@Override
		public void onOffsetsChanged(float xOffset, float yOffset, float xStep, float yStep, int xPixels, int yPixels)
		{
			drawFrame(xPixels);
		}

		/*
		 * Draw one frame of the animation. This method gets called repeatedly
		 * by posting a delayed Runnable. You can do any drawing you want in
		 * here. This example draws a wireframe cube.
		 */
		void drawFrame(int offset)
		{
			final SurfaceHolder holder = getSurfaceHolder();

			Canvas c = null;
			try
			{
				c = holder.lockCanvas();
				if (c != null)
				{
					// draw something
					drawWallPaper(c, offset);
				}
			} finally
			{
				if (c != null)
					holder.unlockCanvasAndPost(c);
			}

			// Reschedule the next redraw
			mHandler.removeCallbacks(mDrawCube);
			if (mVisible)
			{
				// mHandler.postDelayed(mDrawCube, 30000);
			}
		}

		private void drawWallPaper(Canvas c, int offset)
		{
			final Paint paint = mPaint;
			paint.setColor(0xffffffff);
			paint.setAntiAlias(true);
			paint.setStrokeWidth(2);
			paint.setStrokeCap(Paint.Cap.ROUND);
			paint.setStyle(Paint.Style.STROKE);

			Log.d("COM.YBI.WALLSWITCHER", "displaying bitmap " + offset);
			// Matrix matrix = new Matrix();
			// Camera camera = new Camera();

			// camera.save();
			// camera.rotateY(offset/30.0f);
			// camera.rotateY(ASingletonCanvas.getInstance().getAngle().getRoty()
			// );
			// camera.translate(-240.0f, 0.0f,0.0f);
			// camera.getMatrix(matrix);
			// camera.restore();

			// matrix.preTranslate(0.0f, 0.0f);

			// Bitmap tbp = Bitmap.createBitmap(bgi, 0, 0, 480, 800, matrix,
			// true);
			// m.preTranslate(-centerX, -centerY);
			// m.postTranslate(centerX, centerY);
			// canvas.concat(matrix);

			c.drawBitmap(bgi, offset, 0, null);

		}

		@Override
		public void onSharedPreferenceChanged(SharedPreferences prefs, String key)
		{
			String imageUri = prefs.getString("imageUri", null);
			try
			{
				if (imageUri != null)
				{
					Log.d("COM.YBI.WALLSWITCHER", "loading bitmap" + imageUri);
					bgi = Media.getBitmap(contentResolver, Uri.parse(imageUri));
					bgi.setDensity(DisplayMetrics.DENSITY_HIGH);
				}
			} catch (FileNotFoundException e)
			{
				Log.e("COM.YBI.WALLSWITCHER", "filenotfound exception "
						+ sharedPreferences.getString("imageUri", null), e);
			} catch (IOException e)
			{
				Log.e("COM.YBI.WALLSWITCHER", "ioexception exception "
						+ sharedPreferences.getString("imageUri", null), e);
			}
		}

	}
}