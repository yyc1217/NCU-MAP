package tw.edu.ncu.cc.location.data.unit;

import tw.edu.ncu.cc.location.data.location.Location;

public class Unit {

    private String unitCode;
    private String chineseName;
    private String englishName;
    private String shortName;
    private String fullName;
    private String url;
    private Location location;

    public String getUnitCode() {
        return unitCode;
    }

    public void setUnitCode( String unitCode ) {
        this.unitCode = unitCode;
    }

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

    public String getShortName() {
        return shortName;
    }

    public void setShortName( String shortName ) {
        this.shortName = shortName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName( String fullName ) {
        this.fullName = fullName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl( String url ) {
        this.url = url;
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

        Unit unit = ( Unit ) o;

        if ( chineseName != null ? !chineseName.equals( unit.chineseName ) : unit.chineseName != null ) return false;
        if ( englishName != null ? !englishName.equals( unit.englishName ) : unit.englishName != null ) return false;
        if ( fullName != null ? !fullName.equals( unit.fullName ) : unit.fullName != null ) return false;
        if ( location != null ? !location.equals( unit.location ) : unit.location != null ) return false;
        if ( shortName != null ? !shortName.equals( unit.shortName ) : unit.shortName != null ) return false;
        if ( unitCode != null ? !unitCode.equals( unit.unitCode ) : unit.unitCode != null ) return false;
        if ( url != null ? !url.equals( unit.url ) : unit.url != null ) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = unitCode != null ? unitCode.hashCode() : 0;
        result = 31 * result + ( chineseName != null ? chineseName.hashCode() : 0 );
        result = 31 * result + ( englishName != null ? englishName.hashCode() : 0 );
        result = 31 * result + ( shortName != null ? shortName.hashCode() : 0 );
        result = 31 * result + ( fullName != null ? fullName.hashCode() : 0 );
        result = 31 * result + ( url != null ? url.hashCode() : 0 );
        result = 31 * result + ( location != null ? location.hashCode() : 0 );
        return result;
    }

}
