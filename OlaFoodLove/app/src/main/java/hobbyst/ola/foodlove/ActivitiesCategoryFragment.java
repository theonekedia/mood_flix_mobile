/**
 * 
 * @author Arshad
 * 
 * @Company Konstant
 *
 */

package hobbyst.ola.foodlove;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import hobbyst.ola.foodlove.libs.CustomTextView;
import hobbyst.ola.foodlove.libs.collectionpicker.CollectionItem;
import hobbyst.ola.foodlove.libs.collectionpicker.CollectionOnItemClickListener;
import hobbyst.ola.foodlove.libs.collectionpicker.CollectionPicker;

public class ActivitiesCategoryFragment extends BaseFragment {

    private static View view;
    private static CustomTextView title;
    private static CollectionPicker cuisinesPicker;
    int counter;

    // Custom Listener for reloading the dishes
    public static ReloadDishesListener reloadDishesListener;

    public static ActivitiesCategoryFragment newInstance(String text) {
        ActivitiesCategoryFragment f = new ActivitiesCategoryFragment();
        Bundle b = new Bundle();
        b.putString("msg", text);
        f.setArguments(b);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.category_select, container, false);

        cuisinesPicker = (CollectionPicker) view.findViewById(R.id.collection_item_picker);

        cuisinesPicker.setItems(generateItems(MainActivity.categoriesArray));

        title = (CustomTextView) view.findViewById(R.id.tv_title);

        cuisinesPicker.setOnItemClickListener(new CollectionOnItemClickListener() {
            @Override
            public void onClick(CollectionItem item, int position) {
                if (item.isSelected)
                    counter++;
                else
                    counter--;

                if (counter > 0)
                    title.setText(counter + " Activity Selected");
                else
                    title.setText(R.string.tap_categories);
            }
        });

        return view;
    }

    // For setting the Cuisines List of Pickers
    private List<CollectionItem> generateItems(JSONArray cuisines_array) {
        try {
            List<CollectionItem> items = new ArrayList<>();
            if (cuisines_array.length() > 0) {
                for (int c = 0; c < cuisines_array.length(); c++) {
                    items.add(new CollectionItem(cuisines_array.getJSONObject(c).getString("id"),
                            cuisines_array.getJSONObject(c).getString("name")));
                }
            }

            return items;
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            // Attaching a listener for reloading the Dishes
            reloadDishesListener = (ReloadDishesListener) activity;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Interface to implement when you want to get notified of 'reloading Dishes'
     * events. Call reloadDishes(..) to activate ReloadDishesListener.
     */
    public interface ReloadDishesListener {

        /**
         * Method to be called when a Location is selected
         */
        public void reloadDishes();
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
        switch (item.getItemId()) {
            case R.id.next:
                if(counter < 1)
                    Toast.makeText(view.getContext(), R.string.min_activities, Toast.LENGTH_SHORT).show();
                else if(counter > 1)
                    Toast.makeText(view.getContext(), R.string.max_activities, Toast.LENGTH_SHORT).show();
                else {
                    for (int c = 0; c < cuisinesPicker.getItems().size(); c++) {
                        if (cuisinesPicker.getItems().get(c).isSelected)
                            MainActivity.selected_cuisines = cuisinesPicker.getItems().get(c).id;
                    }
                    activity.pushFragments(CreateActivityFragment.class.getName(), true, true);
                }
                return true;
            case android.R.id.home:
                activity.popFragments();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}