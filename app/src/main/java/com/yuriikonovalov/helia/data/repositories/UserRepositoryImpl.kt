package com.yuriikonovalov.helia.data.repositories

import android.net.Uri
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.yuriikonovalov.helia.data.auth.AuthService
import com.yuriikonovalov.helia.data.database.user.UserDatabaseService
import com.yuriikonovalov.helia.data.storage.Storage
import com.yuriikonovalov.helia.domain.entities.User
import com.yuriikonovalov.helia.domain.repositories.UserRepository
import com.yuriikonovalov.helia.domain.usecases.SignInWithGoogleResult
import com.yuriikonovalov.helia.domain.valueobjects.Gender
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

class UserRepositoryImpl @Inject constructor(
    private val auth: AuthService,
    private val database: UserDatabaseService,
    private val storage: Storage
) : UserRepository {

    override val currentUser: FirebaseUser?
        get() = auth.currentUser

    // ✅ FIXED: Safe uid — throws a clear exception instead of crashing with NPE
    private val uid: String
        get() = currentUser?.uid ?: throw IllegalStateException("User not logged in")

    // ✅ FIXED: Safe email
    private val email: String
        get() = currentUser?.email ?: throw IllegalStateException("User has no email")

    override suspend fun getUser(): User {
        return database.getUser(uid)!!
    }

    override fun observeUser(): Flow<User> {
        // ✅ FIXED: Return empty flow instead of crashing if user is null
        val safeUid = currentUser?.uid ?: return emptyFlow()
        return database
            .observeUser(safeUid)
            .filterNotNull()
    }

    override suspend fun authenticateWithGoogle(token: String): SignInWithGoogleResult? {
        val credential = GoogleAuthProvider.getCredential(token, null)
        return try {
            val authResult = auth.signInWithCredential(credential)
            // ✅ FIXED: Safe isNewUser
            val isNewUser = authResult.additionalUserInfo?.isNewUser ?: false
            SignInWithGoogleResult(isNewUser = isNewUser)
        } catch (e: Exception) {
            e.printStackTrace()
            if (e is CancellationException) throw e
            null
        }
    }

    override suspend fun signInWithEmailAndPassword(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
    }

    override suspend fun signUpWithEmailAndPassword(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
    }

    override suspend fun getSignInMethodsForEmail(email: String): List<String> {
        val signInMethodQueryResult = auth.fetchSignInMethodsForEmail(email)
        return signInMethodQueryResult.signInMethods ?: emptyList()
    }

    override suspend fun uploadPhoto(uri: Uri): Uri {
        // ✅ FIXED: Safe uid
        return storage.uploadPhoto(uid, uri)
    }

    override suspend fun deletePhoto() {
        storage.deletePhoto(uid)
    }

    override suspend fun signOut() {
        auth.signOut()
    }

    override suspend fun updateUser(
        fullName: String?,
        dateOfBirth: LocalDate?,
        gender: Gender?
    ) {
        val user = User(
            email = email,
            fullName = fullName,
            photoUri = null,
            gender = gender,
            dateOfBirth = dateOfBirth
        )
        database.updateUser(uid, user)
    }

    override suspend fun setNameAndEmailFromCurrentUser() {
        database.setUserNameAndEmail(
            id = uid,
            fullName = currentUser?.displayName,
            // ✅ FIXED: Safe email with fallback
            email = currentUser?.email ?: ""
        )
    }

    override suspend fun updateUserPhoto(photoUri: Uri?) {
        database.updateUserPhoto(uid, photoUri)
    }

    override fun observeUserPhoto(): Flow<Uri?> {
        // ✅ FIXED: Safe uid
        val safeUid = currentUser?.uid ?: return emptyFlow()
        return database.observeUserPhoto(safeUid)
    }

    override suspend fun deleteProfile() {
        database.deleteUserDocument(uid)
        storage.deletePhoto(uid)
        auth.currentUser?.delete()
    }

    override suspend fun updatePassword(currentPassword: String, password: String) {
        val credential = EmailAuthProvider.getCredential(email, currentPassword)
        auth.currentUser?.reauthenticate(credential)?.await()
        // ✅ FIXED: Safe currentUser
        auth.currentUser?.updatePassword(currentPassword)
    }
}