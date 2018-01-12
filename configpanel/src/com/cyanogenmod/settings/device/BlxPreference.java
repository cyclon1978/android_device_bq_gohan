/**
 * Copyright (C) 2013-2016 The CyanogenMod Project
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

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.LightingColorFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Button;

import java.lang.Math;
import java.text.DecimalFormat;

import com.cyanogenmod.settings.device.utils.Constants;

/**
 * Special preference type that allows configuration of vibrator intensity settings on HTC devices
 */
public class BlxPreference extends DialogPreference implements SeekBar.OnSeekBarChangeListener {
    private static final String TAG = "BlxPreference";

    private static String FILE_PATH = null;
    private static int MAX_VALUE;
    private static int WARNING_THRESHOLD;
    private static int DEFAULT_VALUE;
    private static int MIN_VALUE;

    private Context mContext;
    private SeekBar mSeekBar;
    private TextView mValueText;
    private TextView mWarningText;
    private String mOriginalValue;
    private Drawable mProgressDrawable;
    private Drawable mProgressThumb;
    private LightingColorFilter mRedFilter;

    public BlxPreference(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        mContext = context;

        FILE_PATH = context.getResources().getString(R.string.charging_limit_sysfs_file);
        MAX_VALUE = Integer.valueOf(context.getResources().getString(R.string.charging_limit_max_value));
        WARNING_THRESHOLD = Integer.valueOf(context.getResources().getString(R.string.charging_limit_warning_threshold));
        DEFAULT_VALUE = Integer.valueOf(context.getResources().getString(R.string.charging_limit_default_value));
        MIN_VALUE = Integer.valueOf(context.getResources().getString(R.string.charging_limit_min_value));

        setDialogLayoutResource(R.layout.preference_dialog_blx);
    }

    @Override
    protected void onPrepareDialogBuilder(final AlertDialog.Builder builder) {
        builder.setNeutralButton(R.string.defaults_button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mSeekBar.setProgress(Integer.valueOf(DEFAULT_VALUE-MIN_VALUE));
            }
        });
    }

    @Override
    protected void onBindDialogView(final View view) {
        super.onBindDialogView(view);

        mSeekBar = (SeekBar) view.findViewById(R.id.blx_seekbar);
        mValueText = (TextView) view.findViewById(R.id.blx_value);
        mWarningText = (TextView) view.findViewById(R.id.blx_warning_text);

        final String message = getContext().getResources().getString(
                R.string.charging_limit_dialog_warning,
                WARNING_THRESHOLD);
        mWarningText.setText(message);

        Drawable progressDrawable = mSeekBar.getProgressDrawable();
        if (progressDrawable instanceof LayerDrawable) {
            LayerDrawable ld = (LayerDrawable) progressDrawable;
            mProgressDrawable = ld.findDrawableByLayerId(android.R.id.progress);
        }
        mProgressThumb = mSeekBar.getThumb();
        mRedFilter = new LightingColorFilter(Color.BLACK,
                getContext().getResources().getColor(android.R.color.holo_red_light));

        // Read the current value from sysfs in case user wants to dismiss his changes
        mOriginalValue = Utils.readOneLine(FILE_PATH);

        // Restore percent value from SharedPreferences object
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(mContext);
        int percent = settings.getInt(Constants.BLX_STATE_KEY, DEFAULT_VALUE);

        mSeekBar.setOnSeekBarChangeListener(this);
        mSeekBar.setMax(MAX_VALUE - MIN_VALUE);
        mSeekBar.setProgress(Integer.valueOf(percent-MIN_VALUE));
    }

    @Override
    protected void showDialog(Bundle state) {
        super.showDialog(state);

        // can't use onPrepareDialogBuilder for this as we want the dialog
        // to be kept open on click
        AlertDialog d = (AlertDialog) getDialog();
        Button defaultsButton = d.getButton(DialogInterface.BUTTON_NEUTRAL);
        defaultsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSeekBar.setProgress(DEFAULT_VALUE-MIN_VALUE);
            }
        });
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);

        if (positiveResult) {
            // Store percent value in SharedPreferences object
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt(Constants.BLX_STATE_KEY, mSeekBar.getProgress()+MIN_VALUE);
            editor.commit();
        } else {
            setChargingLimit(mOriginalValue);
        }
    }

    public static void restore(Context context) {
        FILE_PATH = context.getResources().getString(R.string.charging_limit_sysfs_file);

        if (!isSupported(FILE_PATH)) {
            return;
        }

        MAX_VALUE = Integer.valueOf(context.getResources().getString(R.string.charging_limit_max_value));
        DEFAULT_VALUE = Integer.valueOf(context.getResources().getString(R.string.charging_limit_default_value));
        MIN_VALUE = Integer.valueOf(context.getResources().getString(R.string.charging_limit_min_value));

        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(context);
        int limit = settings.getInt(Constants.BLX_STATE_KEY, DEFAULT_VALUE);

        Log.d(TAG, "Restoring blx setting: " + limit);
        setChargingLimit(String.valueOf(limit));       
    }

    public static boolean isSupported(String filePath) {
        return Utils.fileExists(filePath);
    }

    @Override
    public void onProgressChanged(
                final SeekBar seekBar, final int progress, final boolean fromUser) {
        final int limit = progress + MIN_VALUE;
        final boolean shouldWarn = WARNING_THRESHOLD > 0 && limit >= WARNING_THRESHOLD;

        if (mProgressDrawable != null) {
            mProgressDrawable.setColorFilter(shouldWarn ? mRedFilter : null);
        }
        if (mProgressThumb != null) {
            mProgressThumb.setColorFilter(shouldWarn ? mRedFilter : null);
        }

        setChargingLimit(String.valueOf(limit));
        mValueText.setText(
                String.format("%d%%", limit));
    }

    @Override
    public void onStartTrackingTouch(final SeekBar seekBar) {
        // Do nothing
    }

    @Override
    public void onStopTrackingTouch(final SeekBar seekBar) {
        setChargingLimit(String.valueOf(seekBar.getProgress() + MIN_VALUE));
    }

    private static void setChargingLimit(final String limit) {
        Utils.writeValue(FILE_PATH, limit);
    }

}
