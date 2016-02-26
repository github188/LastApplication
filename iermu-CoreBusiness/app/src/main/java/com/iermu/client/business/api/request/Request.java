package com.iermu.client.business.api.request;

import org.json.JSONException;
import org.json.JSONObject;

public abstract class Request {

	public abstract JSONObject toJson() throws JSONException;

}
