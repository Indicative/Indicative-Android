package com.indicative.client.java.android;

/*
 * Standalone client for Indicative's REST API.  Events are stored in SharedPreferences, 
 * then periodically sent to us in a background thread (SendEventsTimerThread).  
 * 
 */

import java.util.Map;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;

public class Indicative {

	private static Indicative instance;
	private static final boolean debug = false;

	private Context context;
	private String apiKey;
	private boolean timerThreadRunning = true;

	private Indicative() {
	}

	public static Indicative getInstance() {
		if (instance == null) {
			instance = new Indicative();
		}
		return instance;
	}

	public static Indicative launch(Context context, String apiKey) {
		Indicative instance = getInstance();
		instance.setApiKey(apiKey);
		instance.setContext(context);

		instance.scheduleEventsTimer();

		return instance;
	}

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
	
	private static synchronized void addEventToSharedPrefs(Context context, String jsonObj){
		SharedPreferences prefs = context.getSharedPreferences(
				"indicative_events", Context.MODE_PRIVATE);
		int eventCount = prefs.getInt(jsonObj, 0);
		prefs.edit().putInt(jsonObj, eventCount + 1).commit();
	}
}
