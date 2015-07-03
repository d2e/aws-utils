package com.autodesk.aws;

import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;

/**
 * Created by dt on 22/5/15.
 */
public class ParamModel {

    private String volId;
    private int retentionPeriod;
    private Region fromRegion;
    private Region toRegion;
    private String profileName;

    public ParamModel() {
    }

    public ParamModel(String volId, int retentionPeriod, String fromRegion, String toRegion) {


        this.volId = volId;
        this.retentionPeriod = retentionPeriod;
        this.fromRegion = Region.getRegion(Regions.fromName(fromRegion));
        this.toRegion = Region.getRegion(Regions.fromName(toRegion));

    }
    //Regions.fromName("us-east-1")
    //Region.getRegion(Regions.US_WEST_1)

    public String getProfileName() {
        return profileName;
    }

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public String getVolId() {
        return volId;
    }

    public void setVolId(String volId) {
        this.volId = volId;
    }

    public int getRetentionPeriod() {
        return retentionPeriod;
    }

    public void setRetentionPeriod(int retentionPeriod) {
        this.retentionPeriod = retentionPeriod;
    }

    public Region getFromRegion() {
        return fromRegion;
    }

    public void setFromRegion(String fromRegion) {
        this.fromRegion = Region.getRegion(Regions.fromName(fromRegion));
    }

    public Region getToRegion() {
        return toRegion;
    }

    public void setToRegion(String toRegion) {
        this.toRegion = Region.getRegion(Regions.fromName(toRegion));
    }

    @Override
    public String toString() {
        return "ParamModel{" +
                "volId='" + volId + '\'' +
                ", retentionPeriod=" + retentionPeriod +
                ", fromRegion='" + fromRegion + '\'' +
                ", toRegion='" + toRegion + '\'' +
                ", profileName='" + profileName + '\'' +
                '}';
    }


}
