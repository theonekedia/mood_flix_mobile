/**
 * 
 * @author Arshad
 * 
 * @Company Konstant
 *
 */

package hobbyst.ola.foodlove;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.WindowManager;

public class SplashActivity extends Activity {

	private static CountdownTimer timer;
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);

        // Hide both the navigation bar and the status bar.
        if (Build.VERSION.SDK_INT < 16) {
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else if (Build.VERSION.SDK_INT < 19) {
			View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(View.GONE);
		} else {
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }

		timer = new CountdownTimer(3000, 100);
		timer.start();
	}
    
	/**** Class for running the showing loading ****/
	public class CountdownTimer extends CountDownTimer {

        public CountdownTimer(long millisInFuture, long countDownInterval) {
			super(millisInFuture, countDownInterval);
		}

		@Override
		public void onFinish() {
			if (!FoodLoveApplication.myPrefs.getString("user_id", "").equals("")) {
				startActivity(new Intent(SplashActivity.this, MainActivity.class));
				finish();
			} else {
				// For starting the Login Activity
				startActivity(new Intent(SplashActivity.this, LoginActivity.class));
				finish();
			}
		}

		@Override
		public void onTick(long millisUntilFinished) {

		}
	}
}