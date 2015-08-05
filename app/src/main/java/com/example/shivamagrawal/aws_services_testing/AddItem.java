package com.example.shivamagrawal.aws_services_testing;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;


public class AddItem extends AppCompatActivity {

    CognitoCachingCredentialsProvider cp;
    AmazonDynamoDBClient ddbClient;
    DynamoDBMapper mapper;
    boolean initialized = false;

    EditText authorInput;
    EditText titleInput;
    EditText isbnInput;
    RatingBar ratingInput;
    Button submitButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        authorInput = (EditText) findViewById(R.id.author);
        titleInput = (EditText) findViewById(R.id.title);
        isbnInput = (EditText) findViewById(R.id.isbn);
        ratingInput = (RatingBar) findViewById(R.id.rating);
        submitButton = (Button) findViewById(R.id.submit);

        submitButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (initialized && fieldsEmpty()) {
                    createBook();
                    clearFields();
                    makeToast("SUCCESS");
                } else {
                    makeToast("ERROR");
                }
            }
        });

        new AWSinitialize().execute();
    }

    private void createBook() {
        Book b = new Book();
        b.setAuthor(authorInput.getText().toString());
        b.setTitle(titleInput.getText().toString());
        b.setIsbn(isbnInput.getText().toString());
        b.setRating(ratingInput.getRating());
        putItem(b);
    }

    public void putItem(Book b) {
        new itemPut().execute(b);
    }

    private class itemPut extends AsyncTask<Book, Integer, Boolean> {
        protected Boolean doInBackground(Book... books) {
            for (Book b : books) {
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

    private boolean fieldsEmpty() {
        if (emptyField(authorInput) ||
                emptyField(titleInput) ||
                emptyField(isbnInput)) {
            return false;
        } else {
            return true;
        }
    }

    private boolean emptyField(EditText et) {
        return et.getText().toString().trim().length() == 0;
    }

    private void clearFields() {
        authorInput.setText("");
        titleInput.setText("");
        isbnInput.setText("");
        ratingInput.setRating(0);
    }

    private void makeToast(String message) {
        Context context = getApplicationContext();
        CharSequence text = message;
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }
}
