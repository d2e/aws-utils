package com.autodesk.aws;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.AmazonEC2Client;
import com.amazonaws.services.ec2.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by dt on 21/5/15.
 */
public class AutodeskAwsProvider {
    private AmazonEC2 ec2;

    private static final Logger log = LoggerFactory.getLogger(AutodeskAwsProvider.class);

    public AutodeskAwsProvider(String profileName) {
        authenticate(profileName);
    }

    /**
     *
     */
    private void authenticate(String profileName) {

        AWSCredentials credentials = new ProfileCredentialsProvider(profileName).getCredentials();

        this.ec2 = new AmazonEC2Client(credentials);
    }

    /**
     *
     */

    public String takeSnapshot(String volId, Region region) {
        String snapid = null;
        if (ec2 != null) {

            ec2.setRegion(region);
            List<String> volumeList = new ArrayList<String>();
            volumeList.add(volId);
            DescribeVolumesResult result = ec2.describeVolumes(new DescribeVolumesRequest(volumeList));
            if (result.getVolumes().size() > 0) {
                Volume vol = result.getVolumes().get(0); //get first snapshot
                CreateSnapshotResult snapResult = ec2.createSnapshot(new CreateSnapshotRequest(vol.getVolumeId(), vol.getVolumeId() + " Date in epoch :" + new Date().getTime()));
                snapid = snapResult.getSnapshot().getSnapshotId();
                log.info("snapshot was successfull ", snapResult);

            } else {

                // unable to find volume
                String error = "Unable to find " + volId + "in Region " + region.getName();
                log.error(error);
                throw new AwsException(error);
            }
        }
        return snapid;
    }

    /**
     * @param snapId
     * @param fromregion
     * @param toregion
     * @param identifier
     */
    public void copySnapshotToOtherRegion(String snapId, Region fromregion, Region toregion, String identifier) {

        log.info("copying snapshot from " + fromregion.getName() + " to " + toregion.getName());
        ec2.setRegion(toregion);
        CopySnapshotResult result = ec2.copySnapshot(new CopySnapshotRequest().withSourceSnapshotId(snapId).withDescription(identifier).withDestinationRegion(toregion.getName()).withSourceRegion(fromregion.getName()));

        log.info("snapshot id is " + result);


    }

    /**
     * @param region
     * @param identifier
     */
    public void deleteSnapshotByIdentifier(Region region, String identifier) {


    }

    /**
     * @param snapId
     * @return
     */
    public boolean isSnapshotCompleted(String snapId) {

        boolean status = false;
        DescribeSnapshotsResult savedSnap = ec2.describeSnapshots(new DescribeSnapshotsRequest().withSnapshotIds(snapId));
        Snapshot snapshot = savedSnap.getSnapshots().get(0);
        if (snapshot.getState().equalsIgnoreCase("completed")) {
            log.info("snap id  :" + snapshot);
            status = true;

        } else {


            log.info("snapshot is still in progress " + snapshot.getProgress() + "" + snapshot.getState() + snapshot);

        }

        return status;
    }

    /**
     * @param searchDesc
     */
    public List<Snapshot> getOlderSnap(String searchDesc, int NoOfDay, Region region) {
        List<Snapshot> oldSnapList = new ArrayList<Snapshot>();


        Calendar retentionPeriod = Calendar.getInstance();
        retentionPeriod.add(Calendar.DAY_OF_MONTH, -NoOfDay);
        log.info("expiry period of backup  " + retentionPeriod.getTime());


        ec2.setRegion(region);
        DescribeSnapshotsResult savedSnap = ec2.describeSnapshots(new DescribeSnapshotsRequest().withFilters(new Filter().withName("description").withValues(searchDesc)));
        for (Snapshot snapshot : savedSnap.getSnapshots()) {

            if (snapshot.getStartTime().before(retentionPeriod.getTime())) {

                oldSnapList.add(snapshot);
               log.info("snapshot to delete " + snapshot);
            }
        }

        return oldSnapList;
    }

    /**
     * @param snapshots
     * @param region
     */
    public void deleteSnapshot(List<Snapshot> snapshots, Region region) {

        ec2.setRegion(region);

        for (Snapshot snapshot : snapshots) {

            ec2.deleteSnapshot(new DeleteSnapshotRequest().withSnapshotId(snapshot.getSnapshotId()));
            log.info("deled snapshot with id {1}", snapshot.getSnapshotId());
        }


    }

    /**
     *
     */
    public void disconnect(){

        com.amazonaws.http.IdleConnectionReaper.shutdown();
        log.info("Killing Jvm");
        System.exit(0);
    }



}
