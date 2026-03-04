/*
 * Copyright (C) 2023 The Android Open Source Project
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

package com.google.android.samples.socialite

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.KeyboardShortcutGroup
import android.view.KeyboardShortcutInfo
import android.view.Menu
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.glance.appwidget.updateAll
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.samples.socialite.ui.Main
import com.google.android.samples.socialite.widget.SociaLiteAppWidget
import com.google.firebase.messaging.FirebaseMessaging
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        enableEdgeToEdge()
        initializeFcm()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            window.isNavigationBarContrastEnforced = false
        }
        super.onCreate(savedInstanceState)
        // Avoid calling widget APIs if the platform doesn't support widgets
        if (packageManager.hasSystemFeature(PackageManager.FEATURE_APP_WIDGETS)) {
            lifecycleScope.launch { SociaLiteAppWidget().updateAll(this@MainActivity) }
        }
        setContent {
            Main(
                appArgs = extractAppArgs(intent),
            )
        }
    }

    /**
     * Sets up Firebase Cloud Messaging (FCM) for push notifications.
     * FCM enables cross device message delivery and versatile message delivery.
     * See https://firebase.google.com/docs/cloud-messaging/android/get-started.
     */
    private fun initializeFcm() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(
            OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("FCM", "Fetching FCM registration token failed", task.exception)
                    return@OnCompleteListener
                } // Get new FCM registration token.
                //  A unique key for this specific app instance and used for sending messages to
                //  this specific app instance.
                val token = task.result
                // Log token, for testing purposes only.
                // Log.d(MainActivity::class::simpleName.toString(), "FCM message token $token")
            },
        )
    }

    private fun extractAppArgs(intent: Intent?): AppArgs? {
        if (intent == null) return null
        return AppArgs.ShortcutParams.tryFrom(intent) ?: AppArgs.LaunchParams.tryFrom(intent)
    }

    override fun onProvideKeyboardShortcuts(
        data: MutableList<KeyboardShortcutGroup?>?,
        menu: Menu?,
        deviceId: Int,
    ) {
        data?.add(provideChatShortcuts())
    }

    private fun provideChatShortcuts(): KeyboardShortcutGroup? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            KeyboardShortcutGroup(
                resources.getString(R.string.chat),
                listOf(
                    KeyboardShortcutInfo(
                        resources.getString(R.string.send_message),
                        KeyEvent.KEYCODE_ENTER,
                        0,
                    ),
                    KeyboardShortcutInfo(
                        resources.getString(R.string.page_up),
                        KeyEvent.KEYCODE_PAGE_UP,
                        0,
                    ),
                    KeyboardShortcutInfo(
                        resources.getString(R.string.page_up),
                        KeyEvent.KEYCODE_DPAD_UP,
                        KeyEvent.META_SHIFT_ON,
                    ),
                    KeyboardShortcutInfo(
                        resources.getString(R.string.page_down),
                        KeyEvent.KEYCODE_PAGE_DOWN,
                        0,
                    ),
                    KeyboardShortcutInfo(
                        resources.getString(R.string.page_down),
                        KeyEvent.KEYCODE_DPAD_DOWN,
                        KeyEvent.META_SHIFT_ON,
                    ),
                ),
            )
        } else {
            null
        }
    }
}
