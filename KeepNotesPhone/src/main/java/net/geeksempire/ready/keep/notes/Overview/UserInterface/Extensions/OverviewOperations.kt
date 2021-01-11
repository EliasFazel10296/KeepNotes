package net.geeksempire.ready.keep.notes.Overview.UserInterface.Extensions

import android.util.Log
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import kotlinx.coroutines.*
import net.geeksempire.ready.keep.notes.KeepNoteApplication
import net.geeksempire.ready.keep.notes.Overview.UserInterface.KeepNoteOverview
import net.geeksempire.ready.keep.notes.R

fun KeepNoteOverview.startDatabaseOperation() = CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {

    val notesRoomDatabaseConfiguration = (application as KeepNoteApplication).notesRoomDatabaseConfiguration

    val allNotesData = notesRoomDatabaseConfiguration
        .prepareRead()
        .getAllNotesData()

    databaseSize = allNotesData.size.toLong()
    databaseTime = noteDatabaseConfigurations.lastTimeDatabaseUpdate()

    notesOverviewViewModel.notesDatabaseQuerySnapshots.postValue(allNotesData)

    notesRoomDatabaseConfiguration.closeDatabase()

}

fun KeepNoteOverview.databaseOperationsCheckpoint() = CoroutineScope(SupervisorJob() + Dispatchers.IO).async {

    val notesRoomDatabaseConfiguration = (application as KeepNoteApplication).notesRoomDatabaseConfiguration

    val newDatabaseSize = notesRoomDatabaseConfiguration.prepareRead()
        .getSizeOfDatabase().toLong()

    if (newDatabaseSize > databaseSize
        || noteDatabaseConfigurations.lastTimeDatabaseUpdate() > databaseTime) {
        Log.d(this@databaseOperationsCheckpoint.javaClass.simpleName, "New Note Added Into Database")

        startDatabaseOperation()

    }

    notesRoomDatabaseConfiguration.closeDatabase()

}

/**
 * - False to Load Evey Time Database Changed
 * - True Just Add Database Change Listener
 **/
fun KeepNoteOverview.startNetworkOperation() {

    firebaseUser?.let {

        //

    }

}

fun KeepNoteOverview.loadUserAccountInformation() {

    firebaseUser?.let {

        Glide.with(applicationContext)
            .load(firebaseUser.photoUrl)
            .error(getDrawable(R.drawable.not_login_icon))
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .transform(CircleCrop())
            .into(overviewLayoutBinding.profileImageView)

    }

}