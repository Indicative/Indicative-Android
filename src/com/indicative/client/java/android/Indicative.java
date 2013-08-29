package com.indicative.client.java.android;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;

/**
 * Standalone client for Indicative's REST API.  Events are stored in SharedPreferences, 
 * then periodically sent to us in a background thread (SendEventsTimerThread).  
 */

public class Indicative {

	private static Indicative instance;
	
	// Enable this to see some basic logging.
	private static final boolean debug = false;
	
	// How long to wait before sending each batch of events. 
	private static final int SEND_EVENTS_TIMER_SECONDS = 60;

	private Context context;
	private String apiKey;

	private Indicative() {
	}

	/**
	 * Instantiates the static Indicative instance and returns it.
	 * 
	 * @return 	The static Indicative instance
	 */
	public static Indicative getInstance() {
		if (instance == null) {
			instance = new Indicative();
		}
		return instance;
	}

	/**
	 * Initializes the static Indicative instance with the project's API Key.
	 * 
	 * @param context	The app context
	 * @param apiKey	Your project's API Key
	 * 
	 * @return 	The static Indicative instance
	 */
	public static Indicative launch(Context context, String apiKey) {
		Indicative instance = getInstance();
		instance.apiKey = apiKey;
		instance.context = context;

		instance.scheduleEventsTimer();

		return instance;
	}

	/**
	 * Schedules the timer to periodically send events (once every 60 seconds by default)
	 */
	public void scheduleEventsTimer() {
		Handler handler = new Handler();
		handler.post(new SendEventsTimerThread(context, handler));
	}

	/**
	 * Creates an Event object and adds it to SharedPreferences.
	 * 
	 * @param context		The app context
	 * @param eventName		The name of your event
	 * @param uniqueId		A unique identifier for the user associated with the event
	 * @param properties	A Map of property names and values
	 */
	public static void recordEvent(Context context,
			String eventName, String uniqueId, Map<String, String> properties) {
		Event event = new Event(getInstance().apiKey, eventName, uniqueId,
				properties);
		String jsonObj = event.getEventAsJSON().toString();

		addEventToSharedPrefs(context, jsonObj);

		if (debug) {
			Log.v("Indicative",
					new StringBuilder("Recorded event: ").append(jsonObj)
							.toString());
		}
	}
	
	/**
	 * Adds the Event object to SharedPreferences
	 * 
	 * @param context		The app context
	 * @param jsonObj		A JSON representation of the event
	 */
	private static synchronized void addEventToSharedPrefs(Context context, String jsonObj){
		SharedPreferences prefs = context.getSharedPreferences(
				"indicative_events", Context.MODE_PRIVATE);
		int eventCount = prefs.getInt(jsonObj, 0);
		prefs.edit().putInt(jsonObj, eventCount + 1).commit();
	}
	
	/**
	 * Object representing an Indicative Event
	 */
	public static class Event {
		
		private String apiKey;
		private String eventName;
		private long eventTime;
		private String eventUniqueId;
		private Map<String, String> properties;
		
		/**
		 * Basic constructor.
		 * 
		 * @param apiKey			Your project's API key
		 * @param eventName			The name of your event
		 * @param eventUniqueId		A unique identifier for the user associated with the event
		 * @param properties		A Map of property names and values
		 */
		public Event(String apiKey, String eventName, String eventUniqueId, Map<String, String> properties){
			this.apiKey = apiKey;
			this.eventName = eventName;
			this.eventTime = System.currentTimeMillis();
			this.eventUniqueId = eventUniqueId;
			this.properties = properties;
		}
		
		/**
		 * Creates a JSON representation of the Event.
		 */
		public JSONObject getEventAsJSON(){
			JSONObject event = new JSONObject();
			
			try {
				event.put("apiKey", apiKey);
				event.put("eventName", eventName);
				event.put("eventTime", eventTime);
				event.put("eventUniqueId", eventUniqueId);
				if(properties != null && !properties.isEmpty()){
					JSONObject propsJson = new JSONObject();
					for(Entry<String, String> entry : properties.entrySet()){
						propsJson.put(entry.getKey(), entry.getValue());
					}
					event.put("properties", propsJson);
				}
			} catch (JSONException e) {
				Log.v("Indicative Event", e.getMessage(), e.fillInStackTrace());
			}
			
			return event;
		}
	}
	
	/**
	 * Scheduled timer to periodically send Events to the Indicative API endpoint.
	 */

	public class SendEventsTimerThread extends Thread {
		private Context context;
		private Handler handler;
		
		SendEventsTimerThread(Context context, Handler handler) {
			this.context = context;
			this.handler = handler;
			setName("SendEventsTimer");
		}
		
		/**
		 * For each Event in SharedPreferences, this executes an AsyncTask to send it to the Indicative API endpoint.
		 */
		@Override
		public void run() {
			
			if(debug){
				Log.v("Indicative Timer", "Running send events timer");
			}
			
			Map<String, ?> events = context.getSharedPreferences("indicative_events", Context.MODE_PRIVATE).getAll();
			
			if(events != null && !events.isEmpty()){
				for (String event : events.keySet()) {
					for(int i = 0 ; i < (Integer)events.get(event) ; i++) {
						new SendEventAsyncTask(context, event).execute();
					}
				}
			}
			
			handler.postDelayed(this, SEND_EVENTS_TIMER_SECONDS * 1000l);
		}
	}
	
	/**
	 * Async Task to send Events to the Indicative API endpoint.
	 */

	public class SendEventAsyncTask extends AsyncTask<Void, Void, Integer> {

		private static final String API_ENDPOINT = "https://api.indicative.com/service/event";

		private Context context;
		private String event;

		public SendEventAsyncTask(Context context, String event) {
			this.context = context;
			this.event = event;
		}
		
		/**
		 * Initializes the static Indicative instance with the project's API Key.
		 * 
		 * @param params	Not used
		 * 
		 * @return 			The status code returned by Indicative
		 */
		@Override
		protected Integer doInBackground(Void... params) {
			if (debug) {
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

				if (debug) {
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

		/**
		 * Removes the Event from SharedPreferences if it was posted successfully, 
		 * or if it received a response indicating a non-retriable error.
		 */
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

			} else if (debug) {
				Log.v("Indicative Async Task: ", "Retriable error occured");
			}
		}

		/**
		 * Converts the response's input stream to a String for readability.
		 * 
		 * @param is	The InputStream
		 * 
		 * @return 		A String representation of the InputStream
		 */
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
}
