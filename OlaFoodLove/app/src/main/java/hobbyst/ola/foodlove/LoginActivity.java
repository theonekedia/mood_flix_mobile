package hobbyst.ola.foodlove;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.mingle.widget.LoadingView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import hobbyst.ola.foodlove.libs.CustomButton;
import hobbyst.ola.foodlove.libs.CustomEditText;
import hobbyst.ola.foodlove.libs.CustomTextView;

/**
 * Created by Arshad Parwez on 05/08/15.
 */

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        View.OnClickListener {

    // Facebook Callback Manager
    public static CallbackManager callbackManager;

    // Google Plus Client
    /* Request code used to invoke sign in user interactions. */
    private static final int RC_SIGN_IN = 0;

    /* Client used to interact with Google APIs. */
    public static GoogleApiClient mGoogleApiClient;

    /* Is there a ConnectionResult resolution in progress? */
    public static boolean mIsResolving = false;

    /* Should we automatically resolve ConnectionResults when possible? */
    public static boolean mShouldResolve = false;

    public static String TAG = "Login Activity";

    //Defining Variables
    public static Typeface RLight, RMedium, RRegular;

    private SignInButton googleSign;
    private static RelativeLayout rl_loadingView;
    private static LoadingView loadingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        if (Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.Primary700));
        }

        RLight = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf");
        RMedium = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Medium.ttf");
        RRegular = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Regular.ttf");

        callbackManager = CallbackManager.Factory.create();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(new Scope(Scopes.PLUS_LOGIN))
                .addScope(new Scope(Scopes.PLUS_ME))
                .addScope(new Scope(Scopes.PROFILE))
                .build();

        LoginButton loginButton = (LoginButton) findViewById(R.id.btn_fb);
        loginButton.setReadPermissions(Arrays.asList("public_profile", "email"));

        // Callback registration
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                final AccessToken accToken = loginResult.getAccessToken();
                GraphRequest request = GraphRequest.newMeRequest(accToken,
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                // Application code
                                JSONObject result_obj = response.getJSONObject();
                                Log.v("result_obj = ", result_obj.toString());
                                try {
                                    String email = result_obj.getString("email");
                                    String fName = result_obj.getString("first_name");
                                    String lName = result_obj.getString("last_name");
                                    String uid = result_obj.getString("id");
                                    String personPhoto = "https://graph.facebook.com/" + result_obj.getString("id") + "/picture?type=normal";
                                    signUpTask(email, fName, lName, "facebook", uid, accToken.getToken(), accToken.getExpires().toString(), personPhoto);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id, name, first_name, last_name, email, gender");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                // App code
                Log.d("FacebookCallback", "onCancel");
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                Log.d("FacebookCallback", "onError = " + exception);
            }
        });

        googleSign = (SignInButton) findViewById(R.id.btn_gplus);
        googleSign.setOnClickListener(this);

        rl_loadingView = (RelativeLayout) findViewById(R.id.rl_loadingView);
        loadingView = (LoadingView) findViewById(R.id.loadingView);
    }

    @Override
    public void onClick(View v) {
        // For Hiding the Keyboard
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

        if(v.equals(googleSign)) {
            if (!mGoogleApiClient.isConnected())
                onSignInClicked();
            else
                onSignOutClicked();
        }
    }

    // For calling common SignUp API
    private void signUpTask(final String email, final String fname, final String lname, final String provider, final String uid, final String token, final String token_expiry, final String profile_image) {
        loadingView.setLoadingText(getString(R.string.signing_up));
        rl_loadingView.setVisibility(View.VISIBLE);

        StringRequest sr = new StringRequest(Request.Method.POST, FoodLoveApplication.SERVER_URL + "user/login?", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, response.toString());
                try {
                    JSONObject response_obj = new JSONObject(response.toString());
                    if(response_obj.getString("status").equals("OK")) {
                        if (response_obj.has("result")) {
                            JSONObject result_obj = response_obj.getJSONObject("result");
                            // Loading Shared Preferences in Editable Mode
                            SharedPreferences.Editor editor = FoodLoveApplication.myPrefs.edit();
                            // Saving New Data
                            editor.putString("user_id", result_obj.getString("id"));
                            editor.putString("first_name", result_obj.getString("first_name"));
                            editor.putString("last_name", result_obj.getString("last_name"));
                            editor.putString("email", result_obj.getString("email"));
                            editor.putString("profile_image", profile_image);
                            editor.putString("login_by", provider);
                            // Finally Saving all changes to Shared Preferences
                            editor.commit();

                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            LoginActivity.this.finish();
                        } else
                            Toast.makeText(LoginActivity.this, R.string.invalid_credentials, Toast.LENGTH_SHORT).show();
                    } else
                        Toast.makeText(LoginActivity.this, response_obj.getString("message"), Toast.LENGTH_SHORT).show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                rl_loadingView.setVisibility(View.GONE);
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // for no internet error
                        if(error instanceof NoConnectionError)
                            Toast.makeText(LoginActivity.this, R.string.network_error, Toast.LENGTH_SHORT).show();
                        else // for server error
                            Toast.makeText(LoginActivity.this, R.string.server_error, Toast.LENGTH_SHORT).show();

                        rl_loadingView.setVisibility(View.GONE);
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("key", FoodLoveApplication.SERVER_KEY);
                JSONObject user_obj = new JSONObject();
                try {
                    user_obj.put("email", email);
                    user_obj.put("first_name", fname);
                    user_obj.put("last_name", lname);
                    if(!provider.equals("")) {
                        user_obj.put("provider", provider);
                        user_obj.put("uid", uid);
                        user_obj.put("token", token);
                        user_obj.put("token_expiry_at", token_expiry);
                        user_obj.put("profile_img", profile_image);
                    }
                    params.put("user", user_obj.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return params;
            }
        };

        // Adding request to request queue
        FoodLoveApplication.getInstance().addToRequestQueue(sr, "signup");
    }

    private void onSignInClicked() {
        // User clicked the sign-in button, so begin the sign-in process and automatically
        // attempt to resolve any errors that occur.
        mShouldResolve = true;
        mGoogleApiClient.connect();
    }

    private void onSignOutClicked() {
        // Clear the default account so that GoogleApiClient will not automatically
        // connect in the future.
        if (mGoogleApiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
            mGoogleApiClient.disconnect();
        }
    }

    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppEventsLogger.activateApp(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Logs 'app deactivate' App Event.
        AppEventsLogger.deactivateApp(this);
    }

    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (!mIsResolving && mShouldResolve) {
            if (connectionResult.hasResolution()) {
                try {
                    connectionResult.startResolutionForResult(this, RC_SIGN_IN);
                    mIsResolving = true;
                } catch (IntentSender.SendIntentException e) {
                    Log.e(TAG, "Could not resolve ConnectionResult.", e);
                    mIsResolving = false;
                    mGoogleApiClient.connect();
                }
            } else {
                // Could not resolve the connection result, show the user an
                // error dialog.
                //showErrorDialog(connectionResult);
            }
        } else {
            // Show the signed-out UI
            //showSignedOutUI();
        }
    }

    public void onConnected(Bundle bundle) {
        // We've resolved any connection errors.  mGoogleApiClient can be used to
        // access Google APIs on behalf of the user.
        Log.d(TAG, "onConnected:" + bundle);
        mShouldResolve = false;

        if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
            Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
            String email = Plus.AccountApi.getAccountName(mGoogleApiClient);
            String fName = currentPerson.getName().getGivenName();
            String lName = currentPerson.getName().getFamilyName();
            String uid = currentPerson.getId();

            String personPhoto = "";
            if(currentPerson.getImage().getUrl().endsWith("sz=50"))
                personPhoto = currentPerson.getImage().getUrl().substring(0, currentPerson.getImage().getUrl().length()-2) + "150";

            Log.d("currentPerson", currentPerson.toString());
            Log.d("personPhoto ", personPhoto);

            signUpTask(email, fName, lName, "google", uid, "", "", personPhoto);
        }
    }

    public void onConnectionSuspended(int cause) {
        mGoogleApiClient.connect();
    }

    protected void onStop() {
        super.onStop();

        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            // If the error resolution was not successful we should not resolve further.
            if (resultCode != RESULT_OK) {
                mShouldResolve = false;
            }

            mIsResolving = false;
            mGoogleApiClient.connect();
        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }
}