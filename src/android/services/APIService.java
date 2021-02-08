/**
 * APIService Object
 *
 * @copyright Center for Health Enhancement Systems Studies
 */
package edu.wisc.chess.plugins.chesslocationmonitoring.services;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import edu.wisc.chess.plugins.chesslocationmonitoring.helpers.Helpers;
import edu.wisc.chess.plugins.chesslocationmonitoring.helpers.SettingsDatabaseTableHelpers;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class APIService {
	/**
	 * Access token for passing data to the server
	 *
	 * @var String
	 */
	protected String access_token = "";

	/**
	 * Server's API url
	 *
	 * @var String
	 */
	protected String api_url = "";

	/**
	 * JSON MediaType
	 *
	 * @var MediaType
	 */
	protected static MediaType JSON = MediaType.parse("application/json; charset=utf-8");

	/**
	 * User agent for app
	 *
	 * @var String
	 */
	protected String user_agent = "";

	/**
	 * Constructor for the object
	 *
	 * @return void
	 */
	public APIService(Context context) {
		SettingsDatabaseTableHelpers settings_helper = new SettingsDatabaseTableHelpers(context);
		HashMap<String, String> settings = settings_helper.getSettings();

		if (settings.containsKey("api_url")) {
			this.api_url = settings.get("api_url");
		}

		if (settings.containsKey("access_token")) {
			this.access_token = settings.get("access_token");
		}

		if (settings.containsKey("user_agent")) {
			this.user_agent = settings.get("user_agent");
		}
	}

	/**
	 * Constructor for the object
	 *
	 * @param api_url			API Url to use
	 * @param access_token		Access token
	 *
	 * @return void
	 */
	public APIService(String api_url, String access_token) {
		this.api_url = api_url;
		this.access_token = access_token;
	}

	/**
	 * Make request to server
	 *
	 * @param method		Method to use when making request (GET, POST)
	 * @param url			URL to make request to
	 * @param data			JSON object of data
	 *
	 * @return void
	 */
	public Call makeRequest(String method, String url, JSONObject data, Callback callback) throws IOException, JSONException {
		if (data == null) {
			data = new JSONObject();
		}

		if (!url.startsWith("http") && !url.startsWith("file")) {
			url = this.api_url + url;
		}

		if (!data.has("token")) {
			data.put("token", this.access_token);
		}

		RequestBody body = RequestBody.create(JSON, data.toString());
		Request request = new Request.Builder()
			.url(url)
			.method(method, body)
			.build();

		if (!this.user_agent.equalsIgnoreCase("")) {
			request = new Request.Builder()
				.url(url)
				.header("User-Agent", this.user_agent)
				.method(method, body)
				.build();
		}

		Helpers.log("Request Url: " + request.url());
		Helpers.log("Request Body: " + data.toString());

		OkHttpClient client = new OkHttpClient();
		Call call = client.newCall(request);
		call.enqueue(callback);

		return call;
	}

	/**
	 * Set access token
	 *
	 * @param token			Token to set
	 *
	 * @return void
	 */
	public void setAccessToken(String token) {
		this.access_token = token;
	}
}
