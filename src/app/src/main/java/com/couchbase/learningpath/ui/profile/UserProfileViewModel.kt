package com.couchbase.learningpath.ui.profile

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import androidx.compose.runtime.mutableStateOf
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.couchbase.lite.Blob
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

import com.couchbase.learningpath.data.KeyValueRepository
import com.couchbase.learningpath.services.AuthenticationService
import com.couchbase.learningpath.R
import com.couchbase.learningpath.models.User

class UserProfileViewModel(
    application: Application,
    private val repository: KeyValueRepository,
    authService: AuthenticationService
) : AndroidViewModel(application) {

    private var currentUser: User? = null

    //track our fields in our composable
    var givenName = mutableStateOf("")
    var surname = mutableStateOf("")
    var jobTitle = mutableStateOf("")
    var emailAddress = mutableStateOf("")
    var team = mutableStateOf("")
    var toastMessage = mutableStateOf("")
    var profilePic = mutableStateOf<Bitmap?>(null)

    init {
        currentUser = authService.getCurrentUser()
        updateUserProfileInfo()
        profilePic.value = BitmapFactory.decodeResource(getApplication<Application>().resources, R.drawable.profile_placeholder)
    }

    fun updateUserProfileInfo() {
        currentUser?.let { authenticatedUser ->
            emailAddress.value = authenticatedUser.username
            team.value = authenticatedUser.team
            //when getting information from the database need to make sure
            //to use Dispatchers.IO so that Disk I/O work isn't done on the main thread
            viewModelScope.launch(Dispatchers.IO) {
                val userProfile = repository.get(authenticatedUser.username)
                //make sure when we update the UI we update on the Main Thread
                withContext(Dispatchers.Main) {
                    userProfile["givenName"]?.let {
                        givenName.value = userProfile["givenName"] as String
                    }
                    userProfile["surname"]?.let {
                        surname.value = userProfile["surname"] as String
                    }
                    userProfile["jobTitle"]?.let {
                        jobTitle.value = userProfile["jobTitle"] as String
                    }
                    userProfile["imageData"]?.let {
                        val blob = userProfile["imageData"] as Blob
                        val d = Drawable.createFromStream(blob.contentStream, "res")
                        profilePic.value = d?.toBitmap()
                    }
                }
            }
        }
    }

    val onGivenNameChanged: (String) -> Unit = { newValue ->
        givenName.value = newValue
    }

    val onSurnameChanged: (String) -> Unit = { newValue ->
        surname.value = newValue
    }

    val onJobTitleChanged: (String) -> Unit = { newValue ->
        jobTitle.value = newValue
    }

    val onProfilePicChanged: (Bitmap) -> Unit = { newValue ->
        viewModelScope.launch(Dispatchers.Main) {
            profilePic.value = newValue
        }
    }

    val clearToastMessage: () -> Unit = {
        toastMessage.value = ""
    }

    val onSave: () -> Unit = {
        //when saving information to the database need to make sure
        //to use Dispatchers.IO so that Disk I/O work isn't done on the main thread
        viewModelScope.launch(Dispatchers.IO) {
            val profile = HashMap<String, Any>()
            profile["givenName"] = givenName.value as Any
            profile["surname"] = surname.value as Any
            profile["jobTitle"] = jobTitle.value as Any
            profile["email"] = emailAddress.value as Any
            profile["team"] = team.value as Any
            profile["documentType"] = "user" as Any
            profilePic.value?.let {
                val outputStream = ByteArrayOutputStream()
                it.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                profile["imageData"] =
                    Blob("image/jpeg", outputStream.toByteArray()) as Any
            }
            val didSave = repository.save(profile)

            //make sure when we update the UI we update on the Main Thread
            withContext(Dispatchers.Main) {
                if (didSave) {
                    toastMessage.value = "Successfully updated profile"
                } else {
                    toastMessage.value = "Error saving, try again later."
                }
            }
        }
    }
}