package com.autodesk.aws;

/**
 * Created by dt on 21/5/15.
 */
public class AwsException extends RuntimeException {

    public AwsException (String errorMsg){

        super(errorMsg);
    }
}
