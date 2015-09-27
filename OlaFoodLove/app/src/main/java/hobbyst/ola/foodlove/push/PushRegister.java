/**
 * 
 * @author Arshad
 * 
 * @Company Konstant
 *
 */

package hobbyst.ola.foodlove.push;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.AsyncTask;
import android.provider.Settings.Secure;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.util.concurrent.atomic.AtomicInteger;

import hobbyst.ola.foodlove.FoodLoveApplication;

/**** For Registering the Device to GCM Server ****/
public class PushRegister {
	public static final String EXTRA_MESSAGE = "message";
    public static final String PROPERTY_REG_ID = "registration_id";
    public static final String PROPERTY_DEVICE_ID = "device_id";
    public static final String PROPERTY_EMAIL_ID = "email_id";
    public static final String PROPERTY_APP_VERSION = "appVersion";
    private static final int MAX_ATTEMPTS = 5;

    /***
     * This is the project number you got from the "Google Cloud Console"
     ***/
    String project_no = "1066141231947";
    
    GoogleCloudMessaging gcm;
    AtomicInteger msgId = new AtomicInteger();
    
    /**
     * Stores the registration ID and the App Version Code in the application's
     * {@code SharedPreferences}.
     *
     * @param context application's context.
     * @param regId registration ID
     */
    private void storeRegistrationId(Context context, String regId) {
        final SharedPreferences prefs = getGcmPreferences(context);
        int appVersion = getAppVersion(context);
        SharedPreferences.Editor editor = prefs.edit();
        
        // Unique Device ID
        String deviceID = Secure.getString(context.getContentResolver(), Secure.ANDROID_ID);
        editor.putString(PROPERTY_REG_ID, regId);
        editor.putString(PROPERTY_DEVICE_ID, deviceID);
        editor.putString(PROPERTY_EMAIL_ID, getAccount(context));
        editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
        
        registerServerInBackground(context);
    }
    
    /**** This snippet takes the simple approach of using the first returned Google account,
     **** but you can pick any Google account on the device. 
     ****/
	private String getAccount(Context con) {
    Account[] accounts = AccountManager.get(con).getAccountsByType("com.google");
    if (accounts.length == 0) {
        return null; 
    }
    Log.d("My Email id  = ", accounts[0].name);
    return accounts[0].name;
} 

    /**
     * Gets the current registration ID for application on GCM service, if there is one.
     * <p>
     * If result is empty, the app needs to register.
     *
     * @return registration ID, or empty string if there is no existing
     *         registration ID.
     */
	public String getRegistrationId(Context context) {
        final SharedPreferences prefs = getGcmPreferences(context);
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.length() == 0)
            return "";
        
        // Check if App was updated; if so, it must clear the registration ID since
        // the existing regID is not guaranteed to work with the new App version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion)
            return "";
        
        return registrationId;
    }

	/**
     * Gets the current device ID for application on GCM service, if there is one.
     * <p>
     * If result is empty, the app needs to register.
     *
     * @return device ID, or empty string if there is no existing
     *         device ID.
     */
	public String getDeviceId(Context context) {
        final SharedPreferences prefs = getGcmPreferences(context);
        String deviceId = prefs.getString(PROPERTY_DEVICE_ID, "");
        if (deviceId.length() == 0)
            return "";
        
        // Check if App was updated; if so, it must clear the device ID since
        // the existing deviceID is not guaranteed to work with the new App version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion)
            return "";
        
        return deviceId;
    }
	
	/**
     * Gets the current gmail ID for application on GCM service, if there is one.
     * <p>
     * If result is empty, the app needs to register.
     *
     * @return gmail ID, or empty string if there is no existing
     *         gmail ID.
     */
	public String getGmailId(Context context) {
        final SharedPreferences prefs = getGcmPreferences(context);
        String gmailId = prefs.getString(PROPERTY_EMAIL_ID, "");
        if (gmailId.length() == 0)
            return "";
        
        // Check if App was updated; if so, it must clear the gmail ID since
        // the existing gmailID is not guaranteed to work with the new App version.
        int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION, Integer.MIN_VALUE);
        int currentVersion = getAppVersion(context);
        if (registeredVersion != currentVersion)
            return "";
        
        return gmailId;
    }
	
    /**
     * Registers the application with GCM servers asynchronously.
     * <p>
     * Stores the registration ID and the App Version Code in the application's
     * shared preferences.
     */
    public void registerInBackground(final Context context) {
    	new AsyncTask<Void, String, String>() {
            @Override
            protected String doInBackground(Void... params) {
            	String msg = "";
				for (int i = 1; i <= MAX_ATTEMPTS; i++) {
					try {
						if (gcm == null)
							gcm = GoogleCloudMessaging.getInstance(context);

						String regid = gcm.register(project_no);
						Log.d("Push reg_id:", regid);

						// Saving the Registration ID to Local Shared Preferences for future use
						storeRegistrationId(context, regid);
						break;
					} catch (Exception ex) {
						msg = "Error :" + ex.getMessage();
						// If there is an error, don't just keep trying to register.
						// Setting a MAX no. of attempts to perform. 
						if (i == MAX_ATTEMPTS) {
							break;
						}
					}
				}
				return msg;
			}

            @Override
            protected void onPostExecute(String message) {
            }
        }.execute(null, null, null);
    }
    
    /**
     * Registers the application with servers asynchronously.
     * <p>
     * Stores the registration ID and the App Version Code in the application's
     * shared preferences.
     */
    public void registerServerInBackground(final Context context) {
    	new AsyncTask<Void, String, String>() {
            @Override
            protected String doInBackground(Void... params) {
            	String msg = "";
				for (int i = 1; i <= MAX_ATTEMPTS; i++) {
					try {
						FoodLoveApplication.register_push(getRegistrationId(context),
                                getDeviceId(context), getGmailId(context));
						break;
					} catch (Exception ex) {
						msg = "Error :" + ex.getMessage();
						
						final SharedPreferences prefs = getGcmPreferences(context);
		                SharedPreferences.Editor editor = prefs.edit();
		                editor.putBoolean("register_server", false);
		                editor.commit();
		                
						// If there is an error, don't just keep trying to register.
						// Setting a MAX no. of attempts to perform. 
						if (i == MAX_ATTEMPTS) {
							break;
						}
					}
				}
				return msg;
			}

            @Override
            protected void onPostExecute(String message) {
            }
        }.execute(null, null, null);
    }

    /**
     * @return Application's version code from the {@code PackageManager}.
     */
    private static int getAppVersion(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return packageInfo.versionCode;
        } catch (NameNotFoundException e) {
            // should never happen
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    /**
     * @return Application's {@code SharedPreferences}.
     */
    public static SharedPreferences getGcmPreferences(Context context) {
        // This sample App persists the registration ID in shared preferences,
        // but how you store the regID in your App is up to you.
        return context.getSharedPreferences("GCM_Pref", Context.MODE_PRIVATE);
    }
}