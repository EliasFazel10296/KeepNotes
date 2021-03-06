/*
 * Copyright © 2021 By Geeks Empire.
 *
 * Created by Elias Fazel
 * Last modified 4/12/21 8:50 AM
 *
 * Licensed Under MIT License.
 * https://opensource.org/licenses/MIT
 */

package net.geeksempire.ready.keep.notes.Database.DataStructure

import androidx.annotation.Keep
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.firebase.Timestamp

@Keep
object Notes {
    const val NoteTile = "noteTile"
    const val NoteTextContent = "noteTextContent"

    const val NoteHandwritingSnapshotLink = "noteHandwritingSnapshotLink"
    const val NoteHandwritingPaintingPaths = "noteHandwritingPaintingPaths"

    const val NoteVoicePaths = "noteVoicePaths"
    const val NoteImagePaths = "noteImagePaths"
    const val NoteGifPaths = "noteGifPaths"

    const val NoteTakenTime = "noteTakenTime"
    const val NoteEditTime = "noteEditTime"

    const val NoteIndex = "noteIndex"

    const val NotesTags = "noteTags"
    const val NotesHashTags = "noteHashTags"

    const val NoteTranscribeTags = "noteTranscribeTags"

    const val NotePinned = "notePinned"
}

@Keep
object NotesTemporaryModification {
    const val NoteIsNotSelected = 0
    const val NoteIsSelected = 1

    const val NoteUnpinned = 0
    const val NotePinned = 1
}

@Keep
data class NotesDataStructure(var uniqueNoteId: Long,
                              var noteTile: String = "Untitled Note",
                              var noteTextContent: String = "No Content",
                              var noteHandwritingPaintingPaths: String? = null,
                              var noteHandwritingSnapshotLink: String? = null,
                              var noteTakenTime: Timestamp = Timestamp.now(),
                              var noteEditTime: Timestamp? = null,
                              var noteIndex: Long,
                              var noteTags: String? = null)

@Keep
data class NotesDataStructureSearch(var uniqueNoteId: Long,
                                    var noteTile: String,
                                    var noteTextContent: String,
                                    var noteHandwritingPaintingPaths: String? = null,
                                    var noteHandwritingSnapshotLink: String? = null,
                                    var noteTags: String,
                                    var noteTranscribeTags: String)

const val NotesDatabase = "NotesDatabase"

@Entity(tableName = NotesDatabase)
@Keep
data class NotesDatabaseModel(
    @NonNull @PrimaryKey var uniqueNoteId: Long,

    @Nullable @ColumnInfo(name = "noteTile", typeAffinity = ColumnInfo.TEXT) var noteTile: String?,
    @Nullable @ColumnInfo(name = "noteTextContent", typeAffinity = ColumnInfo.TEXT) var noteTextContent: String?,

    @Nullable @ColumnInfo(name = "noteHandwritingPaintingPaths", typeAffinity = ColumnInfo.TEXT) var noteHandwritingPaintingPaths: String?,
    @Nullable @ColumnInfo(name = "noteHandwritingSnapshotLink", typeAffinity = ColumnInfo.TEXT) var noteHandwritingSnapshotLink: String?,

    /**
     * Json Of Paths (Download Link) From Firestore
     **/
    @Nullable @ColumnInfo(name = "noteVoicePaths", typeAffinity = ColumnInfo.TEXT) var noteVoicePaths: String?,
    /**
     * Json Of Paths (Download Link) From Firestore
     **/
    @Nullable @ColumnInfo(name = "noteImagePaths", typeAffinity = ColumnInfo.TEXT) var noteImagePaths: String?,
    /**
     * Json Of Paths (Download Link) From Firestore
     **/
    @Nullable @ColumnInfo(name = "noteGifPaths", typeAffinity = ColumnInfo.TEXT) var noteGifPaths: String?,

    @NonNull @ColumnInfo(name = "noteTakenTime", typeAffinity = ColumnInfo.INTEGER) var noteTakenTime: Long,
    @Nullable @ColumnInfo(name = "noteEditTime", typeAffinity = ColumnInfo.INTEGER) var noteEditTime: Long?,

    @NonNull @ColumnInfo(name = "noteIndex") var noteIndex: Long,

    /**
     * Json Array Of Tags
     **/
    @Nullable @ColumnInfo(name = "noteTags", typeAffinity = ColumnInfo.TEXT) var noteTags: String?,
    /**
     * Json Array Of Hash Tags
     **/
    @Nullable @ColumnInfo(name = "noteHashTags", typeAffinity = ColumnInfo.TEXT) var noteHashTags: String?,
    /**
     * Json Of Transcribe Tags For Each Recorded Voice
     **/
    @Nullable @ColumnInfo(name = "noteTranscribeTags", typeAffinity = ColumnInfo.TEXT) var noteTranscribeTags: String?,

    @NonNull @ColumnInfo(name = "notePinned", typeAffinity = ColumnInfo.INTEGER) var notePinned: Int = NotesTemporaryModification.NoteUnpinned,
    @NonNull @ColumnInfo(name = "dataSelected", typeAffinity = ColumnInfo.INTEGER) var dataSelected: Int = NotesTemporaryModification.NoteIsNotSelected
)