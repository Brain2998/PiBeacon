package su.os3.pibeacon;


import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import org.altbeacon.beacon.Beacon;

import java.util.Date;
import java.util.Objects;

public class MyBeacon {

    private MainActivity mActivity;
    Date lastAdTime;
    public String uuid;
    public String distance;
    public String rssi;

    MyBeacon(final MainActivity activity, Beacon beacon){
        mActivity=activity;
        uuid=beacon.getId1().toString();
        distance=String.valueOf(beacon.getDistance());
        rssi=String.valueOf(beacon.getRssi());

        lastAdTime=new Date();
        timeToDestroy();
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mActivity.adapter.add(MyBeacon.this);
                if (uuid.contentEquals("fb0b57a2-8228-44cd-913a-94a122ba1206")){
                    AlertDialog.Builder alertBuilder=new AlertDialog.Builder(mActivity);
                    alertBuilder.setTitle("Notification").setMessage("IBeacon from RaspberryPi reached.");
                    alertBuilder.setNeutralButton("OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                    AlertDialog alert=alertBuilder.show();
                }
            }
        });

    }

    private void timeToDestroy(){
        Thread killer=new Thread() {
            public void run() {
                boolean isAlive=true;
                while (isAlive) {
                    if ((new Date()).getTime() - lastAdTime.getTime() > 5000) {
                        mActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mActivity.adapter.remove(MyBeacon.this);
                                mActivity.beaconMap.remove(uuid);
                            }
                        });
                        isAlive=false;
                    }
                }
            }
        };
        killer.setPriority(3);
        killer.start();
    }

}
