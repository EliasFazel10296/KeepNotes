/*
 * Copyright © 2021 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 4/12/21 8:50 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geeksempire.ready.keep.notes

import android.Manifest
import android.app.ActivityOptions
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.offline_indicator.view.*
import net.geeksempire.ready.keep.notes.AccountManager.SignInProcess.SetupAccount
import net.geeksempire.ready.keep.notes.AccountManager.Utils.UserInformationIO
import net.geeksempire.ready.keep.notes.Browser.BuiltInBrowser
import net.geeksempire.ready.keep.notes.Notes.Taking.TakeNote
import net.geeksempire.ready.keep.notes.Overview.UserInterface.KeepNoteOverview
import net.geeksempire.ready.keep.notes.SecurityConfiguratios.Biometric.BiometricAuthentication
import net.geeksempire.ready.keep.notes.SecurityConfiguratios.Checkpoint.SecurityCheckpoint
import net.geeksempire.ready.keep.notes.Utils.Network.NetworkCheckpoint
import net.geeksempire.ready.keep.notes.Utils.Network.NetworkConnectionListener
import net.geeksempire.ready.keep.notes.Utils.Network.NetworkConnectionListenerInterface
import net.geeksempire.ready.keep.notes.Utils.UI.NotifyUser.SnackbarActionHandlerInterface
import net.geeksempire.ready.keep.notes.Utils.UI.NotifyUser.SnackbarBuilder
import net.geeksempire.ready.keep.notes.databinding.EntryConfigurationLayoutBinding
import java.util.*
import javax.inject.Inject

class EntryConfigurations : AppCompatActivity(), NetworkConnectionListenerInterface {

    private val securityCheckpoint: SecurityCheckpoint by lazy {
        SecurityCheckpoint(applicationContext)
    }

    private val userInformationIO: UserInformationIO by lazy {
        UserInformationIO(applicationContext)
    }

    lateinit var signInSuccessListener: OnSuccessListener<AuthResult>
    lateinit var signInFailureListener: OnFailureListener

    @Inject lateinit var networkCheckpoint: NetworkCheckpoint

    @Inject lateinit var networkConnectionListener: NetworkConnectionListener

    lateinit var entryConfigurationLayoutBinding: EntryConfigurationLayoutBinding

    companion object {
        const val PermissionRequestCode: Int = 123
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        entryConfigurationLayoutBinding = EntryConfigurationLayoutBinding.inflate(layoutInflater)
        setContentView(entryConfigurationLayoutBinding.root)

        (application as KeepNoteApplication)
            .dependencyGraph
            .subDependencyGraph()
            .create(this@EntryConfigurations, entryConfigurationLayoutBinding.rootView)
            .inject(this@EntryConfigurations)

        networkConnectionListener.networkConnectionListenerInterface = this@EntryConfigurations

        if (networkCheckpoint.networkConnection()) {

            if (userInformationIO.readPrivacyAgreement()
                && checkAllPermissions()
                && Firebase.auth.currentUser != null) {

                entryConfigurationLayoutBinding.agreementDataView.visibility = View.INVISIBLE
                entryConfigurationLayoutBinding.proceedButton.visibility = View.INVISIBLE

                openOverviewActivity()

            } else if (userInformationIO.readPrivacyAgreement()) {

                entryConfigurationLayoutBinding.agreementDataView.visibility = View.INVISIBLE
                entryConfigurationLayoutBinding.proceedButton.visibility = View.INVISIBLE

                runtimePermission()

            } else {

                window.setBackgroundDrawable(getDrawable(R.drawable.splash_screen_initial))

                entryConfigurationLayoutBinding.agreementDataView.visibility = View.VISIBLE
                entryConfigurationLayoutBinding.proceedButton.visibility = View.VISIBLE

                entryConfigurationLayoutBinding.blurryBackground.startAnimation(AnimationUtils.loadAnimation(applicationContext, android.R.anim.fade_in))
                entryConfigurationLayoutBinding.blurryBackground.visibility = View.VISIBLE

                entryConfigurationLayoutBinding.proceedButton.setOnClickListener {

                    userInformationIO.savePrivacyAgreement()

                    Handler(Looper.getMainLooper()).postDelayed({

                        entryConfigurationLayoutBinding.agreementDataView.startAnimation(AnimationUtils.loadAnimation(applicationContext, android.R.anim.fade_out))
                        entryConfigurationLayoutBinding.agreementDataView.visibility = View.INVISIBLE

                        entryConfigurationLayoutBinding.proceedButton.startAnimation(AnimationUtils.loadAnimation(applicationContext, android.R.anim.fade_out))
                        entryConfigurationLayoutBinding.proceedButton.visibility = View.INVISIBLE

                        runtimePermission()

                    }, 333)

                }

                entryConfigurationLayoutBinding.agreementDataView.setOnClickListener {

                    BuiltInBrowser.show(
                        context = applicationContext,
                        linkToLoad = getString(R.string.privacyAgreementLink),
                        gradientColorOne = getColor(R.color.default_color_dark),
                        gradientColorTwo = getColor(R.color.default_color_game_dark)
                    )

                }

            }

        } else {

            SnackbarBuilder(applicationContext).show (
                rootView = entryConfigurationLayoutBinding.rootView,
                messageText= getString(R.string.noNetworkConnection),
                messageDuration = Snackbar.LENGTH_INDEFINITE,
                actionButtonText = android.R.string.ok,
                snackbarActionHandlerInterface = object : SnackbarActionHandlerInterface {

                    override fun onActionButtonClicked(snackbar: Snackbar) {
                        super.onActionButtonClicked(snackbar)

                        startActivity(
                            Intent(Settings.ACTION_WIFI_SETTINGS)
                                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK))

                        this@EntryConfigurations.finish()

                    }

                }
            )

        }

    }

    override fun onPause() {
        super.onPause()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissionsList: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissionsList, grantResults)

        when (requestCode) {
            EntryConfigurations.PermissionRequestCode -> {

                if (checkSelfPermission(Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.CHANGE_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.ACCESS_WIFI_STATE) == PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.CHANGE_WIFI_STATE) == PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.WAKE_LOCK) == PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.VIBRATE) == PackageManager.PERMISSION_GRANTED) {

                    val setupAccount = SetupAccount()

                    if (setupAccount.firebaseUser == null) {

                        entryConfigurationLayoutBinding.blurryBackground.visibility = View.VISIBLE

                        entryConfigurationLayoutBinding.waitingView.startAnimation(AnimationUtils.loadAnimation(applicationContext, android.R.anim.fade_in))
                        entryConfigurationLayoutBinding.waitingView.visibility = View.VISIBLE

                        entryConfigurationLayoutBinding.waitingInformationView.startAnimation(AnimationUtils.loadAnimation(applicationContext, android.R.anim.fade_in))
                        entryConfigurationLayoutBinding.waitingInformationView.visibility = View.VISIBLE

                        signInSuccessListener = OnSuccessListener<AuthResult> {

                            entryConfigurationLayoutBinding.waitingView.visibility = View.GONE
                            entryConfigurationLayoutBinding.waitingInformationView.visibility = View.GONE

                            it.user?.let { firebaseUser ->
                                userInformationIO.saveOldFirebaseUniqueIdentifier(firebaseUser.uid)
                            }

                            openTakeNoteActivity()

                        }

                        signInFailureListener = OnFailureListener { exception ->
                            exception.printStackTrace()

                            SnackbarBuilder(applicationContext).show (
                                rootView = entryConfigurationLayoutBinding.rootView,
                                messageText= getString(R.string.anonymouslySignInError),
                                messageDuration = Snackbar.LENGTH_INDEFINITE,
                                actionButtonText = R.string.retryText,
                                snackbarActionHandlerInterface = object : SnackbarActionHandlerInterface {

                                    override fun onActionButtonClicked(snackbar: Snackbar) {
                                        super.onActionButtonClicked(snackbar)

                                        invokeAccountSetup(setupAccount, signInSuccessListener, signInFailureListener)

                                    }

                                }
                            )

                        }

                        invokeAccountSetup(setupAccount, signInSuccessListener, signInFailureListener)

                    } else {

                        openOverviewActivity()

                    }

                } else {

                    runtimePermissionMessage()

                }

            }
        }

    }

    override fun networkAvailable() {



    }

    override fun networkLost() {



    }

    private fun openOverviewActivity() {

        if (securityCheckpoint.securityEnabled()) {

            BiometricAuthentication(this@EntryConfigurations)
                .startAuthenticationProcess()

        } else {

            startActivity(Intent(applicationContext, KeepNoteOverview::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }, ActivityOptions.makeCustomAnimation(applicationContext, R.anim.fade_in, android.R.anim.fade_out).toBundle())

            this@EntryConfigurations.finish()

        }

    }

    private fun openTakeNoteActivity() {

        TakeNote.open(context = applicationContext,
            incomingActivityName = EntryConfigurations::class.java.simpleName,
            extraConfigurations = TakeNote.NoteConfigurations.KeyboardTyping,
            encryptedTextContent = false)

        this@EntryConfigurations.finish()

    }

    private fun invokeAccountSetup(setupAccount: SetupAccount, signInSuccessListener: OnSuccessListener<AuthResult>, signInFailureListener: OnFailureListener) {

        setupAccount.signInAnonymously().also { signInAnonymously ->

            signInAnonymously.addOnSuccessListener(signInSuccessListener)
            signInAnonymously.addOnFailureListener(signInFailureListener)

        }

    }

    private fun runtimePermission() {

        val permissionsList = arrayListOf(
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.CHANGE_NETWORK_STATE,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_CALENDAR,
            Manifest.permission.READ_CALENDAR,
            Manifest.permission.WAKE_LOCK,
            Manifest.permission.VIBRATE
        )

        requestPermissions(
            permissionsList.toTypedArray(),
            EntryConfigurations.PermissionRequestCode
        )

    }

    private fun checkAllPermissions() : Boolean {

        return (checkSelfPermission(Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(Manifest.permission.CHANGE_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(Manifest.permission.ACCESS_WIFI_STATE) == PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(Manifest.permission.CHANGE_WIFI_STATE) == PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(Manifest.permission.WRITE_CALENDAR) == PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(Manifest.permission.WAKE_LOCK) == PackageManager.PERMISSION_GRANTED
                && checkSelfPermission(Manifest.permission.VIBRATE) == PackageManager.PERMISSION_GRANTED)
    }

    private fun runtimePermissionMessage() {

        SnackbarBuilder(applicationContext).show (
            rootView = entryConfigurationLayoutBinding.rootView,
            messageText= getString(R.string.permissionMessage),
            messageDuration = Snackbar.LENGTH_INDEFINITE,
            actionButtonText = R.string.grantPermission,
            snackbarActionHandlerInterface = object : SnackbarActionHandlerInterface {

                override fun onActionButtonClicked(snackbar: Snackbar) {
                    super.onActionButtonClicked(snackbar)

                    runtimePermission()

                }

            }
        )

    }

}