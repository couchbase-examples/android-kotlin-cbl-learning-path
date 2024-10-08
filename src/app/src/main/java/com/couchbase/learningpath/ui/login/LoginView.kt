package com.couchbase.learningpath.ui.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.accompanist.drawablepainter.rememberDrawablePainter

import com.couchbase.learningpath.R
import com.couchbase.learningpath.ui.theme.LearningPathTheme
import com.couchbase.learningpath.ui.theme.Red500

@Composable
fun LoginView(onSuccessLogin: () -> Unit,
              viewModel:LoginViewModel) {
    val username = viewModel.username.observeAsState("")
    val password = viewModel.password.observeAsState("")
    val isError = viewModel.isError.observeAsState(false)

    //****
    //checks authentication if works, route to UserProfile
    //****
    val onLoginCheck: () -> Unit = {
        if (viewModel.login()) {
            onSuccessLogin()
        }
    }

    //setup clean view with LoginWindow
    LearningPathTheme {
        Surface(
            color = MaterialTheme.colors.background,
            modifier = Modifier.fillMaxSize()
        ) {
            LoginWindow(
                username = username.value,
                password = password.value,
                isLoginError = isError.value,
                onUsernameChanged = viewModel.onUsernameChanged,
                onPasswordChanged = viewModel.onPasswordChanged,
                login = onLoginCheck
            )
        }
    }
}

@Composable
fun LoginWindow(
    username: String,
    password: String,
    isLoginError: Boolean,
    onUsernameChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    login: () -> Unit
) {
    val context = LocalContext.current

    //****
    //used for tests - set in the semantics - contentDescription
    //https://developer.android.com/jetpack/compose/testing
    //****
    val usernameResource = stringResource(id = R.string.tfUsername)
    val passwordResource = stringResource(id = R.string.tfPassword)
    val loginResource = stringResource(id = R.string.btnLogin)
    val loginErrorResource = stringResource(id = R.string.loginError)

    //hide the keyboard when we are done editing
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            rememberDrawablePainter(drawable = ContextCompat.getDrawable(context, R.drawable.logo)),
            contentDescription = "Logo",
            modifier = Modifier
                .padding(bottom = 32.dp)
                .clickable {
                    onUsernameChanged("demo@example.com")
                    onPasswordChanged("P@ssw0rd12")
                }
        )
        OutlinedTextField(
            modifier = Modifier.semantics { contentDescription = usernameResource },
            value = username,
            onValueChange = { onUsernameChanged(it) },
            label = { Text(usernameResource) },
            keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
            keyboardOptions = KeyboardOptions.Default.copy(
                capitalization = KeyboardCapitalization.None,
                autoCorrect = false,
                keyboardType = KeyboardType.Email
            )
        )
        OutlinedTextField(
            modifier = Modifier
                .padding(top = 16.dp)
                .semantics { contentDescription = passwordResource },
            value = password,
            onValueChange = { onPasswordChanged(it) },
            label = { Text(passwordResource) },
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(onDone = {
                focusManager.clearFocus()
                login()
            })
        )
        Button(modifier = Modifier
            .padding(top = 32.dp)
            .semantics { contentDescription = loginResource },
            colors = ButtonDefaults.buttonColors(backgroundColor = Red500),
            onClick = {
                login()
            })
        {
            Text(
                loginResource,
                style = MaterialTheme.typography.h5,
                color = Color.White
            )
        }
        if (isLoginError) {
            Text(
                modifier = Modifier.padding(top = 20.dp),
                style = MaterialTheme.typography.subtitle1,
                color = MaterialTheme.colors.error,
                text = loginErrorResource
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    val username = ""
    val password = ""
    val isError = false

    LearningPathTheme {
        LoginWindow(
            username = username,
            password = password,
            isLoginError = isError,
            onUsernameChanged = { },
            onPasswordChanged = { },
            login = { })
    }
}