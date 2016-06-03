package com.example.mk_mkee.testtwitter1;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

public class TwitterOAuthActivity extends AppCompatActivity {

    private String mCallbackURL;
    private Twitter mTwitter;
    private RequestToken mRequestToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_twitter_oauth);

        mCallbackURL = getString(R.string.twitter_callback_url);
        mTwitter = TwitterUtils.getTwitterInstance(this);

        findViewById(R.id.twitter_start_oauth).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startAuthorise();
            }
        });
    }

    /**
     * OAuth認証開始
     *
     * @param listener
     */
    private void startAuthorise(){
        AsyncTask<Void, Void, String> task = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                try{
                    mRequestToken = mTwitter.getOAuthRequestToken(mCallbackURL);
                    Log.d("tag", "1111");
                    return mRequestToken.getAuthorizationURL();
                }catch (TwitterException e){
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(String url){
                if(url != null){
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                    Log.d("tag", "2222");
                }
                else{
                    //失敗
                }
            }
        };
        task.execute();
    }

    @Override
    public void onNewIntent(Intent intent){
        if(intent == null
                || intent.getData() == null
                || !intent.getData().toString().startsWith(mCallbackURL)){
            return;
        }

        Log.d("tag", "4444");
        String verifier = intent.getData().getQueryParameter("oauth_verifier");

        AsyncTask<String, Void, AccessToken> task = new AsyncTask<String, Void, AccessToken>() {
            @Override
            protected AccessToken doInBackground(String... params) {
                try{
                    Log.d("tag", "3333");
                    return mTwitter.getOAuthAccessToken(mRequestToken, params[0]);
                }
                catch (TwitterException e){
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(AccessToken accessToken){
                if(accessToken != null){
                    //認証成功
                    showToast("認証成功");
                    successOAuth(accessToken);
                }
                else{
                    //認証失敗
                    showToast("認証失敗");
                }
            }
        };
        task.execute(verifier);
    }

    private void successOAuth(AccessToken accessToken){
        TwitterUtils.storeAccessToken(this, accessToken);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void showToast(String text){
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }
}
