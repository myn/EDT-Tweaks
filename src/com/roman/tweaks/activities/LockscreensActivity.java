
package com.roman.tweaks.activities;

import com.roman.tweaks.R;
import com.roman.tweaks.ShellInterface;
import com.roman.tweaks.utils.ShortcutPickHelper;

import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.provider.Settings;
import android.util.Log;

public class LockscreensActivity extends PreferenceActivity implements OnPreferenceChangeListener,
        ShortcutPickHelper.OnPickListener {

    private static final String LOCKSCREEN_STYLE_PREF = "pref_lockscreen_style";
    private static final String LOCKSCREEN_QUADRANT_1_PREF = "pref_quadrant_1";
    private static final String LOCKSCREEN_QUADRANT_2_PREF = "pref_quadrant_2";
    private static final String LOCKSCREEN_QUADRANT_3_PREF = "pref_quadrant_3";
    private static final String LOCKSCREEN_QUADRANT_4_PREF = "pref_quadrant_4";
    private static final String LOCKSCREEN_CLOCK_PREF = "pref_clock";
    private static final String PREF_CARRIER_CAPTION = "pref_lockscreen_caption";

    private ListPreference mLockscreenStylePref;
    private CheckBoxPreference mShowHoneyClock;
    private Preference mHoneyQuadrant1Pref;
    private Preference mHoneyQuadrant2Pref;
    private Preference mHoneyQuadrant3Pref;
    private Preference mHoneyQuadrant4Pref;
    private Preference mCurrentCustomActivityPreference;
    private EditTextPreference mLockscreenCaptionPref;
    private String mCurrentCustomActivityString;

    private ShortcutPickHelper mPicker;

    public void onCreate(Bundle ofLove) {
        super.onCreate(ofLove);
        addPreferencesFromResource(R.xml.lockscreen_prefs);

        PreferenceScreen prefSet = getPreferenceScreen();

        /* Lockscreen Style and related related settings */
        mLockscreenStylePref = (ListPreference) prefSet.findPreference(LOCKSCREEN_STYLE_PREF);
        int lockScreenStyle = Settings.System.getInt(getContentResolver(),
                "tweaks_lockscreen_style", 0);
        Log.e("ROMAN", "style: " + lockScreenStyle);
        // mLockscreenStylePref.setValueIndex(lockScreenStyle);
        mLockscreenStylePref.setOnPreferenceChangeListener(this);
        
        mLockscreenCaptionPref = (EditTextPreference) prefSet.findPreference(PREF_CARRIER_CAPTION);
        mLockscreenCaptionPref.setOnPreferenceChangeListener(this);
        

//        mHoneyQuadrant1Pref = prefSet.findPreference(LOCKSCREEN_QUADRANT_1_PREF);
//        mHoneyQuadrant2Pref = prefSet.findPreference(LOCKSCREEN_QUADRANT_2_PREF);
//        mHoneyQuadrant3Pref = prefSet.findPreference(LOCKSCREEN_QUADRANT_3_PREF);
//        mHoneyQuadrant4Pref = prefSet.findPreference(LOCKSCREEN_QUADRANT_4_PREF);
//
//        mShowHoneyClock = (CheckBoxPreference) prefSet.findPreference(LOCKSCREEN_CLOCK_PREF);

        mPicker = new ShortcutPickHelper(this, this);

    }

    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {

        if (preference == mHoneyQuadrant1Pref) {
            mCurrentCustomActivityPreference = preference;
            mCurrentCustomActivityString = "tweaks_lockscreen_hc_activity_1";
            mPicker.pickShortcut();
            return true;

        } else if (preference == mHoneyQuadrant2Pref) {
            mCurrentCustomActivityPreference = preference;
            mCurrentCustomActivityString = "tweaks_lockscreen_hc_activity_2";
            mPicker.pickShortcut();
            return true;

        } else if (preference == mHoneyQuadrant3Pref) {
            mCurrentCustomActivityPreference = preference;
            mCurrentCustomActivityString = "tweaks_lockscreen_hc_activity_3";
            mPicker.pickShortcut();
            return true;

        } else if (preference == mHoneyQuadrant4Pref) {
            mCurrentCustomActivityPreference = preference;
            mCurrentCustomActivityString = "tweaks_lockscreen_hc_activity_4";
            mPicker.pickShortcut();
            return true;
        } else if (preference == mShowHoneyClock) {
            boolean checked = ((CheckBoxPreference) preference).isChecked();
            int value = (checked ? 1 : 0);

            Settings.System.putInt(getContentResolver(),
                    "tweaks_lockscreen_hc_clock_enabled", value);
            return true;
        }

        return false;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mPicker.onActivityResult(requestCode, resultCode, data);
    }

    public boolean onPreferenceChange(Preference preference, Object newValue) {
        if (preference == mLockscreenStylePref) {

            int index = 0;
            CharSequence[] entries = ((ListPreference) preference).getEntryValues();

            for (int i = 0; i < entries.length; i++) {
                if (entries[i].equals((String) newValue)) {
                    index = i;
                    Log.e("Roman", "break: " + i);
                    break;
                }
            }
            int newint = Integer.parseInt(entries[index] + "");
            Settings.System.putInt(getContentResolver(), "tweaks_lockscreen_style",
                    newint);
            // Log.e("Roman", "new val: " + newint);
            // updateStylePrefs(mLockscreenStyle, mInCallStyle);
            return true;
        }
        else if (preference == mLockscreenCaptionPref) {

			String inputCarrierText = String.valueOf((String) newValue);
			Settings.System.putString(getContentResolver(),
					"tweaks_lockscreen_caption", inputCarrierText);
			
			if (ShellInterface.isSuAvailable()) {
				ShellInterface.runCommand("echo '"+inputCarrierText+"' > /system/customize/lock_carrier.txt");
			}
			return true;

		}


        return false;
    }

    public void shortcutPicked(String uri, String friendlyName, boolean isApplication) {
        if (Settings.System.putString(getContentResolver(), mCurrentCustomActivityString, uri)) {
            mCurrentCustomActivityPreference.setSummary(friendlyName);
        }
    }

}
