package twitter;

import twitter4j.Status;
import util.ImageCache;

import java.util.*;

/**
 * Abstracts tweets source with filter capability
 */
public abstract class TwitterSource extends Observable { //Task 1: Make TwitterSource extend Observable
    protected boolean doLogging = true;
    // The set of terms to look for in the stream of tweets
    protected Set<String> terms = new HashSet<>();

    // Called each time a new set of filter terms has been established
    abstract protected void sync();

    protected void log(Status status) {
        if (doLogging) {
            System.out.println(status.getUser().getName() + ": " + status.getText());
        }
    }

    public void setFilterTerms(Collection<String> newterms) {
        terms.clear();
        terms.addAll(newterms);
        sync();
    }

    public List<String> getFilterTerms() {
        return new ArrayList<>(terms);
    }

    /**
     * This method is called each time a tweet is delivered to the application.
    * EFFECTS: Each active query should be informed about each incoming tweet so that
    *       it can determine whether the tweet should be displayed.
     *       Preloads profile photo from status url to cache
    */
    protected void handleTweet(Status status) {
        log(status);
        // loading profile photo to cache
        ImageCache.getInstance().loadImage(status.getUser().getProfileImageURL());

        setChanged();
        notifyObservers(status);
    }
}
