package com.couchbase.learningpath.ui.profile

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.couchbase.lite.Blob
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

import com.couchbase.learningpath.services.AuthenticationService
import com.couchbase.learningpath.R
import com.couchbase.learningpath.data.userprofile.UserProfileRepository
import com.couchbase.learningpath.models.User

class UserProfileViewModel(
    application: Application,
    private val repository: UserProfileRepository,
    private val authService: AuthenticationService
) : AndroidViewModel(application) {

    //track our fields in our composable
    var givenName by mutableStateOf("")
        private set

    var surname by mutableStateOf("")
        private set

    var jobTitle by mutableStateOf("")
        private set

    var emailAddress by mutableStateOf("")
        private set

    var team by mutableStateOf("")
        private set

    var toastMessage by mutableStateOf("")
        private set

    var profilePic by mutableStateOf(defaultProfilePic())
        private set

    private fun defaultProfilePic(): Bitmap {
        return BitmapFactory.decodeResource(getApplication<Application>().resources, R.drawable.profile_placeholder)
    }

    private val userObserver: (User?) -> Unit = { currentUser ->
        currentUser?.let { authenticatedUser ->
            emailAddress = authenticatedUser.username
            team = authenticatedUser.team
            //when getting information from the database need to make sure
            //to use Dispatchers.IO so that Disk I/O work isn't done on the main thread
            viewModelScope.launch(Dispatchers.IO) {
                val userProfile = repository.get(authenticatedUser.username)
                //make sure when we update the UI we update on the Main Thread
                withContext(Dispatchers.Main) {
                    givenName = userProfile["givenName"] as? String ?: ""
                    surname = userProfile["surname"] as? String ?: ""
                    jobTitle = userProfile["jobTitle"] as? String ?: ""
                    profilePic = (userProfile["imageData"] as? Blob)?.let { blob ->
                        val d = Drawable.createFromStream(blob.contentStream, "res")
                        d?.toBitmap()
                    } ?: defaultProfilePic()
                }
            }
        }
    }

    init {
        authService.currentUser.observeForever(userObserver)
    }

    override fun onCleared() {
        authService.currentUser.removeObserver(userObserver)
    }

    val onGivenNameChanged: (String) -> Unit = { newValue ->
        givenName = newValue
    }

    val onSurnameChanged: (String) -> Unit = { newValue ->
        surname = newValue
    }

    val onJobTitleChanged: (String) -> Unit = { newValue ->
        jobTitle = newValue
    }

    val onProfilePicChanged: (Bitmap) -> Unit = { newValue ->
        viewModelScope.launch(Dispatchers.Main) {
            profilePic = newValue
        }
    }

    val clearToastMessage: () -> Unit = {
        toastMessage = ""
    }

    val onSave: () -> Unit = {
        //when saving information to the database need to make sure
        //to use Dispatchers.IO so that Disk I/O work isn't done on the main thread
        viewModelScope.launch(Dispatchers.IO) {
            val profile = HashMap<String, Any>()
            profile["givenName"] = givenName
            profile["surname"] = surname
            profile["jobTitle"] = jobTitle
            profile["email"] = emailAddress
            profile["team"] = team
            profile["documentType"] = "user"
            val outputStream = ByteArrayOutputStream()
            profilePic.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            profile["imageData"] = Blob("image/jpeg", outputStream.toByteArray())
            val didSave = repository.save(profile)

            //make sure when we update the UI we update on the Main Thread
            withContext(Dispatchers.Main) {
                toastMessage = if (didSave) {
                    "Successfully updated profile"
                } else {
                    "Error saving, try again later."
                }
            }
        }
    }
}