/*
 * Copyright (C) 2026 The Android Open Source Project
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

package com.google.android.samples.socialite.fcm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.samples.socialite.repository.NOTIFICATION_DISMISSED
import com.google.android.samples.socialite.repository.NOTIFICATION_ID

/**
 * Receiver for handling notification dismissals set via
 * Notification$Builder#setDeleteIntent(Intent).
 *
 * In a multi-device world (mobile, large screen), notification
 * dismissal on one client should propagate to all other clients
 * to dismiss the notification.
 */
class MessagingBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == NOTIFICATION_DISMISSED) {
            intent.extras?.getInt(NOTIFICATION_ID)?.let {
                Log.d(
                    MessagingBroadcastReceiver::class::simpleName.toString(),
                    "Dismissing notification w/ ID $it",
                )
                // Here is where your app server is notified of a notification dismissal.
                // The app server can then talk to FCM backend to route a message to
                // all clients to dismiss/cancel the notification.
                // See sending messages to devices in
                // https://firebase.google.com/docs/cloud-messaging/server-environment
            }
        }
    }
}
