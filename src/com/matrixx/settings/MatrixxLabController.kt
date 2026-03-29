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
import androidx.preference.Preference
import androidx.preference.PreferenceScreen
import com.android.settings.R
import com.android.settingslib.core.AbstractPreferenceController
import com.android.settingslib.widget.LayoutPreference

class MatrixxLabController(context: Context) : AbstractPreferenceController(context) {

    override fun displayPreference(screen: PreferenceScreen) {
        super.displayPreference(screen)
        
        val matrixxPreference = screen.findPreference<LayoutPreference>(KEY_MATRIXX)!!

        val clickMap = mapOf(
            R.id.themes_card to Intent().setComponent(ComponentName("com.android.settings", "com.android.settings.Settings\$MatrixxUserInterfaceActivity")),
            R.id.system_themes to Intent().setComponent(ComponentName("com.android.settings", "com.android.settings.Settings\$MatrixxStatusBarActivity")),
            R.id.toolbox to Intent().setComponent(ComponentName("com.android.settings", "com.android.settings.Settings\$MatrixxQuickSettingsActivity")),
            R.id.clock_face to Intent().setComponent(ComponentName("com.android.settings", "com.android.settings.Settings\$MatrixxLockscreenActivity")),
            R.id.spoof to Intent().setComponent(ComponentName("com.android.settings", "com.android.settings.Settings\$MatrixxMiscellaneousActivity")),
            R.id.bottom_options to Intent().setComponent(ComponentName("com.android.settings", "com.android.settings.Settings\$MatrixxSoundActivity")),
            R.id.statusbar to Intent().setComponent(ComponentName("com.android.settings", "com.android.settings.Settings\$MatrixxNotificationsActivity")),
            R.id.button_options to Intent().setComponent(ComponentName("com.android.settings", "com.android.settings.Settings\$MatrixxButtonsActivity")),
            R.id.about to Intent().setComponent(ComponentName("com.android.settings", "com.android.settings.Settings\$MatrixxAboutActivity"))
       )

        clickMap.forEach { (id, intent) ->
            matrixxPreference.findViewById<View>(id)?.setOnClickListener {
                mContext.startActivity(intent)
            }
       }
    }

    override fun isAvailable(): Boolean {
        return true
    }

    override fun getPreferenceKey(): String {
        return KEY_MATRIXX
    }

    companion object {
        private const val KEY_MATRIXX = "matrixx_dashboard_quick_access"
    }
}
