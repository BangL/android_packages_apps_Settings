/*
 * Copyright (C) 2012 The CyanogenMod Project
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

package com.android.settings.cyanogenmod;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.SystemProperties;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.ListPreference;
import android.preference.PreferenceScreen;
import android.provider.Settings;

import com.android.settings.R;
import com.android.settings.SettingsPreferenceFragment;

/**
 * Performance Settings
 */
public class PerformanceSettings extends SettingsPreferenceFragment
        implements Preference.OnPreferenceChangeListener {
    private static final String TAG = "PerformanceSettings";

    private static final String USE_DITHERING_PREF = "pref_use_dithering";

    private static final String USE_DITHERING_PERSIST_PROP = "persist.sys.use_dithering";

    private static final String USE_DITHERING_DEFAULT = "1";

    private static final String USE_16BPP_ALPHA_PREF = "pref_use_16bpp_alpha";

    private static final String USE_16BPP_ALPHA_PROP = "persist.sys.use_16bpp_alpha";

    private static final String NOTIFICATION_SHADE_DIM = "notification_shade_dim";

    private static final String DISABLE_WALLPAPER_PREF = "pref_disable_wallpaperservice";

    private static final String DISABLE_WALLPAPER_PERSIST_PROP = "persist.sys.wallpaperservice";

    private static final String DISABLE_WALLPAPER_DEFAULT = "1";

    private ListPreference mUseDitheringPref;

    private CheckBoxPreference mUse16bppAlphaPref;

    private CheckBoxPreference mNotificationShadeDim;

    private CheckBoxPreference mDisableWallpaperPref;

    private AlertDialog alertDialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getPreferenceManager() != null) {

            addPreferencesFromResource(R.xml.performance_settings);

            PreferenceScreen prefSet = getPreferenceScreen();

            String useDithering = SystemProperties.get(USE_DITHERING_PERSIST_PROP, USE_DITHERING_DEFAULT);
            mUseDitheringPref = (ListPreference) prefSet.findPreference(USE_DITHERING_PREF);
            mUseDitheringPref.setOnPreferenceChangeListener(this);
            mUseDitheringPref.setValue(useDithering);
            mUseDitheringPref.setSummary(mUseDitheringPref.getEntry());

            mUse16bppAlphaPref = (CheckBoxPreference) prefSet.findPreference(USE_16BPP_ALPHA_PREF);
            String use16bppAlpha = SystemProperties.get(USE_16BPP_ALPHA_PROP, "0");
            mUse16bppAlphaPref.setChecked("1".equals(use16bppAlpha));

            mNotificationShadeDim = (CheckBoxPreference) prefSet.findPreference(NOTIFICATION_SHADE_DIM);
            mNotificationShadeDim.setChecked((Settings.System.getInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.NOTIFICATION_SHADE_DIM, ActivityManager.isHighEndGfx() ? 1 : 0) == 1));

            mDisableWallpaperPref = (CheckBoxPreference) prefSet
                    .findPreference(DISABLE_WALLPAPER_PREF);
            String disableWallpaper = SystemProperties.get(DISABLE_WALLPAPER_PERSIST_PROP,
                    DISABLE_WALLPAPER_DEFAULT);
            mDisableWallpaperPref.setChecked("0".equals(disableWallpaper));

            /* Display the warning dialog */
            alertDialog = new AlertDialog.Builder(getActivity()).create();
            alertDialog.setTitle(R.string.performance_settings_warning_title);
            alertDialog.setMessage(getResources().getString(R.string.performance_settings_warning));
            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE,
                    getResources().getString(com.android.internal.R.string.ok),
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            return;
                        }
                    });
            alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                public void onCancel(DialogInterface dialog) {
                    PerformanceSettings.this.finish();
                }
            });
            alertDialog.show();
        }
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference == mUse16bppAlphaPref) {
            SystemProperties.set(USE_16BPP_ALPHA_PROP,
                    mUse16bppAlphaPref.isChecked() ? "1" : "0");
        } else if (preference == mNotificationShadeDim) {
            Settings.System.putInt(getActivity().getApplicationContext().getContentResolver(),
                    Settings.System.NOTIFICATION_SHADE_DIM, mNotificationShadeDim.isChecked() ? 1 : 0);
        } else if (preference == mDisableWallpaperPref) {
            SystemProperties.set(DISABLE_WALLPAPER_PERSIST_PROP,
                    mDisableWallpaperPref.isChecked() ? "0" : "1");
        } else {
            // If we didn't handle it, let preferences handle it.
            return super.onPreferenceTreeClick(preferenceScreen, preference);
        }

        return true;
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mUseDitheringPref) {
            String newVal = (String) newValue;
            int index = mUseDitheringPref.findIndexOfValue(newVal);
            SystemProperties.set(USE_DITHERING_PERSIST_PROP, newVal);
            mUseDitheringPref.setSummary(mUseDitheringPref.getEntries()[index]);
        }
        return true;
    }

}
