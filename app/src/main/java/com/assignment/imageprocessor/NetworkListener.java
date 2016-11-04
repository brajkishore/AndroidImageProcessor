package com.assignment.imageprocessor;


/**
 * Created by braj.kishore on 8/12/2016.
 */
public interface NetworkListener<S,T> {


     void onSuccess(S data);
    void onFailure(T data);
}
