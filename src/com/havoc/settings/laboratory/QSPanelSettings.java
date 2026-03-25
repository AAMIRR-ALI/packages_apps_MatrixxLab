/*
 * Copyright (C) 2023 The LeafOS Project
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

package com.havoc.settings.laboratory;

import android.app.ActivityManager;
import android.content.ContentResolver;
import android.content.Context;
import androidx.preference.Preference;
import androidx.preference.PreferenceScreen;
import android.os.Bundle;
import android.os.UserHandle;
import android.provider.Settings;

import androidx.preference.ListPreference;
import androidx.preference.Preference;

import com.android.settings.R;
import com.android.settings.dashboard.DashboardFragment;
import com.android.settings.search.BaseSearchIndexProvider;
import com.android.settingslib.search.SearchIndexable;

import com.android.internal.util.custom.ThemeUtils;
import com.android.internal.util.custom.systemUtils;

import com.tenx.support.preferences.SystemSettingListPreference;
import com.tenx.support.preferences.SystemSettingSeekBarPreference;
import com.tenx.support.preferences.SystemSettingSwitchPreference;
import com.tenx.support.preferences.CustomSeekBarPreference;

@SearchIndexable
public class QSPanelSettings extends DashboardFragment implements
        Preference.OnPreferenceChangeListener {

    private static final String TAG = "QSPanelSettings";

    private static final String KEY_QS_SHOW_AUTO_BRIGHTNESS = "qs_show_auto_brightness";
    private static final String KEY_QS_UI_STYLE  = "qs_tile_ui_style";
    private static final String KEY_QS_PANEL_STYLE  = "qs_panel_style";
    private static final String KEY_QS_SPLIT_SHADE = "qs_split_shade";
    private static final String QS_SPLIT_SHADE_LAYOUT_CTG = "android.theme.customization.qs_landscape_layout";
    private static final String QS_SPLIT_SHADE_LAYOUT_PKG = "com.android.systemui.qs.landscape.split_shade_layout";
    private static final String QS_SPLIT_SHADE_LAYOUT_TARGET = "com.android.systemui";
    private static final String COMPACT_HUN_KEY = "persist.sys.compact_hun.enabled";
    private static final String KEY_QS_COMPACT_PLAYER  = "qs_compact_media_player_mode";
    private static final String KEY_QS_WIDGETS_PLAYER = "qs_widgets_player_enabled";
    private static final String KEY_TILE_ANIM_STYLE = "qs_tile_animation_style";
    private static final String KEY_TILE_ANIM_DURATION = "qs_tile_animation_duration";
    private static final String KEY_TILE_ANIM_INTERPOLATOR = "qs_tile_animation_interpolator";
    
    private Preference mQSWidgetPref;
    private SystemSettingListPreference mQsUI;
    private SystemSettingListPreference mQsPanelStyle;
    private ThemeUtils mThemeUtils;
    private SystemSettingSwitchPreference mSplitShade;
    private Preference mCompactHUNPref;
    private Preference mQsCompactPlayer;
    private SystemSettingListPreference mTileAnimationStyle;
    private SystemSettingSeekBarPreference mTileAnimationDuration;
    private SystemSettingListPreference mTileAnimationInterpolator;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Context mContext = getActivity().getApplicationContext();

        PreferenceScreen preferenceScreen = getPreferenceScreen();
        Preference qsShowAutoBrightnessPreference = preferenceScreen.findPreference(KEY_QS_SHOW_AUTO_BRIGHTNESS);

        mThemeUtils = ThemeUtils.getInstance(getActivity());
        
        mQsUI = (SystemSettingListPreference) findPreference(KEY_QS_UI_STYLE);
        mQsUI.setOnPreferenceChangeListener(this);

        mQsPanelStyle = (SystemSettingListPreference) findPreference(KEY_QS_PANEL_STYLE);
        mQsPanelStyle.setOnPreferenceChangeListener(this);

        mSplitShade = findPreference(KEY_QS_SPLIT_SHADE);
        boolean ssEnabled = isSplitShadeEnabled();
        mSplitShade.setChecked(ssEnabled);
        mSplitShade.setOnPreferenceChangeListener(this);

        mCompactHUNPref = findPreference(COMPACT_HUN_KEY);
        mCompactHUNPref.setOnPreferenceChangeListener(this);

        mQsCompactPlayer = (Preference) findPreference(KEY_QS_COMPACT_PLAYER);
        mQsCompactPlayer.setOnPreferenceChangeListener(this);

        mQSWidgetPref = findPreference(KEY_QS_WIDGETS_PLAYER);
        mQSWidgetPref.setOnPreferenceChangeListener(this);

        mTileAnimationStyle = (SystemSettingListPreference) findPreference(KEY_TILE_ANIM_STYLE);
        mTileAnimationDuration = (SystemSettingSeekBarPreference) findPreference(KEY_TILE_ANIM_DURATION);
        mTileAnimationInterpolator = (SystemSettingListPreference) findPreference(KEY_TILE_ANIM_INTERPOLATOR);
        mTileAnimationStyle.setOnPreferenceChangeListener(this);
        int tileAnimationStyle = Settings.System.getIntForUser(getContentResolver(),
                Settings.System.QS_TILE_ANIMATION_STYLE, 0, UserHandle.USER_CURRENT);
        updateTileAnimStyle(tileAnimationStyle);
        
        checkQSOverlays(mContext);

        if (qsShowAutoBrightnessPreference != null) {
            boolean automaticBrightnessAvailable = getContext().getResources().getBoolean(
                    com.android.internal.R.bool.config_automatic_brightness_available);
            if (!automaticBrightnessAvailable) {
                qsShowAutoBrightnessPreference.setVisible(false);
            }
        }
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ContentResolver resolver = getActivity().getContentResolver();

        if (preference == mQsUI) {
            int value = Integer.parseInt((String) newValue);
            Settings.System.putIntForUser(resolver,
                    Settings.System.QS_TILE_UI_STYLE, value, UserHandle.USER_CURRENT);
            updateQsStyle(getActivity());
            checkQSOverlays(getActivity());
            return true;
        } else if (preference == mQsPanelStyle) {
            int value = Integer.parseInt((String) newValue);
            Settings.System.putIntForUser(resolver,
                    Settings.System.QS_PANEL_STYLE, value, UserHandle.USER_CURRENT);
            updateQsPanelStyle(getActivity());
            checkQSOverlays(getActivity());
            return true;
        } else if (preference == mSplitShade) {
            updateSplitShadeState(((Boolean) newValue).booleanValue());
            return true;
        } else if (preference == mCompactHUNPref) {
            systemUtils.showSystemUIRestartDialog(getContext());
            return true;
        } else if (preference == mQsCompactPlayer) {
            systemUtils.showSystemUIRestartDialog(getActivity());
            return true;
        } else if (preference == mQSWidgetPref) {
            String lastPackageName = Settings.System.getString(resolver,
                    "media_session_last_package_name");
            if (lastPackageName != null && !lastPackageName.isEmpty()) {
                try {
                    ActivityManager am = (ActivityManager) getActivity().getSystemService(Context.ACTIVITY_SERVICE);
                    if (am != null) {
                        am.forceStopPackage(lastPackageName);
                    }
                } catch (Exception e) {}
            }
            systemUtils.showSystemUIRestartDialog(getContext());
            return true;
        } else if (preference == mTileAnimationStyle) {
            int value = Integer.parseInt((String) newValue);
            updateTileAnimStyle(value);
            return true;
        }
        return false;
    }

    private void updateTileAnimStyle(int tileAnimationStyle) {
        mTileAnimationDuration.setEnabled(tileAnimationStyle != 0);
        mTileAnimationInterpolator.setEnabled(tileAnimationStyle != 0);
    }

    private boolean isSplitShadeEnabled() {
        return mThemeUtils.isOverlayEnabled(QS_SPLIT_SHADE_LAYOUT_PKG);
    }
    private void updateSplitShadeState(boolean enable) {
        mThemeUtils.setOverlayEnabled(
                QS_SPLIT_SHADE_LAYOUT_CTG,
                enable ? QS_SPLIT_SHADE_LAYOUT_PKG : QS_SPLIT_SHADE_LAYOUT_TARGET,
                QS_SPLIT_SHADE_LAYOUT_TARGET);
    }

    private void updateQsStyle(Context context) {
        ContentResolver resolver = context.getContentResolver();

        boolean isA11Style = Settings.System.getIntForUser(resolver,
                Settings.System.QS_TILE_UI_STYLE , 0, UserHandle.USER_CURRENT) != 0;

	    String qsUIStyleCategory = "android.theme.customization.qs_ui";
        String overlayThemeTarget  = "com.android.systemui";
        String overlayThemePackage  = "com.android.system.qs.ui.A11";

        if (mThemeUtils == null) {
            mThemeUtils = ThemeUtils.getInstance(context);
        }

	    // reset all overlays before applying
        mThemeUtils.setOverlayEnabled(qsUIStyleCategory, overlayThemeTarget, overlayThemeTarget);

	    if (isA11Style) {
            mThemeUtils.setOverlayEnabled(qsUIStyleCategory, overlayThemePackage, overlayThemeTarget);
	    }
    }

    private void updateQsPanelStyle(Context context) {
        ContentResolver resolver = context.getContentResolver();

        int qsPanelStyle = Settings.System.getIntForUser(resolver,
                Settings.System.QS_PANEL_STYLE, 0, UserHandle.USER_CURRENT);

        String qsPanelStyleCategory = "android.theme.customization.qs_panel";
        String overlayThemeTarget  = "com.android.systemui";
        String overlayThemePackage  = "com.android.systemui";

        switch (qsPanelStyle) {
            case 1:
              overlayThemePackage = "com.android.system.qs.outline";
              break;
            case 2:
            case 3:
              overlayThemePackage = "com.android.system.qs.twotoneaccent";
              break;
            case 4:
              overlayThemePackage = "com.android.system.qs.shaded";
              break;
            case 5:
              overlayThemePackage = "com.android.system.qs.cyberpunk";
              break;
            case 6:
              overlayThemePackage = "com.android.system.qs.neumorph";
              break;
            case 7:
              overlayThemePackage = "com.android.system.qs.reflected";
              break;
            case 8:
              overlayThemePackage = "com.android.system.qs.surround";
              break;
            case 9:
              overlayThemePackage = "com.android.system.qs.thin";
              break;
            default:
              break;
        }

        if (mThemeUtils == null) {
            mThemeUtils = ThemeUtils.getInstance(context);
        }

        // reset all overlays before applying
        mThemeUtils.setOverlayEnabled(qsPanelStyleCategory, overlayThemeTarget, overlayThemeTarget);

        if (qsPanelStyle > 0) {
            mThemeUtils.setOverlayEnabled(qsPanelStyleCategory, overlayThemePackage, overlayThemeTarget);
        }
    }

    private void checkQSOverlays(Context context) {
        ContentResolver resolver = context.getContentResolver();
        int isA11Style = Settings.System.getIntForUser(resolver,
                Settings.System.QS_TILE_UI_STYLE , 0, UserHandle.USER_CURRENT);
        int qsPanelStyle = Settings.System.getIntForUser(resolver,
                Settings.System.QS_PANEL_STYLE , 0, UserHandle.USER_CURRENT);

        // Update summaries
        int index = mQsUI.findIndexOfValue(Integer.toString(isA11Style));
        mQsUI.setValue(Integer.toString(isA11Style));
        mQsUI.setSummary(mQsUI.getEntries()[index]);

        index = mQsPanelStyle.findIndexOfValue(Integer.toString(qsPanelStyle));
        mQsPanelStyle.setValue(Integer.toString(qsPanelStyle));
        mQsPanelStyle.setSummary(mQsPanelStyle.getEntries()[index]);
    }

    @Override
    public int getMetricsCategory() {
        return METRICS_CATEGORY_UNKNOWN;
    }

    @Override
    protected String getLogTag() {
        return TAG;
    }

    @Override
    protected int getPreferenceScreenResId() {
        return R.xml.qs_panel_settings;
    }

    public static final BaseSearchIndexProvider SEARCH_INDEX_DATA_PROVIDER =
            new BaseSearchIndexProvider(R.xml.qs_panel_settings);
}