package com.iermu.client.business.api.request;

import org.json.JSONArray;
import org.json.JSONException;

public abstract class Requests {

	public abstract JSONArray toJson() throws JSONException;

}
