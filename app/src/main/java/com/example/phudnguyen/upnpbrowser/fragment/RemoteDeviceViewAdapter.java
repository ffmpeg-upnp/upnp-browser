package com.example.phudnguyen.upnpbrowser.fragment;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.phudnguyen.upnpbrowser.R;
import com.example.phudnguyen.upnpbrowser.fragment.RemoteDeviceFragment.OnListFragmentInteractionListener;

import org.fourthline.cling.model.meta.DeviceDetails;
import org.fourthline.cling.model.meta.RemoteDevice;

import java.util.List;

public class RemoteDeviceViewAdapter extends RecyclerView.Adapter<RemoteDeviceViewAdapter.ViewHolder> {

    private final List<RemoteDevice> mValues;
    private final OnListFragmentInteractionListener mListener;

    public RemoteDeviceViewAdapter(List<RemoteDevice> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.mItem = mValues.get(position);
        holder.mUUID.setText(mValues.get(position).getIdentity().getUdn().getIdentifierString());
        DeviceDetails details = mValues.get(position).getDetails();
        holder.mFriendlyName.setText(details.getFriendlyName());
        holder.mManufactureDetails.setText("Manufacture: " + details.getManufacturerDetails().getManufacturer());
        holder.mModelDetails.setText("Model: " + details.getModelDetails().getModelName() + " - " + details.getModelDetails().getModelNumber());


        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != mListener) {
                    mListener.onListFragmentInteraction(holder.mItem);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView mUUID;
        public final TextView mFriendlyName;
        public final TextView mManufactureDetails;
        public final TextView mModelDetails;
        public RemoteDevice mItem;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            mUUID = (TextView) view.findViewById(R.id.uuid);
            mFriendlyName = (TextView) view.findViewById(R.id.friendly_name);
            mManufactureDetails = (TextView) view.findViewById(R.id.manufacture_details);
            mModelDetails = (TextView) view.findViewById(R.id.model_details);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + mFriendlyName.getText() + "'";
        }
    }
}
