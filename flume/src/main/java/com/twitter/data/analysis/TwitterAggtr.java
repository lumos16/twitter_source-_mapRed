package com.twitter.data.analysis;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.flume.Context;
import org.apache.flume.Event;
import org.apache.flume.EventDrivenSource;
import org.apache.flume.channel.ChannelProcessor;
import org.apache.flume.conf.Configurable;
import org.apache.flume.event.EventBuilder;
import org.apache.flume.source.AbstractSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vader.sentiment.analyzer.SentimentAnalyzer;
import com.vader.sentiment.util.ScoreType;

import twitter4j.FilterQuery;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterAggtr extends AbstractSource implements EventDrivenSource, Configurable {
	
	private static final Logger logger = LoggerFactory.getLogger(TwitterAggtr.class);
	
	private String consumerKey;
	private String consumerSecretKey;
	private String accessToken;
	private String accessTokenSecretKey;
	private String keyword;
	private String[] keywordLists;
	private TwitterStream streamer;
	
	public void configure(Context context) {
		consumerKey = context.getString(Constants.CONSUMER_KEY);
		consumerSecretKey = context.getString(Constants.CONSUMER_SECRET_KEY);
		accessToken = context.getString(Constants.ACCESSTOKEN_KEY);
		accessTokenSecretKey = context.getString(Constants.ACCESSTOKEN_SECRET_KEY);
	    keyword = context.getString(Constants.KEYWORDS);
	    if (keyword.trim().length() == 0) {
	    	keywordLists = new String[0];
	    } else {
	    	keywordLists = keyword.split(",");
	        for (int i = 0; i < keywordLists.length; i++) {
	        	keywordLists[i] = keywordLists[i].trim();
	        }
	    }

	    ConfigurationBuilder cb = new ConfigurationBuilder();
	    cb.setOAuthConsumerKey(consumerKey);
	    cb.setOAuthConsumerSecret(consumerSecretKey);
	    cb.setOAuthAccessToken(accessToken);
	    cb.setOAuthAccessTokenSecret(accessTokenSecretKey);
	    cb.setJSONStoreEnabled(true);
	    cb.setIncludeEntitiesEnabled(true);

	    streamer = new TwitterStreamFactory(cb.build()).getInstance();
	}
	
	@Override
	public void start() {
        final ChannelProcessor channel = getChannelProcessor();
        final Map<String, String> headers = new HashMap<String, String>();
        StatusListener listener = new StatusListener() {
            public void onStatus(Status status) {
                logger.debug(status.getUser().getScreenName() + ": " + status.getText());
                headers.put("timestamp", String.valueOf(status.getCreatedAt().getTime()));
                headers.put("place", String.valueOf(status.getPlace()));
				StringBuilder sb = new StringBuilder();
                SentimentAnalyzer sa;
				try {
					sa = new SentimentAnalyzer(status.getText());
					sa.analyze();
					Map<String, Float> result = sa.getPolarity();
					sb.append(result.get(ScoreType.COMPOUND)).append(Constants.DELIMITER).append(result.get(ScoreType.POSITIVE)).append(Constants.DELIMITER);
					sb.append(result.get(ScoreType.NEGATIVE)).append(Constants.DELIMITER).append(result.get(ScoreType.NEUTRAL));
				} catch (IOException e) {
					logger.error("IOException occured when analyzing the tweet: " + e.getMessage(),e);
				}
				Event event = EventBuilder.withBody(sb.toString().getBytes(), headers);
                channel.processEvent(event);
            }
            
            public void onException(Exception ex) {
            	logger.error("Exception occured while retreiving the tweet: " + ex.getMessage(),ex);
            }
            
			public void onStallWarning(StallWarning warning) {
				logger.warn("Server side queue is almost full: " + warning.getMessage() + "\\s"+ 
						warning.getPercentFull() + "\\s" + "for twitter: " + getName());
			}
			
			public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {}
            public void onTrackLimitationNotice(int numberOfLimitedStatuses) {}
            public void onScrubGeo(long userId, long upToStatusId) {}
        };
        streamer.addListener(listener);
        if (keywordLists.length == 0) {
            logger.info("Empty twitter input data for analysis...");
            return;
        }
        else {
            logger.info("Triggered tweets filtering");
            FilterQuery query = new FilterQuery().track(keywordLists).language(new String[]{"en"});
            streamer.filter(query);
        }
        super.start();
    }
	
	@Override
	public void stop() {
		logger.debug("Shutting down the Twitter stream for keyword search: " + keyword);
		streamer.shutdown();
		super.stop();
		logger.info("Twitter streaming source {} stopped. Metrics: {} ", getName());
	}
}