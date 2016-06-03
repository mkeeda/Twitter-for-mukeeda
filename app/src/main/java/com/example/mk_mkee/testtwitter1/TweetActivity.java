package com.example.mk_mkee.testtwitter1;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import twitter4j.Twitter;
import twitter4j.TwitterException;

/**
 * Created by mk_mkee on 4/13/16.
 */
public class TweetActivity extends AppCompatActivity{

    private EditText mInputText;
    private Twitter mTwitter;

    @Override
    protected void onCreate(Bundle saveInstanceState){
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_tweet);

        mTwitter = TwitterUtils.getTwitterInstance(this);
        mInputText = (EditText)this.findViewById(R.id.input_text);

        findViewById(R.id.action_tweet).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tweet();
                finish();
            }
        });

    }

    private void tweet(){
        AsyncTask<String, Void, Boolean> task = new AsyncTask<String, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(String... params) {
                try{
                    mTwitter.updateStatus(params[0]);
                    return true;
                }
                catch (TwitterException e){
                    e.printStackTrace();
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean result){
                if(result){
                    showToast("ツイート成功");
                }
                else{
                    showToast("ツイート失敗");
                }
            }
        };
        task.execute(mInputText.getText().toString());
    }

    private void showToast(String text){
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }
}
