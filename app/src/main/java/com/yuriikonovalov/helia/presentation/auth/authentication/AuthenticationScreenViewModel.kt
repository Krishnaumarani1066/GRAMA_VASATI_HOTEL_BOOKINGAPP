package com.yuriikonovalov.helia.presentation.auth.authentication

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yuriikonovalov.helia.domain.usecases.SignInWithGoogleUseCase
import com.yuriikonovalov.helia.domain.valueobjects.Token
import com.yuriikonovalov.helia.utils.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthenticationScreenViewModel @Inject constructor(
    private val signInWithGoogle: SignInWithGoogleUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(AuthenticationUiState())
    val state = _state.asStateFlow()

    fun handleIntent(intent: AuthenticationIntent) = when (intent) {
        is AuthenticationIntent.SignInWithGoogleToken -> handleSignInWithGoogleToken(intent.token)
        AuthenticationIntent.LaunchSigningInWithGoogle -> _state.update { it.launchSignInWithGoogle() }
        AuthenticationIntent.SigningInWithGoogleLaunched -> _state.update { it.signInWithGoogleLaunched() }
        AuthenticationIntent.SignInWithGoogleErrorShown -> _state.update { it.signInWithGoogleErrorShown() }
        AuthenticationIntent.CancelSigningInWithGoogle -> _state.update { it.cancelSigningInWithGoogle() }
    }

    @Suppress("MoveVariableDeclarationIntoWhen")
    private fun handleSignInWithGoogleToken(token: String?) {
        if (token == null) {
            _state.update { it.signInWithGoogleError() }
            return
        }
        viewModelScope.launch {
            val result = signInWithGoogle(Token(token))
            when (result) {
                is Result.Success -> {
                    if (result.data.isNewUser) {
                        _state.update { it.signUpNewUser() }      // new user → fill profile
                    } else {
                        _state.update { it.signInExistingUser() } // ✅ existing user → home
                    }
                }
                is Result.Error -> _state.update { it.signInWithGoogleError() }
            }
        }
    }
}