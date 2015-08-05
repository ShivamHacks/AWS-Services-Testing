package com.example.shivamagrawal.aws_services_testing;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;

import java.util.ArrayList;
import java.util.List;


public class FindItem extends AppCompatActivity {

    CognitoCachingCredentialsProvider cp;
    AmazonDynamoDBClient ddbClient;
    DynamoDBMapper mapper;
    boolean initialized = false;

    EditText searchInput;
    Button searchButton;
    TextView searchResult;
    RatingBar searchRating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_item);

        searchInput = (EditText) findViewById(R.id.search);
        searchButton = (Button) findViewById(R.id.query);
        searchResult = (TextView) findViewById(R.id.searchResult);
        searchRating = (RatingBar) findViewById(R.id.searchRating);

        searchButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (initialized && searchEmpty()) {
                    searchBook();
                    clearSearch();
                    makeToast("SUCCESS");
                } else {
                    makeToast("ERROR");
                }
            }
        });

        new AWSinitialize().execute();

    }

    private void searchBook() {
        Book b = getItem(searchInput.getText().toString());
        if (b != null) {
            searchResult.setText("Title: " + b.getTitle() + " , Author: " + b.getAuthor());
            searchRating.setRating(b.getRating());
        } else {
            searchResult.setText("No results found");
        }
    }

    public Book getItem(String isbn) {
        try {
            Book selectedBooks = new itemGet().execute(isbn).get().get(0);
            return selectedBooks;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private class itemGet extends AsyncTask<String, Integer, List<Book>> {
        protected List<Book> doInBackground(String... isbns) {
            List<Book> selectedBooks = new ArrayList<Book>();
            for (String isbn : isbns) {
                Book selectedBook = mapper.load(Book.class, isbn);
                selectedBooks.add(selectedBook);
            }
            return selectedBooks;
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

    private boolean searchEmpty() {
        if (emptyField(searchInput)) {
            return false;
        } else {
            return true;
        }
    }

    private boolean emptyField(EditText et) {
        return et.getText().toString().trim().length() == 0;
    }

    private void clearSearch() {
        searchInput.setText("");
    }

    private void makeToast(String message) {
        Context context = getApplicationContext();
        CharSequence text = message;
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

}
