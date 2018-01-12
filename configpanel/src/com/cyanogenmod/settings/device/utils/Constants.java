/*
 * Copyright (C) 2017 The CyanogenMod Project
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

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Constants {
   
	public static final String PKE_BOOL = "bool";
	public static final String PKE_INT = "int";

	public enum AdvancedPrefKeysEnum {
        fastcharge_state(
            "/sys/kernel/fast_charge/force_fast_charge",
            Boolean.TRUE,
            PKE_BOOL),
            
        fsync_state(
            "/sys/module/sync/parameters/fsync_enabled",
            Boolean.TRUE,
            PKE_BOOL),
            
        dynamic_fsync_state(
            "/sys/kernel/dyn_fsync/Dyn_fsync_active",
            Boolean.FALSE,
            PKE_BOOL),
            
        cluster_plug_active_state(
            "/sys/module/cluster_plug/parameters/active",
            Boolean.TRUE,
            PKE_BOOL),
            
        cluster_plug_low_power_mode_state(
            "/sys/module/cluster_plug/parameters/low_power_mode",
            Boolean.FALSE,
            PKE_BOOL),
            
        cluster_plug_screen_off_power_mode_state(
            "/sys/module/cluster_plug/parameters/screen_off_power_mode",
            Boolean.FALSE,
            PKE_BOOL),

        cluster_plug_sampling_time_state(
            "/sys/module/cluster_plug/parameters/sampling_time",
            Integer.valueOf(50),
            PKE_INT),
            
        cluster_plug_load_threshold_up_state(
            "/sys/module/cluster_plug/parameters/load_threshold_up",
            Integer.valueOf(80),
            PKE_INT),
            
        cluster_plug_load_threshold_down_state(
            "/sys/module/cluster_plug/parameters/load_threshold_down",
            Integer.valueOf(20),
            PKE_INT),
            
        cluster_plug_vote_threshold_up_state(
            "/sys/module/cluster_plug/parameters/vote_threshold_up",
            Integer.valueOf(3),
            PKE_INT),
            
        cluster_plug_vote_threshold_down_state(
            "/sys/module/cluster_plug/parameters/vote_threshold_down",
            Integer.valueOf(3),
            PKE_INT),
            
        cluster_plug_max_cores_screenoff_state(
            "/sys/module/cluster_plug/parameters/max_cores_screenoff",
	        Integer.valueOf(4),
            PKE_INT);

        private String node;
        private Object defaultValue;
        private String type;
        private AdvancedPrefKeysEnum(String node, Object defaultValue, String type) {
            this.node = node;
            this.defaultValue = defaultValue;
            this.type = type;
        }
        public String getNode() {
            return this.node;
        }
        public Object getDefaultValue() {
            return this.defaultValue;
        }
        public String getType() {
            return this.type;
        }
    }

    // additional preference keys for blx
    public static final String BLX_STATE_KEY = "blx_state";

    public static boolean isPreferenceEnabled(Context context, String key) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getBoolean(key, (Boolean) AdvancedPrefKeysEnum.valueOf(key).getDefaultValue());
    }

    public static String getPreferenceString(Context context, String key) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        Integer defaultValue = (Integer) AdvancedPrefKeysEnum.valueOf(key).getDefaultValue();

		return String.valueOf(preferences.getInt(key, defaultValue));
    }
}
