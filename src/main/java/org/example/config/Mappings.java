package org.example.config;

public class Mappings {

    private Mappings(){}

    public static final String RATE = "rate";
    public static final String RATES = "rates";
    public static final String COUNT = "count";
    public static final String RATE_COUNT = RATES+"/"+COUNT;

    public static final String MESSAGES = "messages";
    public static final String MESSAGE = "message";
    public static final String PARTNERS = "partners";
    public static final String MESSAGES_PARTNERS = MESSAGES+"/"+PARTNERS;
    public static final String NEW = "new";
    public static final String MESSAGES_NEW_COUNT = MESSAGES+"/"+NEW+"/"+COUNT;

    public static final String ADVERTISEMENT = "advertisement";
    public static final String ADVERTISEMENTS = "advertisements";
    public static final String DETAILS = "details";
    public static final String IMG = "img";
    public static final String SAVED = "saved";
    public static final String BRANDS = "brands";
    public static final String BRAND = "brand";
    public static final String TYPES = "types";

    public static final String USER = "user";
    public static final String ROLES = "roles";
    public static final String USER_SAVED_AD = USER+"/"+ADVERTISEMENT+"/"+SAVED;
    public static final String USER_ROLES = USER+"/"+ROLES;
    public static final String USER_DETAILS = USER+"/"+DETAILS;

    public static final String CATEGORIES = "categories";


}
