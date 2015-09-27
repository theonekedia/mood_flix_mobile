package hobbyst.ola.foodlove;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.mingle.widget.LoadingView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import hobbyst.ola.foodlove.libs.CustomButton;
import hobbyst.ola.foodlove.libs.CustomEditText;

/**
 * Created by Arshad Parwez on 15/08/15.
 */
public class CreateActivityFragment extends BaseFragment implements View.OnClickListener {

    private static View rootView;
    private CustomButton btn_select_place;

    int PLACE_PICKER_REQUEST = 1;
    private static Place place_location;

    private static CustomEditText et_desc;

    private static RelativeLayout rl_loadingView;
    private static LoadingView loadingView;

    /**** Returns a new instance of this fragment for the given section number. ****/
    public static CreateActivityFragment newInstance(int sectionNumber) {
        CreateActivityFragment fragment = new CreateActivityFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.create_activity, container, false);

        btn_select_place = (CustomButton) rootView.findViewById(R.id.btn_select_location);
        btn_select_place.setOnClickListener(this);

        et_desc = (CustomEditText) rootView.findViewById(R.id.et_description);

        rl_loadingView = (RelativeLayout) rootView.findViewById(R.id.rl_loadingView);
        loadingView = (LoadingView) rootView.findViewById(R.id.loadingView);

        return rootView;
    }

    @Override
    public void onClick(View v) {

        if(v.equals(btn_select_place)) {
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            try {
                startActivityForResult(builder.build(rootView.getContext()), PLACE_PICKER_REQUEST);
            } catch (GooglePlayServicesRepairableException e) {
                e.printStackTrace();
            } catch (GooglePlayServicesNotAvailableException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_next, menu);

        MenuItem item = menu.getItem(0);
        SpannableString s = new SpannableString(item.getTitle());
        s.setSpan(new ForegroundColorSpan(Color.WHITE), 0, s.length(), 0);
        item.setTitle(s);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // For Hiding the Keyboard
        InputMethodManager imm = (InputMethodManager) rootView.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(et_desc.getWindowToken(), 0);

        switch(item.getItemId()){
            case R.id.next:
                createPost();
                return true;
            case android.R.id.home:
                activity.popFragments();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_PICKER_REQUEST) {
            if (resultCode == activity.RESULT_OK) {
                place_location = PlacePicker.getPlace(data, rootView.getContext());
                String toastMsg = String.format("Selected Place: %s", place_location.getName());
                btn_select_place.setText(String.format("Place: %s", place_location.getName()));
                Toast.makeText(rootView.getContext(), toastMsg, Toast.LENGTH_LONG).show();
            }
        }
    }

    // For calling common SignUp API
    private void createPost() {
        loadingView.setLoadingText("Creating Post ...");
        rl_loadingView.setVisibility(View.VISIBLE);

        StringRequest sr = new StringRequest(Request.Method.POST, FoodLoveApplication.SERVER_URL + "new_post?", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d("create_post", response.toString());
                try {
                    JSONObject response_obj = new JSONObject(response.toString());
                    if(response_obj.getString("status").equals("OK")) {
                        if (response_obj.has("result")) {
                            activity.popToRootFragment();
                            Toast.makeText(rootView.getContext(), "Post Successfully Created!", Toast.LENGTH_SHORT).show();
                        } else
                            Toast.makeText(rootView.getContext(), "Error creating Post", Toast.LENGTH_SHORT).show();
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
                params.put("location", place_location.getName().toString());
                params.put("description", et_desc.getText().toString());
                params.put("loc_lat", String.valueOf(place_location.getLatLng().latitude));
                params.put("loc_lng", String.valueOf(place_location.getLatLng().longitude));
                params.put("category_id", MainActivity.selected_cuisines);
                return params;
            }
        };

        // Adding request to request queue
        FoodLoveApplication.getInstance().addToRequestQueue(sr, "create_post");
    }
}