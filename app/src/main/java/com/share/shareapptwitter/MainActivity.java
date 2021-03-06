package com.share.shareapptwitter;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.twitter.sdk.android.core.models.Media;
import com.twitter.sdk.android.core.services.MediaService;
import com.twitter.sdk.android.tweetcomposer.TweetComposer;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import io.fabric.sdk.android.Fabric;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Response;

public class MainActivity extends Activity {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "S3PN2l06fLywzEAY4xvq4KEci";
    private static final String TWITTER_SECRET = "W0e1auhRqJukaxSfZbWFxcCEIQpediGyOjnCeX7PdqmuiU4vlp";
    private TwitterLoginButton loginButton;
    TwitterSession session;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));
        setContentView(R.layout.activity_main);
        loginButton = (TwitterLoginButton) findViewById(R.id.twitter_login_button);
        loginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                // The TwitterSession is also available through:
                // Twitter.getInstance().core.getSessionManager().getActiveSession()
                 session = result.data;
                // TODO: Remove toast and use the TwitterSession's userID
                // with your app's user model
                String msg = "@" + session.getUserName() + " logged in! (#" + session.getUserId() + ")";
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
                new tt().execute();

            }
            @Override
            public void failure(TwitterException exception) {
                Log.d("TwitterKit", "Login with Twitter failure", exception);
            }
        });

    }
    class tt extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            uploadImage("asdh", session);
            return null;
        }
    }
    private void uploadImage( final String tweettext, TwitterSession twitterSession){
        TweetComposer composer = new TweetComposer();
        
        File f = null;
        //try {
            f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "home.jpg");
            if (!f.exists()) {
                // f.createNewFile();
            }
//id is some like R.drawable.b_image
            /*InputStream inputStream = getResources().getAssets().open("home.jpg");
            OutputStream out=new FileOutputStream(f);
            byte buf[]=new byte[1024];
            int len;
            while((len=inputStream.read(buf))>0)
                out.write(buf,0,len);
            out.close();
            inputStream.close();
        }*/
        /*}
        catch (IOException e){
           e.printStackTrace();
            Log.e("exc", "ex"+e.getMessage()+ "stack:");
            Toast.makeText(getApplicationContext(), "Firt io", Toast.LENGTH_LONG).show();
        } */

    Uri uri = Uri.parse("android.resource://com.share.shareapptwitter/drawable/home.jpg");
        //File f= new File(uri.getPath());
        File imagefile =  new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + "home.jpg");
        /*try{
            InputStream inputStream = getResources().getAssets().open("myfoldername/myfilename");
        } catch (IOException e) {
            e.printStackTrace();
        } */
        //TypedFile typedFile = new TypedFile("image/*", imagefile);
        RequestBody file = RequestBody.create(MediaType.parse("image/*"), f);
        TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient();
        MediaService mediaservice = twitterApiClient.getMediaService();
        Call<Media> mediaCall=  mediaservice.upload(file, null, null);
        try {
           Response<Media> response =  mediaCall.execute();
            if(response.isSuccessful()){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "posted", Toast.LENGTH_LONG).show();
                    }
                });

            }
        } catch (IOException e) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "not posted io", Toast.LENGTH_LONG).show();
                }
            });

            e.printStackTrace();
        }

    }
    public void makeTweet(){
        TwitterApiClient twitterApiClient = TwitterCore.getInstance().getApiClient();
        MediaService mediaService = twitterApiClient.getMediaService();
        //mediaService.upload()

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Make sure that the loginButton hears the result from any
        // Activity that it triggered.
        loginButton.onActivityResult(requestCode, resultCode, data);
    }

}
