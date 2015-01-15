package tw.edu.ncu.cc.ncumap;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.telephony.TelephonyManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import tw.edu.ncu.cc.location.client.tool.config.LocationConfig;
import tw.edu.ncu.cc.location.client.tool.config.NCULocationConfig;
import tw.edu.ncu.cc.location.client.tool.response.ResponseListener;
import tw.edu.ncu.cc.location.client.volley.NCUAsyncLocationClient;
import tw.edu.ncu.cc.location.data.keyword.Word;
import tw.edu.ncu.cc.location.data.place.PlaceType;


public class MyActivity extends ActionBarActivity {

    private static final String[] QUERY_OPTIONS = {"WHEELCHAIR_RAMP", "DISABLED_CAR_PARKING", "DISABLED_MOTOR_PARKING", "EMERGENCY", "AED", "RESTAURANT", "SPORT_RECREATION", "ADMINISTRATION", "RESEARCH", "DORMITORY", "OTHER", "TOILET", "ATM", "BUS_STATION", "PARKING_LOT"};
    private static final String[] QUERY_OPTIONS_TC = {"無障礙坡道", "無障礙汽車位", "無障礙機車位", "緊急", "AED", "餐廳", "休閒生活", "行政服務", "教學研究", "宿舍", "其他單位", "廁所", "提款機", "公車站牌", "停車場"};
    private boolean[] needList = {false, false, false, false, false, true, true, true, true, true, true, false, false, false, false};
    private boolean[] isSelected = new boolean[15];
    private Word[] searchSuggestions;
    private String[] searchSuggestionToString;
    private ListView suggestionList;
    private SearchView searchView;
    private MenuItem submitItem;
    private GridView gridView;

    private int selectNumber = 0;


