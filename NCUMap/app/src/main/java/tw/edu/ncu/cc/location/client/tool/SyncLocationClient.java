package tw.edu.ncu.cc.location.client.tool;

import tw.edu.ncu.cc.location.data.keyword.Word;
import tw.edu.ncu.cc.location.data.person.Person;
import tw.edu.ncu.cc.location.data.place.Place;
import tw.edu.ncu.cc.location.data.place.PlaceType;
import tw.edu.ncu.cc.location.data.unit.Unit;

import java.util.List;

public interface SyncLocationClient {

    public List<Place> getPlaces( String placeName );
    public List<Place> getPlaces( PlaceType placeType );

    public List<Person> getPeople( String peopleName );
    public List<Unit> getUnits( String unitName );

    public List<Word> getWords( String keyword );

}


