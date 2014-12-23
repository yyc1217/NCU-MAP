package tw.edu.ncu.cc.ncumap;

import android.content.Context;
import android.support.v7.app.ActionBar;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

import tw.edu.ncu.cc.location.data.keyword.WordType;
import tw.edu.ncu.cc.location.data.place.PlaceType;

/**
 * Created by tatsujin on 14/12/8.
 */
public class NavigationDrawerFragment extends Fragment implements ListAdapter.OnItemSelectedListener, ListAdapter.OnExpandItemSelectedListener {

    /**
     * Remember the position of the selected item.
     */

    /**
     * Per the design guidelines, you should show the drawer on launch until the user manually
     * expands it. This shared preference tracks this.
     */
    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";

    /**
     * A pointer to the current callbacks instance (the Activity).
     */
    private NavigationDrawerCallbacks mCallbacks;

    /**
     * Helper component that ties the action bar to the navigation drawer.
     */
    private ActionBarDrawerToggle mDrawerToggle;

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerListView;
    private ListAdapter mDrawerListAdapter;
    private View mFragmentContainerView;

    private boolean mUserLearnedDrawer;

    private ArrayList<Boolean> isItemSelected;

    public NavigationDrawerFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Read in the flag indicating whether or not the user has demonstrated awareness of the
        // drawer. See PREF_USER_LEARNED_DRAWER for details.
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Indicate that this fragment would like to influence the set of actions in the action bar.
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mDrawerListView = (ListView) inflater.inflate(
                R.layout.fragment_navigation_drawer, container, false);
        return mDrawerListView;
    }

    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }

    /**
     * Users of this fragment must call this method to set up the navigation drawer interactions.
     *
     * @param fragmentId   The android:id of this fragment in its activity's layout.
     * @param drawerLayout The DrawerLayout containing this fragment's UI.
     */
    public void setUp(int fragmentId, DrawerLayout drawerLayout, ArrayList<NavigationListItem> items, Map<PlaceType, ArrayList<String>> expandItems) {

        Boolean[] addToIsItemSelected = new Boolean[items.size()];
        Arrays.fill(addToIsItemSelected, true);
        isItemSelected = new ArrayList<>(Arrays.asList(addToIsItemSelected));

        mDrawerListAdapter = new ListAdapter(getActivity(), items, expandItems, isItemSelected, this, this);
        mDrawerListView.setAdapter(mDrawerListAdapter);

        for (int i = 0; i != items.size(); ++i) {
            mDrawerListView.setItemChecked(i, true);
        }

        mFragmentContainerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the navigation drawer and the action bar app icon.
        mDrawerToggle = new ActionBarDrawerToggle(
                getActivity(),                    /* host Activity */
                mDrawerLayout,                    /* DrawerLayout object */
                R.drawable.ic_drawer,             /* nav drawer image to replace 'Up' caret */
                R.string.navigation_drawer_open,  /* "open drawer" description for accessibility */
                R.string.navigation_drawer_close  /* "close drawer" description for accessibility */
        ) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (!isAdded()) {
                    return;
                }

                getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!isAdded()) {
                    return;
                }

                if (!mUserLearnedDrawer) {
                    // The user manually opened the drawer; store this flag to prevent auto-showing
                    // the navigation drawer automatically in the future.
                    mUserLearnedDrawer = true;
                    SharedPreferences sp = PreferenceManager
                            .getDefaultSharedPreferences(getActivity());
                    sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).apply();
                }

                getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }
        };

        // If the user hasn't 'learned' about the drawer, open it to introduce them to the drawer,
        // per the navigation drawer design guidelines.
        if (!mUserLearnedDrawer) {
            mDrawerLayout.openDrawer(mFragmentContainerView);
        }

        // Defer code dependent on restoration of previous instance state.
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    public void onItemSelected(int position) {
        boolean selected = !isItemSelected.get(position);
        isItemSelected.set(position, selected);
        if (mDrawerListView != null) {
            mDrawerListView.setItemChecked(position, selected);
        }
        if (mCallbacks != null) {
            mCallbacks.onNavigationDrawerItemSelected(position, selected);
        }
        Log.w("position " + position, String.valueOf(isItemSelected.get(position)));
    }

    @Override
    public void onExpandItemSelected(int position, int which) {
        if (mDrawerLayout != null)
            mDrawerLayout.closeDrawer(mFragmentContainerView);
        mCallbacks.onNavigationDrawerExpandItemSelected(position, which);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (NavigationDrawerCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Forward the new configuration the drawer toggle component.
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);

    }

    private ActionBar getActionBar() {
        return ((ActionBarActivity) getActivity()).getSupportActionBar();
    }

    /**
     * Callbacks interface that all activities using this fragment must implement.
     */
    public static interface NavigationDrawerCallbacks {
        /**
         * Called when an item in the navigation drawer is selected.
         */
        void onNavigationDrawerItemSelected(int position, boolean selected);
        void onNavigationDrawerExpandItemSelected(int position, int which);
    }
}

