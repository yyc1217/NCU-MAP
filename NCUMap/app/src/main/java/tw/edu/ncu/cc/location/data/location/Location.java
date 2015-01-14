package tw.edu.ncu.cc.location.data.location;

public class Location {

    private double lat;
    private double lng;

    public double getLat() {
        return lat;
    }

    public void setLat( double lat ) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng( double lng ) {
        this.lng = lng;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;

        Location location = ( Location ) o;

        if ( Double.compare( location.lat, lat ) != 0 ) return false;
        if ( Double.compare( location.lng, lng ) != 0 ) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits( lat );
        result = ( int ) ( temp ^ ( temp >>> 32 ) );
        temp = Double.doubleToLongBits( lng );
        result = 31 * result + ( int ) ( temp ^ ( temp >>> 32 ) );
        return result;
    }

}
