package tw.edu.ncu.cc.ncumap;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tw.edu.ncu.cc.location.client.tool.response.ResponseListener;
import tw.edu.ncu.cc.location.client.volley.NCUAsyncLocationClient;
import tw.edu.ncu.cc.location.data.keyword.Word;
import tw.edu.ncu.cc.location.data.keyword.WordType;
import tw.edu.ncu.cc.location.data.person.Person;
import tw.edu.ncu.cc.location.data.place.Place;
import tw.edu.ncu.cc.location.data.place.PlaceType;
import tw.edu.ncu.cc.location.data.unit.Unit;

/**
 * Created by Tatsujin on 2014/10/6.
 */
public class MapsActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private NCUAsyncLocationClient locationClient;
    private Map<String, NCUMarker> idMarkerMap;
    private Map<PlaceType, ArrayList<NCUMarker>> typeMarkersMap;
    private NavigationDrawerFragment mNavigationDrawerFragment;

    private Map<PlaceType, ArrayList<String>> navigationExpandItems;

    private final LatLng ncuLocation = new LatLng(24.968297, 121.192151);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);

        idMarkerMap = new HashMap<>();
        typeMarkersMap = new HashMap<>();
        locationClient = MyActivity.locationClient;

        ArrayList<PlaceTypeItem> navigationItems = new ArrayList<>();
        navigationExpandItems = new HashMap<>();
        for (QueryData queryData : MyActivity.selectedQueryOptions) {
            navigationItems.add(new PlaceTypeItem(queryData.getPlaceTypeTC(), queryData.getPlaceType(), Color.HSVToColor(new float[]{queryData.getNum() * 19, (float) 0.8, (float) 0.5})));
            if (queryData.isNeedList())
                navigationExpandItems.put(queryData.getPlaceType(), new ArrayList<String>());
            typeMarkersMap.put(queryData.getPlaceType(), new ArrayList<NCUMarker>());
        }


        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout),
                navigationItems, navigationExpandItems);


        setUpMapIfNeeded();

        if (MyActivity.word != null)
            setWord(MyActivity.word);

        for (final QueryData queryData : MyActivity.selectedQueryOptions) {
            Log.w("PlaceType", queryData.getPlaceType().value());
            locationClient.getPlaces(queryData.getPlaceType(), new ResponseListener<Place>() {
                @Override
                public void onResponse(List<Place> places) {
                    setPlaces(places, queryData.getNum());
                }

                @Override
                public void onError(Throwable throwable) {

                }
            });
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(android.os.Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        MapsInitializer.initialize(this);
        mMap.setMyLocationEnabled(true);
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        String provider = locationManager.getBestProvider(new Criteria(), true);
        if (provider != null) {
            Location location = locationManager.getLastKnownLocation(provider);
            moveToLocation(location);
            locationManager.requestSingleUpdate(provider, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    moveToLocation(location);
                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            }, null);
        }
        else
            moveToLocation(null);

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                NCUMarker ncuMarker = idMarkerMap.get(marker.getId());
                if (ncuMarker.getWordType().equals(WordType.PLACE)) {
                    Place place = (Place) ncuMarker.getObject();
                    if (place.getType().equals(PlaceType.AED) || place.getType().equals(PlaceType.EMERGENCY)) {
                        Location location = mMap.getMyLocation();
                        Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                                Uri.parse("http://maps.google.com/maps?saddr=" + location.getLatitude() + "," + location.getLongitude() + "&daddr=" + marker.getPosition().latitude + "," + marker.getPosition().longitude + "&dirflg=w"));
                        startActivity(intent);
                        return true;
                    }
                    if (!place.getType().equals(PlaceType.SPORT_RECREATION))
                        return false;
                }
                AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
                builder.setTitle(marker.getTitle() + " " + marker.getSnippet());
                View message = null;
                switch (ncuMarker.getWordType()) {
                    case PERSON:
                        Person person = (Person) ncuMarker.getObject();
                        message = getLayoutInflater().inflate(R.layout.dialog_content, null);
                        ((TextView) message).setMovementMethod(LinkMovementMethod.getInstance());
                        ((TextView) message).setText(Html.fromHtml("<a href=\"" + person.getPrimaryUnit().getUrl() + "\">"
                                + person.getPrimaryUnit().getChineseName() + " " + person.getPrimaryUnit().getEnglishName() + "</a>"));
                        if (person.getSecondaryUnit() != null)
                            ((TextView) message).append(Html.fromHtml("<br /><a href=\"" + person.getSecondaryUnit().getUrl() + "\">"
                                    + person.getSecondaryUnit().getChineseName() + " " + person.getSecondaryUnit().getEnglishName() + "</a>"));
                        break;
                    case PLACE:
                        Place place = (Place) ncuMarker.getObject();
                        message = new ImageView(MapsActivity.this);
                        getNetImage((ImageView) message, place.getPictureName());
                        break;
                    case UNIT:
                        Unit unit = (Unit) ncuMarker.getObject();
                        message = getLayoutInflater().inflate(R.layout.dialog_content, null);
                        ((TextView) message).setMovementMethod(LinkMovementMethod.getInstance());
                        if (unit.getUrl() != null)
                            ((TextView) message).setText(Html.fromHtml("<a href=\"" + unit.getUrl() + "\">單位網站</a>"));
                        else
                            ((TextView) message).setText("目前沒有可顯示的資訊");
                        break;
                }
                builder.setView(message);
                builder.setNegativeButton(R.string.close, null);
                builder.show();
                return true;
            }
        });
    }

    private void moveToLocation(Location location) {
        if (location == null) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ncuLocation, 15));
            return;
        }
        LatLng myLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        float[] result = new float[3];
        Location.distanceBetween(myLatLng.latitude, myLatLng.longitude, ncuLocation.latitude, ncuLocation.longitude, result);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(result[0] > 20000 ? ncuLocation : myLatLng, 15));
        Log.w("Distance", String.valueOf(result[0]));
    }

    private void setWord(Word word) {
        WordType type = word.getType();
        switch (type) {
            case PERSON:
                locationClient.getPeople(word.getWord(), new ResponseListener<Person>() {
                    @Override
                    public void onResponse(List<Person> people) {
                        setPeople(people);
                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }
                });
                break;
            case UNIT:
                locationClient.getUnits(word.getWord(), new ResponseListener<Unit>() {
                    @Override
                    public void onResponse(List<Unit> units) {
                        setUnits(units);
                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }
                });
                break;
            case PLACE:
                locationClient.getPlaces(word.getWord(), new ResponseListener<Place>() {
                    @Override
                    public void onResponse(List<Place> places) {
                        setPlaces(places, 16);
                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }
                });
        }
    }

    private void getNetImage(final ImageView imageView, String url) {
        ImageRequest imageRequest = new ImageRequest(url, new Response.Listener<Bitmap>() {
            @Override
            public void onResponse(Bitmap response) {
                imageView.setImageBitmap(response);
            }
        }, 0, 0, null, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                imageView.setImageResource(R.drawable.nashi);
            }
        });
        locationClient.getQueue().add(imageRequest);
    }

    private void setPlaces(List<Place> places, float iconColor) {
        for (Place place : places) {
            Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(place.getLocation().getLat(), place.getLocation().getLng()))
                    .title(place.getChineseName())
                    .snippet(place.getEnglishName())
                    .icon(BitmapDescriptorFactory.defaultMarker(iconColor * 19)));
            NCUMarker ncuMarker = new NCUMarker(WordType.PLACE, place, marker);
            idMarkerMap.put(marker.getId(), ncuMarker);
            typeMarkersMap.get(place.getType()).add(ncuMarker);
            ArrayList<String> items = navigationExpandItems.get(place.getType());
            if (items != null)
                items.add(place.getChineseName());
        }
    }

    private void setUnits(List<Unit> units) {
        for (Unit unit : units) {
            if (unit.getLocation() == null)
                continue;
            Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(unit.getLocation().getLat(), unit.getLocation().getLng()))
                    .title(unit.getFullName())
                    .snippet(unit.getEnglishName())
                    .icon(BitmapDescriptorFactory.defaultMarker(323)));
            idMarkerMap.put(marker.getId(), new NCUMarker(WordType.UNIT, unit, marker));
        }
    }

    private void setPeople(List<Person> people) {
        for (Person person : people) {
            if (person.getPrimaryUnit().getLocation() == null)
                continue;
            Marker marker = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(person.getPrimaryUnit().getLocation().getLat(), person.getPrimaryUnit().getLocation().getLng()))
                    .title(person.getChineseName())
                    .snippet(person.getEnglishName())
                    .icon(BitmapDescriptorFactory.defaultMarker(342)));
            idMarkerMap.put(marker.getId(), new NCUMarker(WordType.PERSON, person, marker));
        }
    }

    @Override
    public void onNavigationDrawerItemSelected(int position, boolean selected) {
        PlaceType placeType = MyActivity.selectedQueryOptions.get(position).getPlaceType();
        for (NCUMarker ncuMarker : typeMarkersMap.get(placeType))
            ncuMarker.getMarker().setVisible(selected);
    }

    @Override
    public void onNavigationDrawerExpandItemSelected(int position, int which) {
        PlaceType placeType = MyActivity.selectedQueryOptions.get(position).getPlaceType();
        Marker marker = typeMarkersMap.get(placeType).get(which).getMarker();
        LatLng latLng = marker.getPosition();
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
    }
}

class NCUMarker {
    private WordType wordType;
    private Object object;
    private Marker marker;

    public NCUMarker(WordType wordType, Object object, Marker marker) {
        this.wordType = wordType;
        this.object = object;
        this.marker = marker;
    }

    public WordType getWordType() {
        return wordType;
    }

    public Object getObject() {
        return object;
    }

    public Marker getMarker() {
        return marker;
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }
}
