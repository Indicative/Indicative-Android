package com.indicative.client.java.android;

import java.util.Map;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;

/**
 * Standalone client for Indicative's REST API.  Events are stored in SharedPreferences, 
 * then periodically sent to us in a background thread (SendEventsTimerThread).  
 */

public class Indicative {

	private static Indicative instance;
	
	//Enable this to see some basic logging.
	private static final boolean debug = false;

	private Context context;
	private String apiKey;
	private boolean timerThreadRunning = true;

	private Indicative() {
	}

	/**
	 * Instantiates the static Indicative instance (if that hasn't already happened), and returns it.
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
		instance.setApiKey(apiKey);
		instance.setContext(context);

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

	public void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}

	public String getApiKey() {
		return apiKey;
	}

	public void setContext(Context context) {
		this.context = context;
	}

	public Context getContext() {
		return context;
	}

	public void setTimerThreadRunning(boolean timerThreadRunning) {
		this.timerThreadRunning = timerThreadRunning;
	}

	public boolean isTimerThreadRunning() {
		return timerThreadRunning;
	}

	public static boolean isDebug() {
		return debug;
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
		Event event = new Event(getInstance().getApiKey(), eventName, uniqueId,
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
}
