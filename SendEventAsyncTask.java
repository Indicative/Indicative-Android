package com.indicative.client.java.android;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.util.Log;

public class SendEventAsyncTask extends AsyncTask<Void, Void, Integer> {

	private static final String API_ENDPOINT = "http://api.skunkalytics.com/service/event";

	private Context context;
	private String event;

	public SendEventAsyncTask(Context context, String event) {
		this.context = context;
		this.event = event;
	}

	@Override
	protected Integer doInBackground(Void... params) {
		if (Indicative.isDebug()) {
			Log.v("Indicative Async Task", new StringBuilder("Sending event: ")
					.append(event).toString());
		}

		int statusCode = 0;

		try {
			HttpParams httpParameters = new BasicHttpParams();
			HttpConnectionParams.setSocketBufferSize(httpParameters, 8192);

			HttpClient client = new DefaultHttpClient(httpParameters);
			HttpPost post = new HttpPost(API_ENDPOINT);

			post.setHeader("Content-Type", "application/json");

			post.setEntity(new StringEntity(event, "UTF-8"));

			HttpResponse resp = client.execute(post);

			statusCode = resp.getStatusLine().getStatusCode();

			if (Indicative.isDebug()) {
				Log.v("Indicative Status Code: ", Integer.toString(statusCode));
				Log.v("Indicative Status Reason: ", resp.getStatusLine()
						.getReasonPhrase());
				Log.d("Indicative Response Body: ", inputStreamToString(resp
						.getEntity().getContent()));
			}

			return statusCode;
		} catch (Exception e) {
			Log.v("Error in Indicative Async Task: ", e.getMessage(), e);
		}

		return 400;
	}

	@Override
	protected void onPostExecute(Integer result) {

		if (result != 0 && result != 408 && result != 500) {

			SharedPreferences prefs = context.getSharedPreferences(
					"indicative_events", Context.MODE_PRIVATE);
			int eventCount = prefs.getInt(event, 0);
			if (eventCount > 1) {
				prefs.edit().putInt(event, eventCount - 1).commit();
			} else {
				prefs.edit().remove(event).commit();
			}

		} else if (Indicative.isDebug()) {
			Log.v("Indicative Async Task: ", "Retriable error occured");
		}
	}

	private String inputStreamToString(InputStream is) {
		String line = "";
		StringBuilder total = new StringBuilder();

		BufferedReader rd = new BufferedReader(new InputStreamReader(is));

		try {
			while ((line = rd.readLine()) != null) {
				total.append(line);
			}
		} catch (IOException e) {
			Log.v("Indicative Async Task", e.getMessage(), e);
		}

		return total.toString();
	}
}
