package tw.edu.ncu.cc.ncumap;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

import tw.edu.ncu.cc.location.client.tool.config.LocationConfig;
import tw.edu.ncu.cc.location.client.tool.config.NCULocationConfig;
import tw.edu.ncu.cc.location.client.tool.response.ResponseListener;
import tw.edu.ncu.cc.location.client.volley.NCUAsyncLocationClient;
import tw.edu.ncu.cc.location.data.keyword.Word;


public class MyActivity extends Activity {

    private String[] queryOptions = {"[WHEELCHAIR_RAMP]", "[DISABLED_CAR_PARKING]", "[DISABLED_MOTOR_PARKING]", "[EMERGENCY]", "[AED]", "[RESTAURANT]", "[SPORT_RECREATION]", "[ADMINISTRATION]", "[RESEARCH]", "[DORMITORY]", "[OTHER]", "[TOILET]", "[ATM]", "[BUS_STATION]", "[PARKING_LOT]"};
    private boolean[] isSelected = {false,false,false,false,false,false,false,false,false,false,false,false,false,false,false};
    private Word[] searchSuggestions;
    private String[] searchSuggestionToString;
    private ListView suggestionList;
    private SearchView searchView;


    public static ArrayList<String> selectedQueryOptions = new ArrayList<String>();//user's selected options
    public static Word word;    //user's selected query string
    public static NCUAsyncLocationClient locationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        LocationConfig locationConfig;
        locationConfig = new NCULocationConfig();
        locationConfig.setServerAddress("http://140.115.3.97/location");

        locationClient = new NCUAsyncLocationClient(locationConfig, this);

        GridView gridview = (GridView) findViewById(R.id.gridView);
        gridview.setAdapter(new ImageAdapter(this));

        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                isSelected[position] = !isSelected[position];
            }
        });

        gridview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                isSelected[position] = !isSelected[position];
                if (isSelected[position])
                    view.setBackgroundColor(Color.CYAN);
                else
                    view.setBackgroundColor(Color.parseColor("#00000000"));
                return true;
            }
        });


        //query suggestoin listview
        suggestionList = (ListView) findViewById(R.id.listView);
        AdapterView.OnItemClickListener mMessageClickedHandler = new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                // Do something in response to the click
                for(int i=0;i<isSelected.length;i++){
                    if(isSelected[i]){selectedQueryOptions.add(queryOptions[i]);}
                }
                word = searchSuggestions[position];
                searchView.setQuery(word.getWord(), false);
                suggestionList.setAdapter(null);
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
            }
        };
        suggestionList.setOnItemClickListener(mMessageClickedHandler);
        suggestionList.setBackgroundColor(Color.parseColor("#222222"));


    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.my, menu);

        // Associate searchable configuration with the SearchView
        MenuItem searchItem = menu.findItem(R.id.search);
        searchView =
                (SearchView) searchItem.getActionView();
        if (null != searchView) {
            searchView.setSubmitButtonEnabled(true);
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                public boolean onQueryTextChange(String newText) {
                    if (word != null && word.getWord().equals(newText))
                        return false;
                    if (newText.length() == 0) {
                        suggestionList.setAdapter(null);
                    } else {
                        locationClient.getWords(newText, new ResponseListener<Word>() {
                            @Override
                            public void onResponse(Set<Word> words) {
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
                    if (word == null) {
                        locationClient.getWords(query, new ResponseListener<Word>() {
                            @Override
                            public void onResponse(Set<Word> words) {
                                word = words.iterator().next();
                                Intent mapsActivity = new Intent(MyActivity.this, MapsActivity.class);
                                startActivity(mapsActivity);
                            }

                            @Override
                            public void onError(Throwable throwable) {

                            }
                        });
                    }
                    else {
                        Intent mapsActivity = new Intent(MyActivity.this, MapsActivity.class);
                        startActivity(mapsActivity);
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

        return super.onOptionsItemSelected(item);
    }
}
