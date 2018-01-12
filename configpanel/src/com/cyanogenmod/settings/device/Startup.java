/*
 * Copyright (C) 2015 The CyanogenMod Project
 *               2017 The LineageOS Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.cyanogenmod.settings.device;

import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.input.InputManager;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.os.UserHandle;
import android.preference.PreferenceManager;
import android.service.gesture.IGestureService;
import android.util.Log;
import android.view.InputDevice;
import android.view.InputEvent;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;

import com.cyanogenmod.settings.device.utils.Constants;

import org.cyanogenmod.internal.util.FileUtils;

public class Startup extends BroadcastReceiver {

    private static final String TAG = Startup.class.getSimpleName();

    @Override
    public void onReceive(final Context context, final Intent intent) {
        // AdvancedSettings.restore(context); // not required... why?        
        BlxPreference.restore(context);

        final String action = intent.getAction();

/*
 *                 <action android:name="android.intent.action.ACTION_BOOT_COMPLETED" />

 * 		if (Intent.ACTION_BOOT_COMPLETED.equals(action)) {
		    // Restore nodes to saved preference values
            for (String pref : Constants.sBooleanNodePreferenceMap.keySet()) {
                boolean value = Constants.isPreferenceEnabled(context, pref);
                String node = Constants.sBooleanNodePreferenceMap.get(pref);
                FileUtils.writeLine(node, value ? "1" : "0");
            }
		} */

        if (cyanogenmod.content.Intent.ACTION_INITIALIZE_CM_HARDWARE.equals(action)) {
            // Restore nodes to saved preference values
            for (Constants.AdvancedPrefKeysEnum prefEnum : Constants.AdvancedPrefKeysEnum.values()) {
				String pref = prefEnum.name();
                String node = prefEnum.getNode();
                String value;
                if (Constants.PKE_BOOL.equals(prefEnum.getType())) {
                    value = Constants.isPreferenceEnabled(context, pref) ?
                            "1" : "0";
                    Utils.writeValue(node, Constants.isPreferenceEnabled(context, pref));
					Log.w(TAG, "Utils.write[bool](" + node + "," + Constants.isPreferenceEnabled(context, pref) + ") got '" + Utils.readOneLine(node) + "'");
                } else {
                    value = Constants.getPreferenceString(context, pref);
                    Utils.writeValue(node, value);
					Log.w(TAG, "Utils.write[string](" + node + "," + value + ") got '" + Utils.readOneLine(node) + "'");
                }
                //FileUtils.write(node, value);
                //Log.w(TAG, "write(" + node + "," + value + ") got '" + Utils.readOneLine(node) + "'");
                /*
                if (!FileUtils.writeLine(node, value)) {
                    Log.w(TAG, "Write to node " + node +
                        " failed while restoring saved preference values");
                } else {
                    Log.w(TAG, "Write to node " + node +
                        " succeeded while restoring saved preference values");
                }
                Log.w(TAG, "writeLine(" + node + "," + value + ") got '" + Utils.readOneLine(node) + "'");*/
            }			
		
		/*	
            // Disable battery settings if needed
            if (hasFastcharge() || hasBlx()) {
                enableComponent(context, AdvancedSettings.class.getName());

                // Disable fastcharge if needed
                if (hasFastcharge()) {
                    enableComponent(context, R.xml.battery_panel.fastcharge_state.class.getName());
                } else {
                    disableComponent(context, R.xml.battery_panel.fastcharge_state.class.getName());
                }

                // Disable battery life saver if needed
                if (hasBlx()) {
                    enableComponent(context, ChargingLimitDialogPreference.class.getName());
                } else {
                    disableComponent(context, ChargingLimitDialogPreference.class.getName());
                }

            } else {
                // disable settings
                disableComponent(context, AdvancedSettings.class.getName());
            }
            */
        }
    }

    static boolean hasAdvancedSettings() {
        return true;//hasFastcharge() || hasBlx();
    }
/*
    static boolean hasFastcharge() {
        return (FileUtils.fileExists(Constants.FASTCHARGE_NODE));
    }

    static boolean hasBlx() {
        return (FileUtils.fileExists(Constants.BLX_NODE));
    } */

    private void disableComponent(Context context, String component) {
        ComponentName name = new ComponentName(context, component);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(name,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }

    private void enableComponent(Context context, String component) {
        ComponentName name = new ComponentName(context, component);
        PackageManager pm = context.getPackageManager();
        if (pm.getComponentEnabledSetting(name)
                == PackageManager.COMPONENT_ENABLED_STATE_DISABLED) {
            pm.setComponentEnabledSetting(name,
                    PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                    PackageManager.DONT_KILL_APP);
        }
    }
}
