package com.indicative.client.java.android;

import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;


public class Event {
	
	private String projectId;
	private String eventName;
	private long eventTime;
	private String eventUniqueId;
	private Map<String, String> properties;
	
	public Event(String projectId, String eventName, String eventUniqueId, Map<String, String> properties){
		this.projectId = projectId;
		this.eventName = eventName;
		this.eventTime = System.currentTimeMillis();
		this.eventUniqueId = eventUniqueId;
		this.properties = properties;
	}
	
	public JSONObject getEventAsJSON(){
		JSONObject event = new JSONObject();
		
		try {
			event.put("projectId", projectId);
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