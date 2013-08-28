package com.indicative.client.java.android;

import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

/**
 * Object representing an Indicative Event
 */

public class Event {
	
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