class ListAdapter extends BaseAdapter {

    interface OnItemSelectedListener {
        public void onItemSelected(int position);
    }

    interface OnExpandItemSelectedListener {
        public void onExpandItemSelected(int position, int which);
    }

    private ArrayList<NavigationListItem> items;
    private Map<PlaceType, ArrayList<String>> expandItemsMap;
    private Context context;
    private ArrayList<Boolean> isItemSelected;
    private OnItemSelectedListener onItemSelectedListener;
    private OnExpandItemSelectedListener onExpandItemSelectedListener;
    private boolean[] isExpand;

    public ListAdapter(Context context, ArrayList<NavigationListItem> items, Map<PlaceType, ArrayList<String>> expandItemsMap, ArrayList<Boolean> isItemSelected, OnItemSelectedListener onItemSelectedListener, OnExpandItemSelectedListener onExpandItemSelectedListener) {
        this.context = context;
        this.items = items;
        this.expandItemsMap = expandItemsMap;
        this.isItemSelected = isItemSelected;
        this.onItemSelectedListener = onItemSelectedListener;
        this.onExpandItemSelectedListener = onExpandItemSelectedListener;
        isExpand = new boolean[items.size()];
        Arrays.fill(isExpand, false);
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ArrayList<String> expandItems = expandItemsMap.get(items.get(position).getPlaceType());
        CheckBox checkBox;
        if (expandItems != null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.navigation_expand_item, parent, false);
            final LinearLayout linearLayout = (LinearLayout) ((LinearLayout) convertView).getChildAt(0);
            checkBox = (CheckBox) linearLayout.getChildAt(0);
            final ImageView imageView = (ImageView) linearLayout.getChildAt(1);
            imageView.setImageResource(isExpand[position] ? R.drawable.ic_action_expand : R.drawable.ic_action_collapse);
            final LinearLayout finalConvertView = (LinearLayout) convertView;
            if (isExpand[position]) {
                int i = 0;
                for (String expandItem : expandItems) {
                    TextView expandTextView = (TextView) LayoutInflater.from(context).inflate(R.layout.navigation_expand_list_item, (LinearLayout) finalConvertView, false);
                    expandTextView.setText(expandItem);
                    expandTextView.setVisibility(isExpand[position] ? View.VISIBLE : View.GONE);
                    final int finalI = i;
                    expandTextView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (onExpandItemSelectedListener != null)
                                onExpandItemSelectedListener.onExpandItemSelected(position, finalI);
                        }
                    });
                    finalConvertView.addView(expandTextView);
                    ++i;
                }
            }
            linearLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isExpand[position] = !isExpand[position];
                    imageView.setImageResource(isExpand[position] ? R.drawable.ic_action_expand : R.drawable.ic_action_collapse);
                    if (isExpand[position]) {
                        int i = 0;
                        for (String expandItem : expandItems) {
                            TextView expandTextView = (TextView) LayoutInflater.from(context).inflate(R.layout.navigation_expand_list_item, (LinearLayout) finalConvertView, false);
                            expandTextView.setText(expandItem);
                            expandTextView.setVisibility(isExpand[position] ? View.VISIBLE : View.GONE);
                            final int finalI = i;
                            expandTextView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (onExpandItemSelectedListener != null)
                                        onExpandItemSelectedListener.onExpandItemSelected(position, finalI);
                                }
                            });
                            finalConvertView.addView(expandTextView);
                            ++i;
                        }
                    }
                    else {
                        finalConvertView.removeAllViews();
                        finalConvertView.addView(linearLayout);
                    }

                }
            });
        }
        else {
            convertView = LayoutInflater.from(context).inflate(R.layout.navigation_list_item, parent, false);
            checkBox = (CheckBox) convertView;
        }
        checkBox.setText(items.get(position).getName());
        checkBox.setTextColor(items.get(position).getColor());
        checkBox.setChecked(isItemSelected.get(position));
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemSelectedListener.onItemSelected(position);
            }
        });
        return convertView;
    }
}

class NavigationListItem {

    String name;
    PlaceType placeType;
    int color;


    NavigationListItem(String name, PlaceType placeType, int color) {
        this.name = name;
        this.placeType = placeType;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public PlaceType getPlaceType() {
        return placeType;
    }

    public void setPlaceType(PlaceType placeType) {
        this.placeType = placeType;
    }
}