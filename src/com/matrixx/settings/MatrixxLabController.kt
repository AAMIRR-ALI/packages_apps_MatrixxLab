/*
 * Copyright (C) 2023 the RisingOS Android Project
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

package com.matrixx.settings

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.view.View
import androidx.preference.PreferenceScreen
import com.android.settings.R
import com.android.settingslib.core.AbstractPreferenceController
import com.android.settingslib.widget.LayoutPreference

class MatrixxLabController(context: Context) : AbstractPreferenceController(context) {

    private val viewCache = mutableMapOf<Int, View?>()

    override fun displayPreference(screen: PreferenceScreen) {
        super.displayPreference(screen)

        screen.findPreference<LayoutPreference>(KEY_MATRIXX)?.let { matrixxPref ->
            setupClickListeners(matrixxPref)
        }
    }

    private fun setupClickListeners(preference: LayoutPreference) {

        val clickMap = mapOf(
            R.id.themes_card to "com.android.settings.Settings\$MatrixxUserInterfaceActivity",
            R.id.system_themes to "com.android.settings.Settings\$MatrixxStatusBarActivity",
            R.id.toolbox to "com.android.settings.Settings\$MatrixxQuickSettingsActivity",
            R.id.clock_face to "com.android.settings.Settings\$MatrixxLockscreenActivity",
            R.id.spoof to "com.android.settings.Settings\$MatrixxMiscellaneousActivity",
            R.id.bottom_options to "com.android.settings.Settings\$MatrixxSoundActivity",
            R.id.statusbar to "com.android.settings.Settings\$MatrixxNotificationsActivity",
            R.id.button_options to "com.android.settings.Settings\$MatrixxButtonsActivity",
            R.id.about to "com.android.settings.Settings\$MatrixxAboutActivity"
        )

        clickMap.forEach { (viewId, activityName) ->

            // Cached lookup (same pattern as your reference)
            val view = viewCache.getOrPut(viewId) {
                preference.findViewById<View>(viewId)
            }

            view?.setOnClickListener {
                try {
                    mContext.startActivity(createIntent(activityName))
                } catch (e: Exception) {
                    // Prevent crash if activity missing
                    android.util.Log.w(
                        "MatrixxLabController",
                        "Failed to start activity: $activityName",
                        e
                    )
                }
            }
        }
    }

    private fun createIntent(activityName: String): Intent {
        return Intent().setComponent(
            ComponentName("com.android.settings", activityName)
        )
    }

    override fun isAvailable(): Boolean = true

    override fun getPreferenceKey(): String = KEY_MATRIXX

    companion object {
        private const val KEY_MATRIXX = "matrixx_dashboard_quick_access"
    }
}