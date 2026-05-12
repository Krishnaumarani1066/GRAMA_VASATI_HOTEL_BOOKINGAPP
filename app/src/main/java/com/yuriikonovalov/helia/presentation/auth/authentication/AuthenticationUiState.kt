package com.yuriikonovalov.helia.presentation.auth.authentication

data class AuthenticationUiState(
    val loading: Boolean = false,
    val signInWithGoogle: Boolean = false,
    val signInWithGoogleError: Boolean = false,
    val signedUpNewUser: Boolean = false,
    val signedIn: Boolean = false                                        // ✅ ADDED
) {
    fun launchSignInWithGoogle() = copy(loading = true, signInWithGoogle = true)

    fun signInWithGoogleLaunched() = copy(signInWithGoogle = false)

    fun cancelSigningInWithGoogle() = copy(loading = false)

    fun signUpNewUser() = copy(loading = false, signedUpNewUser = true)

    fun signInExistingUser() = copy(loading = false, signedIn = true)   // ✅ ADDED

    fun signInWithGoogleError() = copy(loading = false, signInWithGoogleError = true)

    fun signInWithGoogleErrorShown() = copy(signInWithGoogleError = false)
}