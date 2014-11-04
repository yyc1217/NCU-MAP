package tw.edu.ncu.cc.location.client.tool;

import tw.edu.ncu.cc.location.data.keyword.Word;
import tw.edu.ncu.cc.location.data.person.Person;
import tw.edu.ncu.cc.location.data.place.Place;
import tw.edu.ncu.cc.location.data.place.PlaceType;
import tw.edu.ncu.cc.location.data.unit.Unit;

import java.util.Set;

public interface SyncLocationClient {

    public Set<Place> getPlaces( String placeName );
    public Set<Place> getPlaces( PlaceType placeType );

    public Set<Person> getPeople( String peopleName );
    public Set<Unit> getUnits( String unitName );

    public Set<Word> getWords( String keyword );

}


