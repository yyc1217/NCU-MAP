package tw.edu.ncu.cc.location.data.place;

import tw.edu.ncu.cc.location.data.location.Location;

public class Place {

    private String chineseName;
    private String englishName;
    private String pictureName;
    private PlaceType type;
    private Location location;

    public String getChineseName() {
        return chineseName;
    }

    public void setChineseName( String chineseName ) {
        this.chineseName = chineseName;
    }

    public String getEnglishName() {
        return englishName;
    }

    public void setEnglishName( String englishName ) {
        this.englishName = englishName;
    }

    public String getPictureName() {
        return pictureName;
    }

    public void setPictureName( String pictureName ) {
        this.pictureName = pictureName;
    }

    public PlaceType getType() {
        return type;
    }

    public void setType( PlaceType type ) {
        this.type = type;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation( Location location ) {
        this.location = location;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;

        Place place = ( Place ) o;

        if ( chineseName != null ? !chineseName.equals( place.chineseName ) : place.chineseName != null ) return false;
        if ( englishName != null ? !englishName.equals( place.englishName ) : place.englishName != null ) return false;
        if ( location != null ? !location.equals( place.location ) : place.location != null ) return false;
        if ( pictureName != null ? !pictureName.equals( place.pictureName ) : place.pictureName != null ) return false;
        if ( type != place.type ) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = chineseName != null ? chineseName.hashCode() : 0;
        result = 31 * result + ( englishName != null ? englishName.hashCode() : 0 );
        result = 31 * result + ( pictureName != null ? pictureName.hashCode() : 0 );
        result = 31 * result + ( type != null ? type.hashCode() : 0 );
        result = 31 * result + ( location != null ? location.hashCode() : 0 );
        return result;
    }

}