    public static ArrayList<QueryData> selectedQueryOptions;  //user's selected options
    public static Word word;    //user's selected query string
    public static NCUAsyncLocationClient locationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        if (((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE)).getLine1Number() == null) {
            TextView emergencyText = (TextView) findViewById(R.id.emergency_text);
            emergencyText.setVisibility(View.GONE);
            LinearLayout buttonBar = (LinearLayout) findViewById(R.id.button_bar);
            buttonBar.removeAllViews();
            buttonBar.setVisibility(View.GONE);
        }
        else {
            //buttons of emergency numbers
            Button militaryCall = (Button) findViewById(R.id.military_button);
            Button frontSecurityCall = (Button) findViewById(R.id.front_security_button);
            Button backSecurityCall = (Button) findViewById(R.id.back_security_button);
            Button healthButton = (Button) findViewById(R.id.health_button);
            View.OnClickListener callListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    switch (v.getId()) {
                        case R.id.military_button:
                            intent.setData(Uri.parse("tel:03-2805666"));
                            break;
                        case R.id.front_security_button:
                            intent.setData(Uri.parse("tel:03-4227151%2357119"));
                            break;
                        case R.id.back_security_button:
                            intent.setData(Uri.parse("tel:03-4227151%2357319"));
                            break;
                        case R.id.health_button:
                            intent.setData(Uri.parse("tel:03-4227151%2357270"));
                            break;
                        default:
                            return;
                    }
                    startActivity(intent);
                }
            };
            militaryCall.setOnClickListener(callListener);
            frontSecurityCall.setOnClickListener(callListener);
            backSecurityCall.setOnClickListener(callListener);
            healthButton.setOnClickListener(callListener);
        }


        //create a ncu location client
        LocationConfig locationConfig;
        locationConfig = new NCULocationConfig();
        locationConfig.setServerAddress("http://140.115.3.97/location");
        locationClient = new NCUAsyncLocationClient(locationConfig, this);


        //settings of query options
        gridView = (GridView) findViewById(R.id.grid_view);
        gridView.setAdapter(new ImageAdapter(this, isSelected));
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (selectNumber == 0) {
                    isSelected[position] = !isSelected[position];
                    openMap();
                } else {
                    selectView(view, position);
                }
            }
        });
        gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                selectView(view, position);
                return true;
            }
        });


        //list view of query suggestions
        suggestionList = (ListView) findViewById(R.id.list_view);
        AdapterView.OnItemClickListener mMessageClickedHandler = new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                // Do something in response to the click
                word = searchSuggestions[position];
                searchView.setQuery(word.getWord(), false);
                suggestionList.setAdapter(null);
            }
        };
        suggestionList.setOnItemClickListener(mMessageClickedHandler);
        suggestionList.setBackgroundColor(Color.parseColor("#222222"));
    }

    @Override
    protected void onStart() {
        super.onStart();

        //initialize
        Arrays.fill(isSelected, false);
        word = null;
        selectedQueryOptions = new ArrayList<>();
        selectNumber = 0;
        if (submitItem != null)
            submitItem.setVisible(false);
        ((ImageAdapter) gridView.getAdapter()).notifyDataSetChanged();
        if (searchView != null)
            searchView.setQuery("", false);


        //check network status
        NetworkInfo networkInfo = ((ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (networkInfo == null || !networkInfo.isConnected() || !networkInfo.isAvailable()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setIcon(R.drawable.ic_launcher);
            builder.setTitle(R.string.network_unreachable);
            builder.setMessage(R.string.open_network_message);
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }
            });
            builder.setPositiveButton(R.string.network_settings, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    startActivity(new Intent(Settings.ACTION_SETTINGS));
                }
            });
            builder.show();
        }
    }

    /**
     * select view in position
     * @param view the view selected
     * @param position where the view is
     */
    private void selectView(View view, int position) {
        isSelected[position] = !isSelected[position];
        if (isSelected[position]) {
            selectNumber++;
            view.setBackgroundColor(Color.HSVToColor(new float[] {position * 19, (float) 0.7, (float) 0.9}));
            if (selectNumber == 1)
                submitItem.setVisible(true);
        }
        else {
            selectNumber--;
            view.setBackgroundColor(Color.parseColor("#00000000"));
            if (selectNumber == 0)
                submitItem.setVisible(false);
        }
    }

    /**
     * open map
     */
    private void openMap() {
        for(int i=0;i<isSelected.length;i++){
            if(isSelected[i]) {
                selectedQueryOptions.add(new QueryData(PlaceType.fromValue(QUERY_OPTIONS[i]), QUERY_OPTIONS_TC[i], needList[i], i));
            }
        }
        Intent mapsActivity = new Intent(MyActivity.this, MapsActivity.class);
        startActivity(mapsActivity);
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.my, menu);

        // Associate searchable configuration with the SearchView
        submitItem = menu.findItem(R.id.submit);
        MenuItem searchItem = menu.findItem(R.id.search);
        searchView =
                (SearchView) searchItem.getActionView();
        if (null != searchView) {
            searchView.setQueryHint(getString(R.string.query_hint));

            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                public boolean onQueryTextChange(String newText) {
                    if (word != null && word.getWord().equals(newText))
                        return false;
                    if (newText.length() == 0) {
                        suggestionList.setAdapter(null);
                    } else {
                        locationClient.getWords(newText, new ResponseListener<Word>() {
                            @Override
                            public void onResponse(List<Word> words) {
                                int i = 0;
                                searchSuggestions = new Word[words.size()];
                                for (Word word : words) {
                                    searchSuggestions[i] = word;
                                    i++;
                                }
                                searchSuggestionToString = new String[i];
                                for (int j = 0; j < i; j++) {
                                    searchSuggestionToString[j] = searchSuggestions[j].getWord();
                                }
                                suggestionList.setAdapter(new ArrayAdapter<>(getApplicationContext(),
                                        android.R.layout.simple_list_item_1, searchSuggestionToString));
                            }

                            @Override
                            public void onError(Throwable throwable) {

                            }
                        });
                    }


                    return true;
                }

                public boolean onQueryTextSubmit(String query) {
                    //Here u can get the value "query" which is entered in the search box.
                    if (word == null || ! word.getWord().equals(query)) {
                        locationClient.getWords(query, new ResponseListener<Word>() {
                            @Override
                            public void onResponse(List<Word> words) {
                                word = words.get(0);
                                openMap();
                            }

                            @Override
                            public void onError(Throwable throwable) {

                            }
                        });
                    }
                    else {
                        openMap();
                    }
                    return true;
                }
            });

            searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (! hasFocus)
                        suggestionList.setAdapter(null);
                }
            });
        }


        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.submit:
                openMap();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}


class QueryData {
    private PlaceType placeType;
    private String placeTypeTC;
    private boolean needList;
    private int num;

    public QueryData(PlaceType placeType, String placeTypeTC, boolean needList, int num) {
        this.placeType = placeType;
        this.placeTypeTC = placeTypeTC;
        this.needList = needList;
        this.num = num;
    }

    public PlaceType getPlaceType() {
        return placeType;
    }

    public void setPlaceType(PlaceType placeType) {
        this.placeType = placeType;
    }

    public String getPlaceTypeTC() {
        return placeTypeTC;
    }

    public void setPlaceTypeTC(String placeTypeTC) {
        this.placeTypeTC = placeTypeTC;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public boolean isNeedList() {
        return needList;
    }
}