package com.autodesk.aws;

/**
 * Created by dt on 21/5/15.
 */
public class AwsModel {


    private Boolean isCompleted;



    private String snapId;


    public String getSnapId() {
        return snapId;
    }

    public void setSnapId(String snapId) {
        this.snapId = snapId;
    }

    public Boolean  getIsCompleted() {
        return isCompleted;
    }

    public void setIsCompleted(Boolean isCompleted) {
        this.isCompleted = isCompleted;
    }

    @Override
    public String toString() {
        return "AwsModel{" +
                "isCompleted=" + isCompleted +
                ", snapId='" + snapId + '\'' +
                '}';
    }
}
