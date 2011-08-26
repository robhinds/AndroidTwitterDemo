package com.tmm.android.twitter;

import java.util.ArrayList;

import org.json.JSONObject;

import twitter4j.Twitter;
import android.app.ListActivity;
import android.os.Bundle;

import com.tmm.android.twitter.appliaction.TwitterApplication;
import com.tmm.android.twitter.reader.TweetReader;

public class TweetsActivity extends ListActivity {
	
	private TwitterListAdapter adapter;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Twitter t = ((TwitterApplication)getApplication()).getTwitter();
		
		ArrayList<JSONObject> jobs = TweetReader.retrieveSpecificUsersTweets(t);
		
		adapter = new TwitterListAdapter(this,jobs);
		setListAdapter(adapter);
	}

}