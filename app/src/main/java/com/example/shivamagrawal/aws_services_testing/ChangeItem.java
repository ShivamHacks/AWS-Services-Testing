package com.example.shivamagrawal.aws_services_testing;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;

import java.util.ArrayList;
import java.util.List;


public class ChangeItem extends AppCompatActivity {

    CognitoCachingCredentialsProvider cp;
    AmazonDynamoDBClient ddbClient;
    DynamoDBMapper mapper;
    boolean initialized = false;

    EditText updateObject;
    Spinner updateAttribute;
    EditText updateValue;
    Button updateButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_item);

        updateObject = (EditText) findViewById(R.id.updateObject);
        updateAttribute = (Spinner) findViewById(R.id.updateAttribute);
        updateValue = (EditText) findViewById(R.id.updateValue);
        updateButton = (Button) findViewById(R.id.updateSubmit);

        List<String> list = new ArrayList<String>();
        list.add("Title");
        list.add("Author");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, list);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        updateAttribute.setAdapter(dataAdapter);

        updateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (initialized && updateEmpty()) {
                    updateBook();
                    clearUpdates();
                    makeToast("SUCCESS");
                } else {
                    makeToast("ERROR");
                }
            }
        });

        new AWSinitialize().execute();

    }

    private void updateBook() {
        updateItem(updateObject.getText().toString(), String.valueOf(updateAttribute.getSelectedItem()), updateValue.getText().toString());
    }

    public void updateItem(String isbn, String attribute, String value) {
        UpdateParam params = new UpdateParam(isbn, attribute, value);
        new itemUpdate().execute(params);
    }

    private class UpdateParam {
        String isbn;
        String attribute;
        String value;

        public UpdateParam(String isbn, String attribute, String value) {
            this.isbn = isbn;
            this.attribute = attribute;
            this.value = value;
        }
    }

    private class itemUpdate extends AsyncTask<UpdateParam, Integer, Boolean> {
        protected Boolean doInBackground(UpdateParam... params) {
            for (UpdateParam p : params) {
                String isbn = p.isbn;
                String attribute = p.attribute;
                String value = p.value;
                Book b = mapper.load(Book.class, isbn);
                if (attribute == "Title") {
                    b.setTitle(value);
                } else if (attribute == "Author") {
                    b.setAuthor(value);
                }
                mapper.save(b);
            }
            return true;
        }
    }

    private class AWSinitialize extends AsyncTask<Void, Integer, Boolean> {
        protected Boolean doInBackground(Void... params) {
            String identityPoolID = getString(R.string.aws_identity_pool_id);
            cp = new CognitoCachingCredentialsProvider(
                    getApplicationContext(),
                    identityPoolID, // Identity Pool ID
                    Regions.US_EAST_1 // Region
            );
            ddbClient = new AmazonDynamoDBClient(cp);
            mapper = new DynamoDBMapper(ddbClient);
            return true;
        }

        protected void onPostExecute(Boolean result) {
            initialized = result;
        }
    }

    // Other stuff

    private boolean updateEmpty() {
        if (emptyField(updateObject) || emptyField(updateValue)) {
            return false;
        } else {
            return true;
        }
    }

    private boolean emptyField(EditText et) {
        return et.getText().toString().trim().length() == 0;
    }

    private void clearUpdates() {
        updateObject.setText("");
        updateValue.setText("");
    }

    private void makeToast(String message) {
        Context context = getApplicationContext();
        CharSequence text = message;
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }
}
