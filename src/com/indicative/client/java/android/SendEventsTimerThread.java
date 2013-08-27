package com.indicative.client.java.android;

import java.util.Map;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

public class SendEventsTimerThread extends Thread {
	private Context context;
	private Handler handler;
	
	SendEventsTimerThread(Context context, Handler handler) {
		this.context = context;
		this.handler = handler;
		setName("SendEventsTimer");
	}
	
	@Override
	public void run() {
		
		if(Indicative.isDebug()){
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
		
		if (Indicative.getInstance().isTimerThreadRunning()) {
			handler.postDelayed(this, 60000l);
		} else if (Indicative.isDebug()){
			Log.v("Indicative timer", "Stopping event posts...");
		}
	}
}
