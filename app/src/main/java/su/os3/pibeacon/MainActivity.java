package su.os3.pibeacon;


import android.os.RemoteException;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.os.Bundle;
import android.util.Log;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity implements BeaconConsumer {

    private BeaconManager beaconManager;
    private RecyclerView recyclerView;
    public BeaconsAdapter adapter;
    public Map<String, MyBeacon> beaconMap=new HashMap<String, MyBeacon>();

    public static final String TAG= "BeaconTest";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActivityCompat.requestPermissions( this, new String[] {  android.Manifest.permission.ACCESS_FINE_LOCATION  },1);

        recyclerView=findViewById(R.id.rvBeacons);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        List<MyBeacon> beaconList=new ArrayList<>();
        adapter=new BeaconsAdapter(this, beaconList);
        recyclerView.setAdapter(adapter);

        beaconManager=BeaconManager.getInstanceForApplication(this);

        beaconManager.getBeaconParsers().add(new BeaconParser()
                .setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));

        beaconManager.getBeaconParsers().add(new BeaconParser()
                .setBeaconLayout("m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BeaconParser.EDDYSTONE_UID_LAYOUT));
        beaconManager.getBeaconParsers().add(new BeaconParser().setBeaconLayout(BeaconParser.EDDYSTONE_TLM_LAYOUT));
        beaconManager.getBeaconParsers().add(new BeaconParser(). setBeaconLayout(BeaconParser.EDDYSTONE_URL_LAYOUT));
        beaconManager.setRegionStatePeristenceEnabled(false);
        beaconManager.bind(this);
    }

    @Override
    public void onBeaconServiceConnect() {
        final Region region=new Region("allBeacons", null, null, null);

        beaconManager.setMonitorNotifier(new MonitorNotifier() {
            @Override
            public void didEnterRegion(final Region region) {
                try {
                    Log.d(TAG, "Did enter region");
                    beaconManager.startRangingBeaconsInRegion(region);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void didExitRegion(Region region) {
                try {
                    Log.d(TAG, "Did exit region");
                    beaconManager.stopMonitoringBeaconsInRegion(region);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void didDetermineStateForRegion(int i, Region region) {
                Log.d(TAG, "Test determine");
            }
        });

        beaconManager.setRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(final Collection<Beacon> beacons, Region region) {
                for (final Beacon beacon: beacons) {

                    Log.d(TAG, "distance: "+ beacon.getDistance()+ " UUID: "+ beacon.getId1());
                    //+" Major: "+ beacon.getId2()+" Minor: "+beacon.getId3());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String uuid=beacon.getId1().toString();
                            if (beaconMap.containsKey(uuid)) {
                                MyBeacon currBeacon=beaconMap.get(uuid);
                                int index=adapter.mData.indexOf(currBeacon);
                                adapter.remove(currBeacon);
                                currBeacon.lastAdTime=new Date();
                                currBeacon.distance=String.valueOf(beacon.getDistance());
                                currBeacon.rssi=String.valueOf(beacon.getRssi());
                                adapter.mData.add(index, currBeacon);
                                adapter.notifyDataSetChanged();
                            }
                            else {
                                MyBeacon myBeacon=new MyBeacon(MainActivity.this, beacon);
                                beaconMap.put(uuid, myBeacon);
                            }
                        }
                    });
                }
            }

        });


        try {
            beaconManager.startMonitoringBeaconsInRegion(region);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
    }
}
