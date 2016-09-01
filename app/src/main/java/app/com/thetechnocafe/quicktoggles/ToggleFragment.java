package app.com.thetechnocafe.quicktoggles;

import android.Manifest;
import android.animation.Animator;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.media.AudioManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ToggleButton;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import java.lang.reflect.Method;

/**
 * Created by gurleensethi on 20/08/16.
 */
public class ToggleFragment extends Fragment {

    private ToggleButton mWifiToggle;
    private ToggleButton mBluetoothToggle;
    private ToggleButton mGPSToggle;
    private ToggleButton mAirplaneModeToggle;
    private ToggleButton mDataToggle;
    private ToggleButton mHotspotToggle;
    private DiscreteSeekBar mVolumeSeekBar;
    private DiscreteSeekBar mMusicSeekBar;
    private DiscreteSeekBar mAlarmSeekBar;
    private DiscreteSeekBar mVoiceCallSeekBar;
    private ToggleButton mOrientationToggle;

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
        mDataToggle = (ToggleButton) view.findViewById(R.id.data_toggle_button);
        mHotspotToggle = (ToggleButton) view.findViewById(R.id.hotspot_toggle_button);
        mVolumeSeekBar = (DiscreteSeekBar) view.findViewById(R.id.system_volume_seekbar);
        mMusicSeekBar = (DiscreteSeekBar) view.findViewById(R.id.music_volume_seekbar);
        mAlarmSeekBar = (DiscreteSeekBar) view.findViewById(R.id.alarm_volume_seekbar);
        mVoiceCallSeekBar = (DiscreteSeekBar) view.findViewById(R.id.voice_call_volume_seekbar);
        mOrientationToggle = (ToggleButton) view.findViewById(R.id.orientation_toggle_button);

        requestPermissions(new String[]{Manifest.permission.WRITE_SETTINGS}, 1);

        return view;
    }

    private void setUpWiFiToggle() {
        final WifiManager wifiManager = (WifiManager) getContext().getSystemService(Context.WIFI_SERVICE);
        mWifiToggle.setChecked(wifiManager.isWifiEnabled());

        mWifiToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                createCircularAnimation(mWifiToggle);
                wifiManager.setWifiEnabled(isChecked);
            }
        });
    }

    private void setUpBluetoothToggle() {
        final BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
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
        mAirplaneModeToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS);
                startActivity(intent);
            }
        });
    }

    private void setUpDataToggle() {
        TelephonyManager telephonyManager = (TelephonyManager) getContext().getSystemService(Context.TELEPHONY_SERVICE);
        try {
            Method getMobileDataEnabledMethod = telephonyManager.getClass().getDeclaredMethod("getDataEnabled");
            if (getMobileDataEnabledMethod != null) {
                mDataToggle.setChecked((Boolean) getMobileDataEnabledMethod.invoke(telephonyManager));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        mDataToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS);
                startActivity(intent);
            }
        });
    }

    private void setUpHotspotToggle() {
        WifiManager wifiManager = (WifiManager) getContext().getSystemService(Context.WIFI_SERVICE);
        mHotspotToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                startActivity(intent);
            }
        });
    }

    private void setUpVolumeSeekBar() {
        final AudioManager audioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        mVolumeSeekBar.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_RING));
        mVolumeSeekBar.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_RING));
        mVolumeSeekBar.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                audioManager.setStreamVolume(AudioManager.STREAM_RING, value, 0);
            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {

            }
        });
    }

    private void setUpMusicVolumeSeekBar() {
        final AudioManager audioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        mMusicSeekBar.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        mMusicSeekBar.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
        mMusicSeekBar.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, value, 0);
            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {

            }
        });
    }

    private void setUpAlarmVolumeSeekBar() {
        final AudioManager audioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        mAlarmSeekBar.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM));
        mAlarmSeekBar.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_ALARM));
        mAlarmSeekBar.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                audioManager.setStreamVolume(AudioManager.STREAM_ALARM, value, 0);
            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {

            }
        });
    }

    private void setUpVoiceCallVolumeSeekBar() {
        final AudioManager audioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        mVoiceCallSeekBar.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_VOICE_CALL));
        mVoiceCallSeekBar.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_VOICE_CALL));
        mVoiceCallSeekBar.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int value, boolean fromUser) {
                audioManager.setStreamVolume(AudioManager.STREAM_VOICE_CALL, value, 0);
            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {

            }
        });
    }

    private void setUpOrientationToggle() {
        int screenRotation = 0;
        try {
            screenRotation = Settings.System.getInt(getContext().getContentResolver(), Settings.System.ACCELEROMETER_ROTATION);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        mOrientationToggle.setChecked(screenRotation == 1);
        mOrientationToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (Settings.System.canWrite(getContext())) {
                    Settings.System.putInt(getContext().getContentResolver(), Settings.System.ACCELEROMETER_ROTATION, isChecked ? 1 : 0);
                } else {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                    startActivity(intent);
                }
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
        setUpDataToggle();
        setUpHotspotToggle();
        setUpVolumeSeekBar();
        setUpMusicVolumeSeekBar();
        setUpAlarmVolumeSeekBar();
        setUpVoiceCallVolumeSeekBar();
        setUpOrientationToggle();
    }

    private void createCircularAnimation(View view) {
        int cx = view.getWidth() / 2;
        int cy = view.getHeight() / 2;
        float finalRadius = (float) Math.hypot(cx, cy);
        Animator animatior = null;
        if (Build.VERSION.SDK_INT > 20) {
            animatior = ViewAnimationUtils.createCircularReveal(view, cx, cy, 0, finalRadius);
            animatior.start();
        }
    }
}
