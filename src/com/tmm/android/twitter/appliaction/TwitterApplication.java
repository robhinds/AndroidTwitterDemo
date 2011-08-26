/**
 * 
 */
package com.tmm.android.twitter.appliaction;


import oauth.signpost.OAuthProvider;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import twitter4j.Twitter;
import android.app.Application;

/**
 * @author rob
 *
 */
public class TwitterApplication extends Application{
	
	
	private Twitter twitter;
	/**
	 * @return the twitter
	 */
	public Twitter getTwitter() {
		return twitter;
	}

	/**
	 * @param twitter the twitter to set
	 */
	public void setTwitter(Twitter twitter) {
		this.twitter = twitter;
	}

	private OAuthProvider provider;
	private CommonsHttpOAuthConsumer consumer;
	

	/**
	 * @param provider the provider to set
	 */
	public void setProvider(OAuthProvider provider) {
		this.provider = provider;
	}

	/**
	 * @return the provider
	 */
	public OAuthProvider getProvider() {
		return provider;
	}

	/**
	 * @param consumer the consumer to set
	 */
	public void setConsumer(CommonsHttpOAuthConsumer consumer) {
		this.consumer = consumer;
	}

	/**
	 * @return the consumer
	 */
	public CommonsHttpOAuthConsumer getConsumer() {
		return consumer;
	}


}
