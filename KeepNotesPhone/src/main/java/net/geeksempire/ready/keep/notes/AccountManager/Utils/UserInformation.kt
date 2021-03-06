/*
 * Copyright © 2021 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 4/12/21 8:50 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geeksempire.ready.keep.notes.AccountManager.Utils

import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import net.geeksempire.ready.keep.notes.AccountManager.UserInterface.AccountInformation
import net.geeksempire.ready.keep.notes.R

class UserInformation(private val context: AccountInformation) {

    companion object {
        const val GoogleSignInLinkRequestCode = 103
        const val GoogleSignInRequestCode = 105

        fun userProfileDatabasePath(userUniqueIdentifier: String) : String = "ReadyKeepNotes/UserInformation/${userUniqueIdentifier}/Profile"

        fun invitedSuccessDatabasePath(invitingFriendUniqueIdentifier: String, userUniqueIdentifier: String) : String = "ReadyKeepNotes/UserInformation/${invitingFriendUniqueIdentifier}/SuccessfulInvitations/Friends/${userUniqueIdentifier}"

    }

    fun startSignInProcess() {

        val googleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.webClientId))
            .requestEmail()
            .build()

        val googleSignInClient = GoogleSignIn.getClient(context, googleSignInOptions)
        googleSignInClient.signInIntent.run {
            context.startActivityForResult(this@run, UserInformation.GoogleSignInRequestCode)
        }

    }

}