package tw.edu.ncu.cc.location.data.person;

import tw.edu.ncu.cc.location.data.unit.Unit;

public class Person {

    private String chineseName;
    private String englishName;
    private String title;
    private Unit primaryUnit;
    private Unit secondaryUnit;
    private String officePhone;

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

    public String getTitle() {
        return title;
    }

    public void setTitle( String title ) {
        this.title = title;
    }

    public Unit getPrimaryUnit() {
        return primaryUnit;
    }

    public void setPrimaryUnit( Unit primaryUnit ) {
        this.primaryUnit = primaryUnit;
    }

    public Unit getSecondaryUnit() {
        return secondaryUnit;
    }

    public void setSecondaryUnit( Unit secondaryUnit ) {
        this.secondaryUnit = secondaryUnit;
    }

    public String getOfficePhone() {
        return officePhone;
    }

    public void setOfficePhone( String officePhone ) {
        this.officePhone = officePhone;
    }

    @Override
    public boolean equals( Object o ) {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;

        Person person = ( Person ) o;

        if ( chineseName != null ? !chineseName.equals( person.chineseName ) : person.chineseName != null )
            return false;
        if ( englishName != null ? !englishName.equals( person.englishName ) : person.englishName != null )
            return false;
        if ( officePhone != null ? !officePhone.equals( person.officePhone ) : person.officePhone != null )
            return false;
        if ( primaryUnit != null ? !primaryUnit.equals( person.primaryUnit ) : person.primaryUnit != null )
            return false;
        if ( secondaryUnit != null ? !secondaryUnit.equals( person.secondaryUnit ) : person.secondaryUnit != null )
            return false;
        if ( title != null ? !title.equals( person.title ) : person.title != null ) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = chineseName != null ? chineseName.hashCode() : 0;
        result = 31 * result + ( englishName != null ? englishName.hashCode() : 0 );
        result = 31 * result + ( title != null ? title.hashCode() : 0 );
        result = 31 * result + ( primaryUnit != null ? primaryUnit.hashCode() : 0 );
        result = 31 * result + ( secondaryUnit != null ? secondaryUnit.hashCode() : 0 );
        result = 31 * result + ( officePhone != null ? officePhone.hashCode() : 0 );
        return result;
    }

}
