package tw.edu.ncu.cc.location.data.place;

public enum PlaceType {

    WHEELCHAIR_RAMP              ("無障礙坡道",      false),
    DISABLED_CAR_PARKING        ("無障礙汽車位",    false),
    DISABLED_MOTOR_PARKING     ("無障礙機車位",    false),
    EMERGENCY                      ("緊急",            false),
    AED                               ("AED",            false),
    RESTAURANT                      ("餐廳",           true),
    SPORT_RECREATION              ("休閒生活",       true),
    ADMINISTRATION                 ("行政服務",       true),
    RESEARCH                        ("教學研究",       true),
    DORMITORY                      ("宿舍",            true),
    OTHER                            ("其他單位",       true),
    TOILET                            ("廁所",           false),
    ATM                               ("提款機",         false),
    BUS_STATION                     ("公車站牌",       false),
    PARKING_LOT                     ("停車場",         false);

    private String cName;
    private boolean isNeed;

    PlaceType (String cName, boolean isNeed) {
        this.cName = cName;
        this.isNeed = isNeed;
    }

    public String value() {
        return name();
    }

    public static PlaceType fromValue( String string ) {
        return valueOf( string );
    }

    public String getCName() {
        return this.cName;
    }

    public boolean isNeed() {
        return this.isNeed;
    }
}
