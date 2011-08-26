package com.tmm.android.twitter;


import oauth.signpost.OAuthProvider;
import oauth.signpost.basic.DefaultOAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.http.AccessToken;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.tmm.android.twitter.appliaction.TwitterApplication;
import com.tmm.android.twitter.util.Constants;



public class AuthActivity extends Activity {

	private Twitter twitter;
	private OAuthProvider provider;
	private CommonsHttpOAuthConsumer consumer;

	private String CONSUMER_KEY =           Constants.CONSUMER_KEY;
	private String CONSUMER_SECRET =        Constants.CONSUMER_SECRET;
	private String CALLBACK_URL =           "callback://tweeter";

	private Button buttonLogin;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		System.setProperty("http.keepAlive", "false");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_oauth);
		
		//check for saved log in details..
		checkForSavedLogin();

		//set consumer and provider on teh Application service
		getConsumerProvider();
		
		//Define login button and listener
		buttonLogin = (Button)findViewById(R.id.ButtonLogin);
		buttonLogin.setOnClickListener(new OnClickListener() {  
			public void onClick(View v) {
				askOAuth();
			}
		});
	}

	private void checkForSavedLogin() {
		// Get Access Token and persist it
		AccessToken a = getAccessToken();
		if (a==null) return;	//if there are no credentials stored then return to usual activity

		// initialize Twitter4J
		twitter = new TwitterFactory().getInstance();
		twitter.setOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
		twitter.setOAuthAccessToken(a);
		((TwitterApplication)getApplication()).setTwitter(twitter);
		
		startFirstActivity();
		finish();
	}

	/**
	 * Kick off the activity to display 
	 */
	private void startFirstActivity() {
		System.out.println("STARTING FIRST ACTIVITY!");
		Intent i = new Intent(this, TweetsActivity.class);
		startActivityForResult(i, Constants.ACTIVITY_LATEST_TWEETS);
	}

	/**
	 * This method checks the shared prefs to see if we have persisted a user token/secret
	 * if it has then it logs on using them, otherwise return null
	 * 
	 * @return AccessToken from persisted prefs
	 */
	private AccessToken getAccessToken() {
		SharedPreferences settings = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
		String token = settings.getString("accessTokenToken", "");
		String tokenSecret = settings.getString("accessTokenSecret", "");
		if (token!=null && tokenSecret!=null && !"".equals(tokenSecret) && !"".equals(token)){
			return new AccessToken(token, tokenSecret);
		}
		return null;
	}

	

	/**
	 * Open the browser and asks the user to authorize the app.
	 * Afterwards, we redirect the user back here!
	 */
	private void askOAuth() {
		try {
			consumer = new CommonsHttpOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
			provider = new DefaultOAuthProvider("http://twitter.com/oauth/request_token", "http://twitter.com/oauth/access_token", "http://twitter.com/oauth/authorize");
			String authUrl = provider.retrieveRequestToken(consumer, CALLBACK_URL);
			Toast.makeText(this, "Please authorize this app!", Toast.LENGTH_LONG).show();
			setConsumerProvider();
			startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(authUrl)));
		} catch (Exception e) {
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
		}
	}
	

	/**
	 * As soon as the user successfully authorized the app, we are notified
	 * here. Now we need to get the verifier from the callback URL, retrieve
	 * token and token_secret and feed them to twitter4j (as well as
	 * consumer key and secret).
	 */
	@Override
	protected void onResume() {
		super.onResume();
		System.out.println("RESUMING!!");
		if (this.getIntent()!=null && this.getIntent().getData()!=null){
			Uri uri = this.getIntent().getData();
			if (uri != null && uri.toString().startsWith(CALLBACK_URL)) {
				String verifier = uri.getQueryParameter(oauth.signpost.OAuth.OAUTH_VERIFIER);
				try {
					// this will populate token and token_secret in consumer
					provider.retrieveAccessToken(consumer, verifier);

					// Get Access Token and persist it
					AccessToken a = new AccessToken(consumer.getToken(), consumer.getTokenSecret());
					storeAccessToken(a);

					// initialize Twitter4J
					twitter = new TwitterFactory().getInstance();
					twitter.setOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
					twitter.setOAuthAccessToken(a);
					((TwitterApplication)getApplication()).setTwitter(twitter);
					//Log.e("Login", "Twitter Initialised");
					
					startFirstActivity();

				} catch (Exception e) {
					//Log.e(APP, e.getMessage());
					e.printStackTrace();
					Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
				}
			}
		}
	}

	/**
	 * This method persists the Access Token information so that a user
	 * is not required to re-login every time the app is used
	 * 
	 * @param a - the access token
	 */
	private void storeAccessToken(AccessToken a) {
		SharedPreferences settings = getSharedPreferences(Constants.PREFS_NAME, MODE_PRIVATE);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("accessTokenToken", a.getToken());
		editor.putString("accessTokenSecret", a.getTokenSecret());
		editor.commit();
	}
	
	
	/**
	 * Get the consumer and provider from the application service (in the case that the
	 * activity is restarted so the objects are not lost
	 */
	private void getConsumerProvider() {
		OAuthProvider p = ((TwitterApplication)getApplication()).getProvider();
		if (p!=null){
			provider = p;
		}
		CommonsHttpOAuthConsumer c = ((TwitterApplication)getApplication()).getConsumer();
		if (c!=null){
			consumer = c;
		}
	}
	
	
	/**
	 * Set the consumer and provider from the application service (in the case that the
	 * activity is restarted so the objects are not lost)
	 */
	private void setConsumerProvider() {
		if (provider!=null){
			((TwitterApplication)getApplication()).setProvider(provider);
		}
		if (consumer!=null){
			((TwitterApplication)getApplication()).setConsumer(consumer);
		}
	}

}
