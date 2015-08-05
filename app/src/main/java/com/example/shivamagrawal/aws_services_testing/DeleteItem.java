package com.example.shivamagrawal.aws_services_testing;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;


public class DeleteItem extends AppCompatActivity {

    CognitoCachingCredentialsProvider cp;
    AmazonDynamoDBClient ddbClient;
    DynamoDBMapper mapper;
    boolean initialized = false;

    EditText deleteObject;
    Button deleteButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_item);

        deleteObject = (EditText) findViewById(R.id.deleteObject);
        deleteButton = (Button) findViewById(R.id.deleteButton);

        deleteButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (initialized && emptyFields()) {

                    clearInputs();
                    makeToast("SUCCESS");
                } else {
                    makeToast("ERROR");
                }
            }
        });

        new AWSinitialize().execute();

    }

    private void deleteBook() {
        deleteItem(mapper.load(Book.class, deleteObject.getText().toString()));
    }

    public void deleteItem(Book b) {
        new itemDelete().execute(b);
    }

    private class itemDelete extends AsyncTask<Book, Integer, Boolean> {
        protected Boolean doInBackground(Book... books) {
            for (Book b : books) {
                mapper.delete(b);
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


    private boolean emptyFields() {
        if (emptyField(deleteObject)) {
            return false;
        } else {
            return true;
        }
    }

    private boolean emptyField(EditText et) {
        return et.getText().toString().trim().length() == 0;
    }

    private void clearInputs() {
        deleteObject.setText("");
    }

    private void makeToast(String message) {
        Context context = getApplicationContext();
        CharSequence text = message;
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }
}
