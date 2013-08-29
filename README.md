Android Client for Indicative's REST API

This REST client creates a JSON representation of your event and posts it to Indicative's Event endpoint.

Features:

+ No external dependencies, so you'll never have library conflicts.
+ Asynchronous, designed to never slow down or break your app.
+ Fault tolerent: if network connectivity is lost, events are queued and retried.

Sample usage:

    // In the onCreate() method of your Application class, call the launch() 
    // method, passing in the application context and your project's API key
    @Override
	public void onCreate() {
	    Indicative.launch(getApplicationContext(), "Your-API-Key-Goes-Here");
	}
    
    // Then start tracking events with the recordEvent() method
    Map<String, String> properties = new HashMap<String, String>();
    properties.put("Age", "23");
    properties.put("Gender", "Female");
    Indicative.recordEvent(context, "Registration", "user47", properties);

You should modify and extend this class to your heart's content.  If you make any changes please send a pull request!

As a best practice, consider adding a method that takes as a parameter the object representing your user, and adds certain default properties based on that user's characteristics (e.g., gender, age, etc.).

For more details, see our documentation at: http://www.indicative.com/docs/integration.html


