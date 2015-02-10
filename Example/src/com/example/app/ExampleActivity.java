package com.example.app;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import com.indicative.client.java.android.Indicative;

import java.util.HashMap;
import java.util.Map;

public class ExampleActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Indicative.initialize(getApplicationContext(), "TEST-API-KEY");

        final Button addUniqueButton = (Button) findViewById(R.id.uniquebutton);
        addUniqueButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Indicative.setUniqueID("jackie@indicative.com");
            }
        });


        final Button clearUniqueButton = (Button) findViewById(R.id.clearuniquebutton);
        clearUniqueButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Indicative.clearUniqueID();
            }
        });


        final Button addPropStrButton = (Button) findViewById(R.id.addpropstrbutton);
        addPropStrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Indicative.addProperty("commonprop1", "string!");
            }
        });


        final Button addPropIntButton = (Button) findViewById(R.id.addpropintbutton);
        addPropIntButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Indicative.addProperty("commonprop2", 4);
            }
        });


        final Button addPropBoolButton = (Button) findViewById(R.id.addpropboolbutton);
        addPropBoolButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Indicative.addProperty("commonprop3", true);
            }
        });


        final Button buildEventALL = (Button) findViewById(R.id.buildnameidpropsbutton);
        buildEventALL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, Object> props = new HashMap<String, Object>();
                props.put("prop1", "propstr");
                props.put("prop2", 5);
                props.put("prop3", true);
                Indicative.buildEvent("buildall", "unique@unique.com", props);
            }
        });


        final Button buildEventNameId = (Button) findViewById(R.id.buildnameidbutton);
        buildEventNameId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("Indicative", "building event name id");
                Indicative.buildEvent("buildnameid", "id@id.com");
            }
        });

        final Button buildEventNameProps = (Button) findViewById(R.id.buildnamepropsbutton);
        buildEventNameProps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, Object> props = new HashMap<String, Object>();
                props.put("prop4", "propstr");
                props.put("prop5", 5);
                props.put("prop6", true);
                Indicative.buildEvent("buildnameid", props);
            }
        });

        final Button buildEventName = (Button) findViewById(R.id.buildnamebutton);
        buildEventName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Indicative.buildEvent("buildname");
            }
        });

        final Button removeProp = (Button) findViewById(R.id.removepropbutton);
        removeProp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Indicative.removeProperty("commonprop1");
            }
        });

        final Button removeAllProp = (Button) findViewById(R.id.removeallpropbutton);
        removeAllProp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Indicative.clearProperties();
            }
        });

        final Button addAllProps = (Button) findViewById(R.id.addallpropbutton);
        addAllProps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("commonprop1", "override");
                map.put("commonprop4", 43);
                map.put("commonprop5", "another");
                Indicative.addProperties(map);
            }
        });
    }
}
