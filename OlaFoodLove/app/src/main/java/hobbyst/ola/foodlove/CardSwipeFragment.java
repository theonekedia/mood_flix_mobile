package hobbyst.ola.foodlove;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.andtinder.model.CardModel;
import com.andtinder.model.Orientations;
import com.andtinder.view.CardContainer;
import com.andtinder.view.SimpleCardStackAdapter;
import com.mingle.widget.LoadingView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.json.JSONException;
import org.json.JSONObject;

public class CardSwipeFragment extends BaseFragment {

    private static View rootView;
    private static CardContainer mCardContainer;

    private static RelativeLayout rl_loadingView;
    private static LoadingView loadingView;
    public static String TAG = "Order Home Fragment";

    /**** Returns a new instance of this fragment for the given section number. ****/
    public static CardSwipeFragment newInstance(int sectionNumber) {
        CardSwipeFragment fragment = new CardSwipeFragment();
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
        rootView = inflater.inflate(R.layout.card_swipe, container, false);

        mCardContainer = (CardContainer) rootView.findViewById(R.id.layoutview);
        mCardContainer.setOrientation(Orientations.Orientation.Disordered);


        SimpleCardStackAdapter adapter = new SimpleCardStackAdapter(rootView.getContext());
        for(int i = 0; i<MainActivity.postsArray.length(); i++) {
            try {
                JSONObject post = MainActivity.postsArray.getJSONObject(i);

                CardModel card = new CardModel(post.getString("location"), post.getString("description"), rootView.getResources().getDrawable(R.drawable.picture1));

                card.setOnCardDimissedListener(new CardModel.OnCardDimissedListener() {
                    @Override
                    public void onLike() {
                        Log.d("Swipeable Card", "I liked it");
                    }

                    @Override
                    public void onDislike() {
                        Log.d("Swipeable Card", "I did not liked it");
                    }
                });

                card.setOnClickListener(new CardModel.OnClickListener() {
                    @Override
                    public void OnClickListener() {
                        Log.i("Swipeable Cards", "I am pressing the card");
                    }
                });

                adapter.add(card);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        mCardContainer.setAdapter(adapter);

        rl_loadingView = (RelativeLayout) rootView.findViewById(R.id.rl_loadingView);
        loadingView = (LoadingView) rootView.findViewById(R.id.loadingView);

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.global, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                activity.popFragments();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    void setGoogleProfilePic(final String profilePic) {
            View vv = (View) rootView.findViewById(R.id.global_container);
            final ImageView image = (ImageView) vv.findViewById(R.id.image);
            Target target = new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    if(Build.VERSION.SDK_INT < 16)
                        image.setBackgroundDrawable(new BitmapDrawable(bitmap));
                    else
                        image.setBackground(new BitmapDrawable(rootView.getResources(), bitmap));
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {
                    // use error drawable if desired
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            };

            // thumbnail image
            Picasso.with(rootView.getContext()) // Context : con
                    .load(profilePic) // load: This path may be a remote URL,
                            //file resource (prefixed with file:), content resource (prefixed with content:),
                            //or android resource (prefixed with android.resource:
                    .error(R.drawable.com_facebook_profile_picture_blank_square)
                    .placeholder(R.drawable.com_facebook_profile_picture_blank_square)
                    .into(target);

            image.setTag(target);
    }
}