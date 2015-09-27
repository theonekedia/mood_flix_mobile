package hobbyst.ola.foodlove;

import android.app.Fragment;
import android.os.Bundle;

/**
 * Created by Arshad Parwez on 17/08/15.
 */

/****
 * All inner Fragments must extends the BaseFragment instead of Fragment to
 * override the onBackPressed of the DocketActivity.
 ****/
public class BaseFragment extends Fragment {
    public static MainActivity activity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity = (MainActivity) this.getActivity();
    }

    public boolean onBackPressed() {
        return false;
    }
}