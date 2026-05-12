package com.yuriikonovalov.helia.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yuriikonovalov.helia.domain.usecases.GetAppPreferencesUseCase
import com.yuriikonovalov.helia.domain.valueobjects.Theme
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val getAppPreferences: GetAppPreferencesUseCase,
) : ViewModel() {
    private val _state = MutableStateFlow(MainActivityUiState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            collectAppPreferences()
        }
    }

    // ✅ FIXED: Removed signOutIfNotRemembered() — it was signing the user
    // out every time the app started because rememberMe defaulted to false,
    // forcing repeated logins. Firebase already manages session persistence.
    private suspend fun collectAppPreferences() {
        getAppPreferences()
            .collect { appPreferences ->
                _state.update { oldState ->
                    oldState.copy(
                        isLoading = false,
                        theme = appPreferences.theme,
                        showOnboarding = appPreferences.showOnboarding
                    )
                }
            }
    }

    fun handleIntent(intent: MainActivityIntent) = when (intent) {
        is MainActivityIntent.AuthStateChange -> handleAuthStateChange(intent.isUserLoggedIn)
    }

    private fun handleAuthStateChange(userLoggedIn: Boolean) {
        _state.update { oldState ->
            oldState.copy(userLoggedIn = userLoggedIn)
        }
    }
}


data class MainActivityUiState(
    val isLoading: Boolean = true,
    val theme: Theme = Theme.LIGHT,
    val showOnboarding: Boolean = true,
    val userLoggedIn: Boolean = false
)

sealed interface MainActivityIntent {
    data class AuthStateChange(val isUserLoggedIn: Boolean) : MainActivityIntent
}