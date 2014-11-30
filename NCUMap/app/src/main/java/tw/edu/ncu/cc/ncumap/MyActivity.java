package tw.edu.ncu.cc.ncumap;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
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


public class MyActivity extends Activity {

    private static final String[] QUERY_OPTIONS = {"WHEELCHAIR_RAMP", "DISABLED_CAR_PARKING", "DISABLED_MOTOR_PARKING", "EMERGENCY", "AED", "RESTAURANT", "SPORT_RECREATION", "ADMINISTRATION", "RESEARCH", "DORMITORY", "OTHER", "TOILET", "ATM", "BUS_STATION", "PARKING_LOT"};
    private static final String[] QUERY_OPTIONS_TC = {"無障礙坡道", "無障礙汽車位", "無障礙機車位", "緊急", "AED", "餐廳", "休閒生活", "行政服務", "教學研究", "宿舍", "其他單位", "廁所", "提款機", "公車站牌", "停車場"};
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

        //buttons of emergency numbers
        Button militaryCall = (Button) findViewById(R.id.militaryButton);
        Button frontSecurityCall = (Button) findViewById(R.id.frontSecurityButton);
        Button backSecurityCall = (Button) findViewById(R.id.backSecurityButton);
        Button healthButton = (Button) findViewById(R.id.healthButton);
        View.OnClickListener callListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                switch (v.getId()) {
                    case R.id.militaryButton:
                        intent.setData(Uri.parse("tel:03-2805666"));
                        break;
                    case R.id.frontSecurityButton:
                        intent.setData(Uri.parse("tel:03-4227151%2357119"));
                        break;
                    case R.id.backSecurityButton:
                        intent.setData(Uri.parse("tel:03-4227151%2357319"));
                        break;
                    case R.id.healthButton:
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


        //get a ncu location client
        LocationConfig locationConfig;
        locationConfig = new NCULocationConfig();
        locationConfig.setServerAddress("http://140.115.3.97/location");
        locationClient = new NCUAsyncLocationClient(locationConfig, this);


        //settings of query options
        gridView = (GridView) findViewById(R.id.gridView);
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
        suggestionList = (ListView) findViewById(R.id.listView);
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
        selectedQueryOptions = new ArrayList<QueryData>();
        selectNumber = 0;
        if (submitItem != null)
            submitItem.setVisible(false);
        ((ImageAdapter) gridView.getAdapter()).notifyDataSetChanged();
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
            view.setBackgroundColor(Color.HSVToColor(new float[] {position * 19, (float) 0.4, (float) 0.9}));
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
                selectedQueryOptions.add(new QueryData(PlaceType.fromValue(QUERY_OPTIONS[i]), QUERY_OPTIONS_TC[i], i));
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
                                suggestionList.setAdapter(new ArrayAdapter<String>(getApplicationContext(),
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
    private int num;

    public QueryData(PlaceType placeType, String placeTypeTC, int num) {
        this.placeType = placeType;
        this.placeTypeTC = placeTypeTC;
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
}