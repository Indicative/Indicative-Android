package com.example.Indogative;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.indicative.client.java.android.Indicative;

import java.util.HashMap;
import java.util.Map;

public class IndogativeActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        //"43f0abdf-701e-4013-9557-ea0c146da70e" -> jackie+android@indicative.com
        Indicative.initialize(this.getApplicationContext(), "43f0abdf-701e-4013-9557-ea0c146da70e");

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
                Indicative.addProperty("common prop name str", "string!");
            }
        });


        final Button addPropIntButton = (Button) findViewById(R.id.addpropintbutton);
        addPropIntButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Indicative.addProperty("common prop name int", 4);
            }
        });


        final Button addPropBoolButton = (Button) findViewById(R.id.addpropboolbutton);
        addPropBoolButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Indicative.addProperty("common prop name bool", true);
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
    }
}
