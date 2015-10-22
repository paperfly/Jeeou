package com.paperfly.instantjio.util;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class Constants {
    public static final String TAG = Constants.class.getCanonicalName();
    public static final String EVENT_OBJECT = TAG + ".EVENT_OBJECT";
    public static final String EVENT_KEY = TAG + ".EVENT_KEY";
    public static final String GROUP_OBJECT = TAG + ".GROUP_OBJECT";
    public static final String GROUP_KEY = TAG + ".GROUP_KEY";
    public static final String CHOOSING_MODE = TAG + ".CHOOSING_MODE";
    public static final String CHOSEN_GROUPS = TAG + ".CHOSEN_GROUPS";
    public static final String CHOSEN_CONTACTS = TAG + ".CHOSEN_CONTACTS";
    public static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("EEE, MMM dd, yyyy", Locale.getDefault());
    public static final SimpleDateFormat TIME_FORMATTER = new SimpleDateFormat("hh:mm a", Locale.getDefault());
    public static final SimpleDateFormat DATE_TIME_FORMATTER = new SimpleDateFormat("EEE, MMM dd, yyyy hh:mm a", Locale.getDefault());
}
