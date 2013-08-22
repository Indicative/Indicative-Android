Android
=======

Android Client for Indicative's REST API

Standalone client for Indicative's REST API.  Events are stored in SharedPreferences, then periodically sent to us in a background thread.

Sample usage: 

  Map<String, String> properties = new HashMap<String, String>();
  properties.put("Age", "23");
  properties.put("Gender", "Female");
  Indicative.recordEvent(context, "Registration", "user47", properties);
  
For more details, see our documentation at: http://staging.skunkalytics.com/docs/integration.html
