package com.example.phudnguyen.upnpbrowser.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.phudnguyen.upnpbrowser.R;
import com.example.phudnguyen.upnpbrowser.service.UpnpServiceProvider;

import org.fourthline.cling.android.AndroidUpnpService;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.registry.Registry;
import org.fourthline.cling.registry.RegistryListener;

import java.util.ArrayList;

public class RemoteDeviceFragment extends Fragment implements RegistryListener {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 1;
    private OnListFragmentInteractionListener mListener;
    private RemoteDeviceViewAdapter mAdapter;
    private ArrayList<RemoteDevice> mRemoteDevices;
    private RecyclerView mRecyclerView;
    private AndroidUpnpService mUpnpService;

    public RemoteDeviceFragment() {
    }

    public static RemoteDeviceFragment newInstance(int columnCount) {
        RemoteDeviceFragment fragment = new RemoteDeviceFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item_list, container, false);

        // Set the mAdapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            mRecyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                mRecyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                mRecyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(mRecyclerView.getContext(),
                    LinearLayoutManager.VERTICAL);
            mRecyclerView.addItemDecoration(dividerItemDecoration);

            mRemoteDevices = new ArrayList<>();
            mAdapter = new RemoteDeviceViewAdapter(mRemoteDevices, mListener);
            mRecyclerView.setAdapter(mAdapter);
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnListFragmentInteractionListener) {
            mListener = (OnListFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnListFragmentInteractionListener");
        }

        if (context instanceof UpnpServiceProvider) {
            mUpnpService = ((UpnpServiceProvider) context).getUpnpService();
            if (mUpnpService != null) {
                mUpnpService.get().getRegistry().addListener(this);
            }
            for (RemoteDevice rd : mUpnpService.getRegistry().getRemoteDevices()) {
                this.remoteDeviceAdded(mUpnpService.getRegistry(), rd);
            }

        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        if (mUpnpService != null) {
            mUpnpService.get().getRegistry().removeListener(this);
        }
    }

    @Override
    public void remoteDeviceDiscoveryStarted(Registry registry, RemoteDevice device) {

    }

    @Override
    public void remoteDeviceDiscoveryFailed(Registry registry, RemoteDevice device, Exception ex) {

    }

    @Override
    public void remoteDeviceAdded(Registry registry, RemoteDevice device) {
        synchronized (mRemoteDevices) {
            int position = mRemoteDevices.indexOf(device);
            if (position >= 0) {
                mRemoteDevices.remove(device);
                mRemoteDevices.add(position, device);
            } else {
                mRemoteDevices.add(device);
            }
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void remoteDeviceUpdated(Registry registry, RemoteDevice device) {
        synchronized (mRemoteDevices) {
            int position = mRemoteDevices.indexOf(device);
            if (position >= 0) {
                mRemoteDevices.remove(device);
                mRemoteDevices.add(position, device);
            } else {
                mRemoteDevices.add(device);
            }
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void remoteDeviceRemoved(Registry registry, RemoteDevice device) {
        synchronized (mRemoteDevices) {
            mRemoteDevices.remove(device);
        }
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void localDeviceAdded(Registry registry, LocalDevice device) {

    }

    @Override
    public void localDeviceRemoved(Registry registry, LocalDevice device) {

    }

    @Override
    public void beforeShutdown(Registry registry) {

    }

    @Override
    public void afterShutdown() {

    }

    public interface OnListFragmentInteractionListener {
        void onListFragmentInteraction(RemoteDevice item);
    }
}
