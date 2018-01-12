/*
 * Copyright (C) 2016-2017 The CyanogenMod Project
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

import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;

import com.cyanogenmod.settings.device.preferences.SeekBarPreference;
import com.cyanogenmod.settings.device.utils.NodePreferenceActivity;
import com.cyanogenmod.settings.device.utils.Constants;

public class AdvancedSettings extends NodePreferenceActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.advanced_settings_panel);
/*
        for (Constants.AdvancedPrefKeysEnum prefEnum : Constants.AdvancedPrefKeysEnum.values()) {
            String pref = prefEnum.name();
			Preference p = findPreference(pref);
			if (p instanceof SeekBarPreference) {
				SeekBarPreference s = ((SeekBarPreference) p);
				s.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
					@Override
					public boolean onPreferenceChange(Preference preference, Object newValue) {
						final int progress = Integer.valueOf(String.valueOf(newValue));
						preference.setSummary(String.format("My progress value: %d", progress));
					}
				});
			}
		}
  */      
    }
 
}
