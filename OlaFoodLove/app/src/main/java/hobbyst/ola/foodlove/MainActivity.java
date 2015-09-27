package hobbyst.ola.foodlove;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONArray;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ActivitiesCategoryFragment.ReloadDishesListener {

    // Defining Variables
    public static Typeface RMedium;
    public static Toolbar toolbar;

    private static final int RC_SIGN_IN = 0;
    /* Client used to interact with Google APIs. */
    public static GoogleApiClient mGoogleApiClient;
    /* Is there a ConnectionResult resolution in progress? */
    public static boolean mIsResolving = false;
    /* Should we automatically resolve ConnectionResults when possible? */
    public static boolean mShouldResolve = false;

    public static JSONArray categoriesArray, postsArray;
    public static String selected_cuisines;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        if(Build.VERSION.SDK_INT >= 21) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(getResources().getColor(R.color.Primary700));
        }

        RMedium = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Medium.ttf");

        // Initializing Toolbar and setting it as the actionbar
        toolbar = (Toolbar) findViewById(R.id.toolbar_layout);
        toolbar.setTitle(" ");
        setSupportActionBar(toolbar);

        TextView toolbarTitle = (TextView) toolbar.findViewById(R.id.toolbar_title);
        toolbarTitle.setText(getResources().getText(R.string.app_name));
        toolbarTitle.setTypeface(RMedium);

        // Initialising the base fragment
        pushFragments(SelectSourceFragment.class.getName(), false, false);
    }

    /**** For adding fragments from a tab ****/
    public void pushFragments(String fragment_name, boolean shouldAnimate, boolean shouldAdd) {
        Fragment fragment = Fragment.instantiate(this, fragment_name);
        FragmentTransaction ft = getFragmentManager().beginTransaction();
        if (shouldAnimate)
            ft.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_left, R.anim.slide_in_right, R.anim.slide_out_right);
        ft.replace(R.id.container, fragment);

        if(shouldAdd)
            ft.addToBackStack(AppConstants.TAG_Order);
        ft.commit();
    }

    /**** For removing fragments from a tab ****/
    public void popFragments() {
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.popBackStack();
    }

    /**** For removing fragments from a tab ****/
    public void popToRootFragment() {
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.popBackStack(AppConstants.TAG_Order, FragmentManager.POP_BACK_STACK_INCLUSIVE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.global, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (getFragmentManager().findFragmentById(R.id.container) instanceof SelectSourceFragment) {
                this.finish();
                return true;
            } else {
                popFragments();
                return false;
            }
        }

        return super.onKeyUp(keyCode, event);
    }

    /**** For loading the Dishes based on the Food Types & Cuisines selection ****/
    @Override
    public void reloadDishes() {

    }
}
