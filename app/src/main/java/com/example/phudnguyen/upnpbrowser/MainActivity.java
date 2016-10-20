package com.example.phudnguyen.upnpbrowser;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.phudnguyen.upnpbrowser.fragment.RemoteDeviceFragment;
import com.example.phudnguyen.upnpbrowser.service.MyUpnpService;
import com.example.phudnguyen.upnpbrowser.service.UpnpServiceProvider;

import org.fourthline.cling.UpnpService;
import org.fourthline.cling.android.AndroidUpnpService;
import org.fourthline.cling.model.meta.Device;
import org.fourthline.cling.model.meta.LocalDevice;
import org.fourthline.cling.model.meta.RemoteDevice;
import org.fourthline.cling.registry.DefaultRegistryListener;
import org.fourthline.cling.registry.Registry;

public class MainActivity extends AppCompatActivity implements
        RemoteDeviceFragment.OnListFragmentInteractionListener,
        UpnpServiceProvider {
    private static final String TAG = MainActivity.class.getSimpleName();
    private AndroidUpnpService upnpService;
    private ServiceConnection serviceConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName className, IBinder service) {
            upnpService = (AndroidUpnpService) service;
            mRemoteDeviceFragment = RemoteDeviceFragment.newInstance(1);
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_holder, mRemoteDeviceFragment).commit();
            upnpService.getControlPoint().search();
        }

        public void onServiceDisconnected(ComponentName className) {
            upnpService = null;
        }
    };
    private RemoteDeviceFragment mRemoteDeviceFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                upnpService.getRegistry().removeAllRemoteDevices();
                upnpService.getControlPoint().search();
            }
        });

        getApplicationContext().bindService(
                new Intent(this, MyUpnpService.class),
                serviceConnection,
                Context.BIND_AUTO_CREATE
        );
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // This will stop the UPnP service if nobody else is bound to it
        getApplicationContext().unbindService(serviceConnection);
    }

    public AndroidUpnpService getUpnpService() {
        return upnpService;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onListFragmentInteraction(RemoteDevice item) {

    }

}
