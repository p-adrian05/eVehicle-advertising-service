package org.example.exceptions;

public class MaximumSavedAdsReachedException extends Exception{

    public MaximumSavedAdsReachedException() {
        super();
    }

    public MaximumSavedAdsReachedException(String message) {
        super(message);
    }
}
