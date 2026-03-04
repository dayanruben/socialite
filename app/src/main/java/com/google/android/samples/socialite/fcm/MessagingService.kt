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

import android.app.NotificationManager
import android.util.Log
import androidx.core.content.getSystemService
import com.google.android.samples.socialite.repository.NOTIFICATION_ACTION
import com.google.android.samples.socialite.repository.NOTIFICATION_ID
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

/**
 * Service for handling Firebase Cloud Messaging.
 */
class MessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        // Token used for device targeting.
        // See https://firebase.google.com/docs/cloud-messaging/android/get-started#access-fcm-registration-token
        super.onNewToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        val notificationManager: NotificationManager =
            this.getSystemService() ?: throw IllegalStateException()

        // Handle data payload
        if (remoteMessage.data.isNotEmpty()) {
            // If payload is relevant to dismissal, cancel the notification.
            // For the device that triggered this dismissal, it is noop but
            // for other devices that belongs to the user, the notification will be
            // removed.
            // Note that the below is just an example of a payload that can be
            // relevant to dismissal.
            if (remoteMessage.data[NOTIFICATION_ACTION] == "DISMISSAL" &&
                remoteMessage.data[NOTIFICATION_ID] != null
            ) {
                Log.d(
                    MessagingService::class::simpleName.toString(),
                    "Message data payload: ${remoteMessage.data}",
                )
                notificationManager.cancel(remoteMessage.data[NOTIFICATION_ID]!!.toInt())
            }

            // Handle notification payload
            remoteMessage.notification?.let {
                // Log.d("FCM", "Message Notification Body: ${it.body}")
                // Trigger local notification here
                // This notif should have unique ID as well.
            }
        }
    }

    /**
     * It is good practice to handle deleted messages in FCM
     * See https://firebase.google.com/docs/cloud-messaging/android/receive-messages#override-on-deleted-messages
     */
    override fun onDeletedMessages() {
        super.onDeletedMessages()
        // Up to the developer on how to handle this and surface this behavior to the end user.
    }
}
