package su.os3.pibeacon;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class BeaconsAdapter extends RecyclerView.Adapter<BeaconsAdapter.ViewHolder>{
    List<MyBeacon> mData;
    private LayoutInflater mInflater;

    // data is passed into the constructor
    BeaconsAdapter(Context context, List<MyBeacon> data) {
        this.mInflater = LayoutInflater.from(context);
        this.mData = data;
    }

    // inflates the row layout from xml when needed
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.beacons_row, parent, false);
        return new ViewHolder(view);
    }

    // binds the data to the TextView in each row
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        MyBeacon beacon = mData.get(position);
        holder.uuidTextView.setText(beacon.uuid);
        holder.distanceTextView.setText(String.valueOf(beacon.distance));
        holder.rssiTextView.setText(beacon.rssi);
    }

    // total number of rows
    @Override
    public int getItemCount() {
        return mData.size();
    }


    // stores and recycles views as they are scrolled off screen
    public class ViewHolder extends RecyclerView.ViewHolder  {
        TextView uuidTextView;
        TextView distanceTextView;
        TextView rssiTextView;

        ViewHolder(View itemView) {
            super(itemView);
            uuidTextView = itemView.findViewById(R.id.tvBeaconUUID);
            distanceTextView = itemView.findViewById(R.id.tvBeaconDistance);
            rssiTextView = itemView.findViewById(R.id.tvBeaconRssi);
        }

    }


    public void add(MyBeacon item) {
        mData.add(item);
        notifyDataSetChanged();
    }

    public void remove(MyBeacon item) {
        mData.remove(item);
        notifyDataSetChanged();
    }
}
