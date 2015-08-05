package com.example.shivamagrawal.aws_services_testing;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    Spinner dynamoDBselector;
    Button dynamoDBlaunch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // dynamoDB stuff
        dynamoDBselector = (Spinner) findViewById(R.id.dynamoDBselector);
        String[] dbLaunches = new String[]{"Add Item", "Find Item", "Change Item", "Delete Item"};
        final Map<String, Class> DBlaunches = new HashMap<String, Class>();
        DBlaunches.put("Add Item", AddItem.class);
        DBlaunches.put("Find Item", FindItem.class);
        DBlaunches.put("Change Item", ChangeItem.class);
        DBlaunches.put("Delete Item", DeleteItem.class);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, dbLaunches);
        dynamoDBselector.setAdapter(adapter);
        dynamoDBlaunch = (Button) findViewById(R.id.dynamoDBlaunch);
        dynamoDBlaunch.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String chosenDBlaunch = String.valueOf(dynamoDBselector.getSelectedItem());
                Intent DBactivityLaunch = new Intent(getApplicationContext(), DBlaunches.get(chosenDBlaunch));
                startActivity(DBactivityLaunch);
            }
        });
    }

}
