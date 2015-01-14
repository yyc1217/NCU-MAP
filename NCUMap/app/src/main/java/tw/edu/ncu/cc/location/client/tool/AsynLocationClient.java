package tw.edu.ncu.cc.location.client.tool;

import tw.edu.ncu.cc.location.client.tool.response.ResponseListener;
import tw.edu.ncu.cc.location.data.keyword.Word;
import tw.edu.ncu.cc.location.data.person.Person;
import tw.edu.ncu.cc.location.data.place.Place;
import tw.edu.ncu.cc.location.data.place.PlaceType;
import tw.edu.ncu.cc.location.data.unit.Unit;

public interface AsynLocationClient {

    public void getPlaces( String placeName, ResponseListener<Place> responseListener );
    public void getPlaces( PlaceType placeType , ResponseListener<Place> responseListener );

    public void getPlaceUnits( String placeName, ResponseListener<Unit> responseListener );

    public void getPeople( String peopleName, ResponseListener<Person> responseListener );
    public void getUnits ( String unitName, ResponseListener<Unit> responseListener );

    public void getWords ( String keyword, ResponseListener<Word> responseListener );

}
