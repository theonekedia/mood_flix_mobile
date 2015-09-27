package hobbyst.ola.foodlove;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.mingle.widget.LoadingView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import hobbyst.ola.foodlove.libs.CustomButton;

/**
 * Created by Arshad Parwez on 15/08/15.
 */
public class SelectSourceFragment extends BaseFragment implements View.OnClickListener {

    private static View rootView;
    private CustomButton btn_create, btn_search;
    private static RelativeLayout rl_loadingView;
    private static LoadingView loadingView;

    /**** Returns a new instance of this fragment for the given section number. ****/
    public static SelectSourceFragment newInstance(int sectionNumber) {
        SelectSourceFragment fragment = new SelectSourceFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.select_source, container, false);

        btn_create = (CustomButton) rootView.findViewById(R.id.btn_create);
        btn_create.setOnClickListener(this);

        btn_search = (CustomButton) rootView.findViewById(R.id.btn_search);
        btn_search.setOnClickListener(this);

        rl_loadingView = (RelativeLayout) rootView.findViewById(R.id.rl_loadingView);
        loadingView = (LoadingView) rootView.findViewById(R.id.loadingView);

        return rootView;
    }

    @Override
    public void onClick(View v) {

        if(v.equals(btn_create)) {
            getCategories();
        } else if(v.equals(btn_search)) {
            getActivities();
        }
    }

    // For calling common SignUp API
    private void getCategories() {
        loadingView.setLoadingText("Loading ...");
        rl_loadingView.setVisibility(View.VISIBLE);

        StringRequest sr = new StringRequest(Request.Method.POST, FoodLoveApplication.SERVER_URL + "category?", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("category", response.toString());
                try {
                    JSONObject response_obj = new JSONObject(response.toString());
                    if(response_obj.getString("status").equals("OK")) {
                        if (response_obj.has("result")) {
                            JSONObject result_obj = response_obj.getJSONObject("result");
                            MainActivity.categoriesArray = result_obj.getJSONArray("category");
                            activity.pushFragments(ActivitiesCategoryFragment.class.getName(), true, true);
                        } else
                            Toast.makeText(rootView.getContext(), "No categories", Toast.LENGTH_SHORT).show();
                    } else
                        Toast.makeText(rootView.getContext(), response_obj.getString("message"), Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(rootView.getContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
                        else // for server error
                            Toast.makeText(rootView.getContext(), R.string.server_error, Toast.LENGTH_SHORT).show();

                        rl_loadingView.setVisibility(View.GONE);
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("key", FoodLoveApplication.SERVER_KEY);
                return params;
            }
        };

        // Adding request to request queue
        FoodLoveApplication.getInstance().addToRequestQueue(sr, "category");
    }

    // For calling common SignUp API
    private void getActivities() {
        loadingView.setLoadingText("Loading ...");
        rl_loadingView.setVisibility(View.VISIBLE);

        StringRequest sr = new StringRequest(Request.Method.POST, FoodLoveApplication.SERVER_URL + "all_post?", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("category", response.toString());
                try {
                    JSONObject response_obj = new JSONObject(response.toString());
                    if(response_obj.getString("status").equals("OK")) {
                        if (response_obj.has("result")) {
                            JSONObject result_obj = response_obj.getJSONObject("result");
                            MainActivity.postsArray = result_obj.getJSONArray("post");
                            activity.pushFragments(CardSwipeFragment.class.getName(), true, true);
                        } else
                            Toast.makeText(rootView.getContext(), "No Posts", Toast.LENGTH_SHORT).show();
                    } else
                        Toast.makeText(rootView.getContext(), response_obj.getString("message"), Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(rootView.getContext(), R.string.network_error, Toast.LENGTH_SHORT).show();
                        else // for server error
                            Toast.makeText(rootView.getContext(), R.string.server_error, Toast.LENGTH_SHORT).show();

                        rl_loadingView.setVisibility(View.GONE);
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("key", FoodLoveApplication.SERVER_KEY);
                params.put("user_id", FoodLoveApplication.myPrefs.getString("user_id", ""));
                return params;
            }
        };

        // Adding request to request queue
        FoodLoveApplication.getInstance().addToRequestQueue(sr, "category");
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.global, menu);
    }

    @Override
    public void onResume() {
        super.onResume();

        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        setHasOptionsMenu(true);
    }
}