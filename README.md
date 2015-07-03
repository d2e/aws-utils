autodesk-cloud
==============

autodesk aws related utility

 * Supported functionality are as follows:-
 * Create snapshot from volume
 * Monitor snapshot status
 * Copy Snapshot from 1 region to another
 * delete snapshot if retention period has expired

command example


``` mvn   exec:java -Dexec.mainClass="com.autodesk.aws.BackupAction" -Dexec.args="vol-f3bd30bf 7 us-east-1 us-west-1 'profile tcad'" ```
