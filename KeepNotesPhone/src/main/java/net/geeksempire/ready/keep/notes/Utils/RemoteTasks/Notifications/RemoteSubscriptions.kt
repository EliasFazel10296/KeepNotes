/*
 * Copyright © 2021 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 4/12/21 8:50 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geeksempire.ready.keep.notes.Utils.RemoteTasks.Notifications

import com.google.firebase.messaging.FirebaseMessaging

class RemoteSubscriptions {

    fun subscribe(topicToSubscribe: String) {

        FirebaseMessaging.getInstance().subscribeToTopic(topicToSubscribe)

    }

}