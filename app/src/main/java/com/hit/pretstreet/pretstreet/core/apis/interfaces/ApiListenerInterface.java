package com.hit.pretstreet.pretstreet.core.apis.interfaces;

import org.json.JSONObject;

/**
 * Created by User on 7/6/2017.
 */

public interface ApiListenerInterface {
    void onResponse(JSONObject response);
    void onError(String error);
}
