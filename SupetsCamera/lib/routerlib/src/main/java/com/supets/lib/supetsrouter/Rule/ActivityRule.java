package com.supets.lib.supetsrouter.Rule;

import android.app.Activity;
import com.supets.lib.supetsrouter.Rule.exception.ActivityNotRouteException;

public class ActivityRule extends BaseIntentRule<Activity> {

    /** activity路由scheme*/
    public static final String ACTIVITY_SCHEME = "activity://";

    /**
     * {@inheritDoc}
     */
    @Override
    public void throwException(String pattern) {
        throw new ActivityNotRouteException(pattern);
    }

}
