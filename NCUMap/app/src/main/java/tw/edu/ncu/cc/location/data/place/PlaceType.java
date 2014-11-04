package tw.edu.ncu.cc.location.data.place;

public enum PlaceType {

    WHEELCHAIR_RAMP, DISABLED_CAR_PARKING, DISABLED_MOTOR_PARKING, EMERGENCY,
    AED, RESTAURANT, SPORT_RECREATION, ADMINISTRATION, RESEARCH, DORMITORY, OTHER,
    TOILET, ATM, BUS_STATION, PARKING_LOT;

    public String value() {
        return name();
    }

    public static PlaceType fromValue( String string ) {
        return valueOf( string );
    }

}
