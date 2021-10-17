package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.github.scribejava.apis.TwitterApi;

import org.json.JSONException;
import org.parceler.Parcels;

import okhttp3.Headers;

public class ComposeActivity extends AppCompatActivity {

    EditText etCompose;
    Button btnTweet;
    TextView tvCount;

    TwitterClient client;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        etCompose = findViewById(R.id.etCompose);
        btnTweet = findViewById(R.id.btnTweet);
        tvCount = findViewById(R.id.tvCount);
        client =  TwitterApp.getRestClient(this);

        tvCount.setText(String.valueOf(280));
        etCompose.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Fires right as the text is being changed (even supplies the range of text)
                Log.d("count", etCompose.getText().toString().length() + "");
                String temp = (280 - etCompose.getText().toString().length()) + "";
                tvCount.setText(temp);
                if(280 - etCompose.getText().toString().length() < 0) {
                    tvCount.setTextColor(Color.RED);
                }
                else {
                    tvCount.setTextColor(Color.BLACK);
                }


            }

            @Override
            public void afterTextChanged(Editable s) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // Fires right before text is changing
            }

        });


        btnTweet.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                String tweetContent = etCompose.getText().toString();
                if(tweetContent.isEmpty()) {
                    Toast.makeText(ComposeActivity.this, "sorry, ur tweet cannot be empty.", Toast.LENGTH_SHORT).show();
                    Log.d("dbby", "short");

                    return;
                }
                if(tweetContent.length() > 280) {
                    Toast.makeText(ComposeActivity.this, "tweet too long", Toast.LENGTH_SHORT).show();
                    Log.d("dbby", "long");

                    return;
                }
                Toast.makeText(ComposeActivity.this,tweetContent , Toast.LENGTH_SHORT).show();
                Log.d("dbby", tweetContent);

                client.publishTweet(tweetContent, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        Log.i("ComposeActivity", "published tweet");
                        try {
                            Tweet tweet = Tweet.fromJson(json.jsonObject);
                            Log.i("ComposeActivity", "published tweet says: " + tweet.body);
                            Intent intent = new Intent();
                            intent.putExtra("tweet", Parcels.wrap(tweet));
                            setResult(RESULT_OK,intent);
                            finish();

                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Log.e("ComposeActivity", "on fail to push tweet", throwable);
                    }
                });

            }
        });
    }
}