Android
=======

Android Client for Indicative's REST API

Standalone client for Indicative's REST API.  Events are stored in SharedPreferences, then periodically sent to us in a background thread.  It has no external dependencies, so you'll never have library conflicts, and it should never slow down or break your app.  You should modify and extend this class to your heart's content.  As a best practice, consider adding a method that takes as a parameter the object representing the user, and adds certain default properties based on that user's characteristics (e.g., gender, age, etc.).

Sample usage: 

    Map<String, String> properties = new HashMap<String, String>();
    properties.put("Age", "23");
    properties.put("Gender", "Female");
    Indicative.recordEvent(context, "Registration", "user47", properties);
  
For more details, see our documentation at: http://www.indicative.com/docs/integration.html
