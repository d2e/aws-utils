package com.autodesk.aws;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.ec2.model.Snapshot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Created by dt on 21/5/15.
 */
public class BackupAction {


    private static final Logger log = LoggerFactory.getLogger(BackupAction.class);

    public static void main(String[] arr) throws Exception {

      
        final ParamModel params = new ParamModel();
        if (arr.length > 0) {
            params.setVolId(arr[0]);
            params.setRetentionPeriod(Integer.valueOf(arr[1]));
            params.setFromRegion(arr[2]);
            params.setToRegion(arr[3]);
            params.setProfileName(arr[4]);
        }


        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
        final String volume = params.getVolId();//"vol-f3bd30bf";
        final AutodeskAwsProvider provider = new AutodeskAwsProvider(params.getProfileName());//"profile tcad"
        ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(3);
        log.info("backup process started at " + new Date());
        final String snapshotId = provider.takeSnapshot(volume, params.getFromRegion());
        System.out.println(snapshotId);
        final AwsModel model = new AwsModel();
        model.setSnapId(snapshotId);
        Future<AwsModel> progressTask = scheduledExecutorService.submit(new Callable<AwsModel>() {

            public AwsModel call() throws Exception {
                boolean inProgress = true;
                while (inProgress) {
                    model.setIsCompleted(provider.isSnapshotCompleted(model.getSnapId()));
                    if (!model.getIsCompleted()) {
                        Thread.sleep(1000);// sleep for second

                    } else {
                        inProgress = false;
                    }

                }
                return model;
            }
        });
/**
 * wait for task to complete and then initiate geo mirroring of snapshot
 */
        if (progressTask.get().getIsCompleted()) {
            log.info("snapshot task done");
            Future<Boolean> copyTask = scheduledExecutorService.submit(new Callable<Boolean>() {

                public Boolean call() throws Exception {
                    log.info("copy data to us region");

                    /**
                     * There is no scope for error! if error occur then i am screwed
                     *
                     */
                    provider.copySnapshotToOtherRegion(model.getSnapId(), params.getFromRegion(), params.getToRegion(), volume + " epoch Time :" + new Date().getTime());
                    return Boolean.TRUE;

                }


            });

            try {


                List<Snapshot> snapshots1 = provider.getOlderSnap(params.getVolId() + "*", params.getRetentionPeriod(), params.getFromRegion());

                provider.deleteSnapshot(snapshots1, params.getFromRegion());
                List<Snapshot> snapshots2 = provider.getOlderSnap(params.getVolId() + "*", params.getRetentionPeriod(), params.getToRegion());
                provider.deleteSnapshot(snapshots2, params.getToRegion());
            }catch (AmazonServiceException e) {
                log.error("error while deleting old snapshot " , e);
                provider.disconnect();
            }


        }
        provider.disconnect();
        log.info("waiting for all task to complete .. ");

        log.info("Task finished at " + new Date());
        Thread.sleep(1000 * 30); //sleep for 30 second
        log.info("shutdown thread..");
        scheduledExecutorService.shutdown();
        ;
    }


    /**
     *
     */

}
