package com.example.mk_mkee.testtwitter1;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.image.SmartImageView;

import java.util.List;

import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;

public class MainActivity extends AppCompatActivity {

    private ListView mListView;
    private TweetAdapter mAdapter;
    private Twitter mTwitter;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ツールバーをアクションバーとしてセット
        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        //認証してなければ認証画面へ
        if(!TwitterUtils.hasAccessToken(this)){
            Intent intent = new Intent(this, TwitterOAuthActivity.class);
            startActivity(intent);
            finish();
        }
        else{
            //SwipeRefreshLayoutの設定
            mSwipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipelayout);
            mSwipeRefreshLayout.setOnRefreshListener(mOnRefreshListener);
            mSwipeRefreshLayout.setColorSchemeResources(
                    R.color.swipe_color_1, R.color.swipe_color_2,
                    R.color.swipe_color_3, R.color.swipe_color_4);

            //ListViewの設定
            mListView = (ListView)this.findViewById(R.id.myList);
            mAdapter = new TweetAdapter(this);
            mListView.setAdapter(mAdapter);
            mTwitter = TwitterUtils.getTwitterInstance(this);
            reloadTimeLine();
        }

    }

    private SwipeRefreshLayout.OnRefreshListener mOnRefreshListener = new SwipeRefreshLayout.OnRefreshListener(){
        @Override
        public void onRefresh(){
            reloadTimeLine();
            mSwipeRefreshLayout.setRefreshing(false);
        }
    };
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        //sendボタン
        else if(id == R.id.send){
            Intent intent = new Intent(this, TweetActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private class TweetAdapter extends ArrayAdapter<twitter4j.Status>{
        private LayoutInflater mInflater;
        public TweetAdapter(Context context){
            super(context, android.R.layout.simple_list_item_1);
            mInflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        }

        //作ったカスタムビューを表示するように各要素をセットしてる
        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            if(convertView == null){
                convertView = mInflater.inflate(R.layout.list_item_tweet, null);
            }
            Status item = getItem(position);

            //むけえだ
            TextView name = (TextView)convertView.findViewById(R.id.name);
            name.setText(item.getUser().getName());
            //@mk_mkee
            TextView screenName = (TextView)convertView.findViewById(R.id.screen_name);
            screenName.setText("@" + item.getUser().getScreenName());
            //ツイート内容
            TextView tweetText = (TextView)convertView.findViewById(R.id.tweet_text);
            tweetText.setText(item.getText());
            //アイコン画像
            SmartImageView icon = (SmartImageView) convertView.findViewById(R.id.icon);
            icon.setImageUrl(item.getUser().getBiggerProfileImageURL());

            return convertView;
        }
    }

    private void reloadTimeLine(){
        AsyncTask<Void, Void, List<twitter4j.Status>> task = new AsyncTask<Void, Void, List<twitter4j.Status>>() {
            @Override
            protected List<twitter4j.Status> doInBackground(Void... params) {
                try{
                    ResponseList<twitter4j.Status> timeline = mTwitter.getHomeTimeline();
                    return timeline;
                }
                catch (TwitterException e){
                    e.printStackTrace();
                }
                return null;
            }

            //このメソッドはdoInBackgroundのあとに呼ばれる
            //doInBackgroundの戻り値がonPostExecuteの引数になる
            @Override
            protected void onPostExecute(List<twitter4j.Status> result){
                if(result != null){
                    mAdapter.clear();
                    for(twitter4j.Status status : result){
                        mAdapter.add(status);
                    }
                    //リストの先頭に移動
                    //getListView().setSelection(0);
                    mListView.setSelection(0);
                }
                else{
                    showToast("タイムラインの取得に失敗");
                }
            }
        };
        task.execute();
    }

    private void showToast(String text){
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }
}
