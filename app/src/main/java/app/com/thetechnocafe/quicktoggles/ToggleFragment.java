package app.com.thetechnocafe.quicktoggles;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

/**
 * Created by gurleensethi on 20/08/16.
 */
public class ToggleFragment extends Fragment {

    private ToggleButton mWifiToggle;
    private ToggleButton mBluetoothToggle;
    private ToggleButton mGPSToggle;
    private ToggleButton mAirplaneModeToggle;

    public static ToggleFragment getInstance() {
        return new ToggleFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_toggle, container, false);

        mWifiToggle = (ToggleButton) view.findViewById(R.id.wifi_toggle_button);
        mBluetoothToggle = (ToggleButton) view.findViewById(R.id.bluetooth_toggle_button);
        mGPSToggle = (ToggleButton) view.findViewById(R.id.gps_toggle_button);
        mAirplaneModeToggle = (ToggleButton) view.findViewById(R.id.airplane_toggle_button);

        return view;
    }

    private void setUpWiFiToggle() {
        final WifiManager wifiManager = (WifiManager) getContext().getSystemService(Context.WIFI_SERVICE);
        mWifiToggle.setChecked(wifiManager.isWifiEnabled());

        mWifiToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                wifiManager.setWifiEnabled(isChecked);
            }
        });
    }

    private void setUpBluetoothToggle() {
        final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (Build.VERSION.SDK_INT > 22)
            if (getActivity().checkSelfPermission(Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED)
                requestPermissions(new String[]{Manifest.permission.BLUETOOTH}, 1);
        mBluetoothToggle.setChecked(bluetoothAdapter.isEnabled());
        mBluetoothToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (Build.VERSION.SDK_INT > 22)
                    if (getActivity().checkSelfPermission(Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED && getActivity().checkSelfPermission(Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED)
                        requestPermissions(new String[]{Manifest.permission.BLUETOOTH, Manifest.permission.BLUETOOTH_ADMIN}, 1);
                if (isChecked) {
                    bluetoothAdapter.enable();
                } else {
                    bluetoothAdapter.disable();
                }
            }
        });
    }

    private void setUpGPSToggle() {
        LocationManager locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        mGPSToggle.setChecked(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER));
        mGPSToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });
    }

    private void setUpAirplaneModeToggle() {
        int enabled = Settings.System.getInt(getContext().getContentResolver(), Settings.System.AIRPLANE_MODE_ON, 0);
        mAirplaneModeToggle.setChecked(enabled == 1);
        mAirplaneModeToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                int enabled = 0;
                if(isChecked) {
                    enabled = 1;
                } else {
                    enabled = 0;
                }
                Settings.System.putInt(getContext().getContentResolver(), Settings.System.AIRPLANE_MODE_ON, enabled);
                //Broadcast mode change
                Intent intent = new Intent(Intent.ACTION_AIRPLANE_MODE_CHANGED);
                intent.putExtra("state",enabled == 1);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        updateUI();
    }

    private void updateUI() {
        setUpWiFiToggle();
        setUpBluetoothToggle();
        setUpGPSToggle();
        setUpAirplaneModeToggle();
    }
}
