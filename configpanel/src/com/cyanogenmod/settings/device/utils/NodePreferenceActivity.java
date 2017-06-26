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

package com.cyanogenmod.settings.device.utils;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.ListPreference;
import android.preference.SwitchPreference;
import com.cyanogenmod.settings.device.preferences.SeekBarPreference;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;

import org.cyanogenmod.internal.util.FileUtils;
import org.cyanogenmod.internal.util.ScreenType;

import com.cyanogenmod.settings.device.Utils;

public class NodePreferenceActivity extends PreferenceActivity
        implements OnPreferenceChangeListener {

    private static final String TAG = NodePreferenceActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();

        // If running on a phone, remove padding around the listview
        if (!ScreenType.isTablet(this)) {
            getListView().setPadding(0, 0, 0, 0);
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        Log.w(TAG, "onPreferenceChange " + preference.getKey());

		Constants.AdvancedPrefKeysEnum prefEnum = Constants.AdvancedPrefKeysEnum.valueOf(preference.getKey());
        String node = prefEnum.getNode();
        Log.w(TAG, "onPreferenceChange node " + node);
        
        if (!TextUtils.isEmpty(node)) { // && FileUtils.isFileWritable(node)) {
            if (preference instanceof SwitchPreference) {
                Boolean value = (Boolean) newValue;
				Log.w(TAG, "onPreferenceChange SwitchPreference writeLine(" + node + "," + value + ")");
	            Utils.writeValue(node, value);
				Log.w(TAG, "Utils.writeValue(" + node + "," + value + ") got '" + Utils.readOneLine(node) + "'");

/*	            String writeValue = value ? "1" : "0";
	            
                FileUtils.writeLine(node, writeValue);
				Log.w(TAG, "FileUtils.writeLine(" + node + "," + writeValue + ") got '" + Utils.readOneLine(node) + "'");*/
                return true;
            } else if (preference instanceof ListPreference) {
				String value = (String) newValue;
				Log.w(TAG, "onPreferenceChange ListPreference writeLine(" + node + "," + value + ")");
                Utils.writeValue(node, value);
				Log.w(TAG, "Utils.writeValue(" + node + "," + value + ") got '" + Utils.readOneLine(node) + "'");
                /*
                FileUtils.writeLine(node, writeValue);*/
                return true;
            } else if (preference instanceof SeekBarPreference) {
				String value = Integer.toString((Integer) newValue);
				Log.w(TAG, "onPreferenceChange SeekBarPreference writeLine(" + node + "," + value + ")");
                Utils.writeValue(node, value);
				Log.w(TAG, "Utils.writeValue(" + node + "," + value + ") got '" + Utils.readOneLine(node) + "'");

/*                
                FileUtils.writeLine(node, value);*/
                return true;
            }
        }

        Log.e(TAG, "onPreferenceChange failed for " + preference.getKey());
        return false;
    }

    @Override
    public void addPreferencesFromResource(int preferencesResId) {
        super.addPreferencesFromResource(preferencesResId);
        // Initialize node preferences
        for (Constants.AdvancedPrefKeysEnum prefEnum : Constants.AdvancedPrefKeysEnum.values()) {
            String pref = prefEnum.name();
            String node = prefEnum.getNode();

            Preference preference = findPreference(pref);
            if (preference == null) {
				Log.e(TAG, "addPreferencesFromResource preference not found for " + pref);
				continue;
			}
			
            if (preference instanceof SwitchPreference) {
                SwitchPreference b = (SwitchPreference) preference;
                b.setOnPreferenceChangeListener(this);
                if (FileUtils.isFileReadable(node)) {
                    String curNodeValue = FileUtils.readOneLine(node);
					Log.e(TAG, "addPreferencesFromResource setChecked(" + (curNodeValue.equals("1") || curNodeValue.equals("Y")) + ") because curNodeValue is " + curNodeValue);
                    b.setChecked(curNodeValue.equals("1") || curNodeValue.equals("Y"));
                } else {
                    b.setEnabled(false);
                }
            } else if (preference instanceof ListPreference) {
				ListPreference l = (ListPreference) preference;
                l.setOnPreferenceChangeListener(this);
                if (FileUtils.isFileReadable(node)) {
					String curNodeValue = FileUtils.readOneLine(node);
					Log.e(TAG, "addPreferencesFromResource setValue(" + curNodeValue + ")");
                    l.setValue(curNodeValue);
                } else {
                    l.setEnabled(false);
                }
            } else if (preference instanceof SeekBarPreference) {
				SeekBarPreference s = (SeekBarPreference) preference;
				s.setOnPreferenceChangeListener(this);
                if (FileUtils.isFileReadable(node)) {
					String curNodeValue = FileUtils.readOneLine(node);
					Log.e(TAG, "addPreferencesFromResource setProgress(" + Integer.parseInt(curNodeValue) + ") because curNodeValue is " + curNodeValue);
                    s.setProgress(Integer.parseInt(curNodeValue));
                } else {
                    s.setEnabled(false);
                }
			}
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        // Respond to the action bar's Up/Home button
        case android.R.id.home:
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
