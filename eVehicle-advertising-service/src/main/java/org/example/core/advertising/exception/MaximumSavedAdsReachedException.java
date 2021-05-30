package org.example.core.advertising.exception;

public class MaximumSavedAdsReachedException extends Exception{

    public MaximumSavedAdsReachedException() {
        super();
    }

    public MaximumSavedAdsReachedException(String message) {
        super(message);
    }
}
