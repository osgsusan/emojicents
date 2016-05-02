package osg.susan.android.emojicents;

import android.content.Context;
import android.preference.PreferenceManager;

/**
 * Created by susanosgood on 4/17/16.
 */
public class LimitPreferences {

    private static final String PREF_LIMIT = "spendingLimit";
    private static final String PREF_REMAINDER = "totalRemainder";

    public static float getStoredLimit(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getFloat(PREF_LIMIT,  0);
    }

    public static void setStoredLimit(Context context, float limit) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putFloat(PREF_LIMIT, limit)
                .apply();
    }

    public static float getStoredRemainder(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getFloat(PREF_REMAINDER, 0);
    }

    public static void setStoredRemainder(Context context, float remainder) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putFloat(PREF_REMAINDER, remainder)
                .apply();
    }
}
