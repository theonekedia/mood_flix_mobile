package hobbyst.ola.foodlove;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.FacebookSdk;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import hobbyst.ola.foodlove.push.PushRegister;

/**
 * Created by Arshad Parwez on 05/08/15.
 */

public class FoodLoveApplication extends Application {

    public static final String TAG = FoodLoveApplication.class.getSimpleName();

    private RequestQueue mRequestQueue;

    private static FoodLoveApplication mInstance;

    public static final String SERVER_URL = "http://10.20.241.44:3000/api/v0/mobile/";
    public static final String SANDBOX_SERVER_URL = "http://sandbox-t.olacabs.com/v1/";
    //public static final String SERVER_URL = "http://192.168.0.4:3000/api/v0/mobile/";
    public static final String SERVER_KEY = "mood_flix";
    public static final String SERVER_TOKEN = "3d5eb3f7045e4e87b9791a340071121b";
    public static final String SERVER_AUTHORIZATION = "55a506e7de294a5395f7795a45c87e58";

    // Asynctask
    public static AsyncTask<Void, Void, Void> mRegisterTask;

    /* Variables for Push Notifications */
    GoogleCloudMessaging gcm;
    private String regid;

    public static SharedPreferences myPrefs;

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;
        FacebookSdk.sdkInitialize(mInstance);

        myPrefs = getSharedPreferences("Ola_Prefs", MODE_PRIVATE);

        // Initializing the Push Service
        PushRegister push = new PushRegister();

        // Check device for Play Services APK. If check succeeds, proceed with GCM registration.
        if (checkPlayServices()) {
            gcm = GoogleCloudMessaging.getInstance(this);
            regid = push.getRegistrationId(this);
            Log.d("Push Reg id = ", regid);
            SharedPreferences gcm_prefs = push.getGcmPreferences(this);

            if (regid.length() == 0)
                push.registerInBackground(this);
            else if (gcm_prefs.getBoolean("register_server", false) == false)
                push.registerServerInBackground(this);
        }
    }

    /**
     * Check the device to make sure it has the Google Play Services APK.
     * If it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     **/
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS)
            return false;
        return true;
    }

    public static synchronized FoodLoveApplication getInstance() {
        return mInstance;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        }

        return mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req, String tag) {
        // set the default tag if tag is empty
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToRequestQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests(Object tag) {
        if (mRequestQueue != null) {
            mRequestQueue.cancelAll(tag);
        }
    }

    /**** For registering device id to the server ****/
    public static void register_push(final String reg_key, final String device_id, final String email_id) {

        StringRequest sr = new StringRequest(Request.Method.POST, FoodLoveApplication.SERVER_URL + "register?", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("Register PUSH", response.toString());
                try {
                    JSONObject response_obj = new JSONObject(response.toString());
                    if(response_obj.getString("status").equals("OK")) {
                        if (response_obj.has("result")) {
                            JSONObject result_obj = response_obj.getJSONObject("result");
                            final SharedPreferences prefs = PushRegister.getGcmPreferences(mInstance);
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putBoolean("register_server", true);
                            editor.putString("reg_id", result_obj.getString("reg_id"));
                            editor.commit();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // for no internet error
                        if(error instanceof NoConnectionError)
                            Toast.makeText(mInstance, R.string.network_error, Toast.LENGTH_SHORT).show();
                        else // for server error
                            Toast.makeText(mInstance, R.string.server_error, Toast.LENGTH_SHORT).show();
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("key", FoodLoveApplication.SERVER_KEY);
                params.put("registration_key", reg_key);
                params.put("device_id", device_id);
                params.put("email", email_id);
                if(!FoodLoveApplication.myPrefs.getString("user_id", "0").equals("0"))
                    params.put("user_id", FoodLoveApplication.myPrefs.getString("user_id", "0"));

                return params;
            }
        };

        // Adding request to request queue
        FoodLoveApplication.getInstance().addToRequestQueue(sr, "register");
    }
}