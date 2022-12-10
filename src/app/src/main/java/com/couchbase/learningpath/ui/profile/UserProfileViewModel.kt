package com.couchbase.learningpath.ui.profile

import android.app.Application
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
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
    private val authService: AuthenticationService
) : AndroidViewModel(application) {

    //track our fields in our composable
    private val _givenName = MutableLiveData("")
    val givenName: LiveData<String> = _givenName

    private val _surname = MutableLiveData("")
    val surname: LiveData<String> = _surname

    private val _jobTitle = MutableLiveData("")
    val jobTitle: LiveData<String> = _jobTitle

    private val _emailAddress = MutableLiveData("")
    val emailAddress: LiveData<String> = _emailAddress

    private val _team = MutableLiveData("")
    val team: LiveData<String> = _team

    private val _toastMessage = MutableLiveData("")
    val toastMessage: LiveData<String> = _toastMessage

    private val _profilePic = MutableLiveData<Bitmap?>(null)
    val profilePic: LiveData<Bitmap?> = _profilePic

    private val userObserver: (User?) -> Unit = { currentUser ->
        currentUser?.let { authenticatedUser ->
            _emailAddress.value = authenticatedUser.username
            _team.value = authenticatedUser.team
            //when getting information from the database need to make sure
            //to use Dispatchers.IO so that Disk I/O work isn't done on the main thread
            viewModelScope.launch(Dispatchers.IO) {
                val userProfile = repository.get(authenticatedUser.username)
                //make sure when we update the UI we update on the Main Thread
                withContext(Dispatchers.Main) {
                    userProfile["givenName"]?.let {
                        _givenName.value = userProfile["givenName"] as String
                    }
                    userProfile["surname"]?.let {
                        _surname.value = userProfile["surname"] as String
                    }
                    userProfile["jobTitle"]?.let {
                        _jobTitle.value = userProfile["jobTitle"] as String
                    }
                    userProfile["imageData"]?.let {
                        val blob = userProfile["imageData"] as Blob
                        val d = Drawable.createFromStream(blob.contentStream, "res")
                        _profilePic.value = d?.toBitmap()
                    }
                }
            }
        }
    }

    init {
        authService.currentUser.observeForever(userObserver)
        _profilePic.value = BitmapFactory.decodeResource(getApplication<Application>().resources, R.drawable.profile_placeholder)
    }

    override fun onCleared() {
        authService.currentUser.removeObserver(userObserver)
    }

    val onGivenNameChanged: (String) -> Unit = { newValue ->
        _givenName.value = newValue
    }

    val onSurnameChanged: (String) -> Unit = { newValue ->
        _surname.value = newValue
    }

    val onJobTitleChanged: (String) -> Unit = { newValue ->
        _jobTitle.value = newValue
    }

    val onProfilePicChanged: (Bitmap) -> Unit = { newValue ->
        viewModelScope.launch(Dispatchers.Main) {
            _profilePic.value = newValue
        }
    }

    val clearToastMessage: () -> Unit = {
        _toastMessage.value = ""
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
                    _toastMessage.value = "Successfully updated profile"
                } else {
                    _toastMessage.value = "Error saving, try again later."
                }
            }
        }
    }
}