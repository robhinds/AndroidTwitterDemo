package com.tmm.android.twitter.reader;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import android.util.Log;

import com.tmm.android.twitter.util.Utility;

public class TweetReader {
	
	private static final String screenName = "rob_hinds";

	
	/**
	 * a method to retrive a list of tweets from the users who the current user
	 * is following
	 * @return List JSON of other tweets
	 */
	public static ArrayList<JSONObject> retrieveSpecificUsersTweets(Twitter twitter){
		List<Status> statuses = new ArrayList<Status>();
	    try {
	    	Paging p = new Paging(1);	//get first page only of timeline - dont want to return everything!
			statuses = twitter.getFriendsTimeline(p);
		} catch (TwitterException e) {
			Log.e("Twitter", "Error retrieving tweets");
			Log.e("Twitter", e.getMessage());
		}

		return convertTimelineToJson(statuses); 
	}
	
	
	
	/**
	 * Method that converts a list of Status' to a JSON array that can
	 * be displayed by the grid view
	 * 
	 * @param statuses
	 * @return
	 */
	private static ArrayList<JSONObject> convertTimelineToJson(List<Status> statuses) {
		ArrayList<JSONObject> JOBS = new ArrayList<JSONObject>();
		try {
			if (statuses.size()>0){
				for (Status s : statuses){
					String avatar = "http://" + s.getUser().getProfileImageURL().getHost() + s.getUser().getProfileImageURL().getPath();
					JSONObject object = new JSONObject();
					object.put("tweet", s.getText());
					String timePosted = Utility.getDateDifference(s.getCreatedAt());
					object.put("tweetDate", timePosted);
					object.put("author", s.getUser().getName());
					object.put("avatar", avatar);
					object.put("userObj", s.getUser());
					object.put("tweetId", s.getId());
					
					JOBS.add(object);	
				}
			}else{
				JSONObject object = new JSONObject();
				object.put("tweet", "You have not logged in yet! Please log on to view latest tweets");
				object.put("author", "");
				JOBS.add(object);
			}

		} catch (JSONException e1) {
			Log.e("JSON", "There was an error creating the JSONObject", e1);
		}
		return JOBS;
	}
}
