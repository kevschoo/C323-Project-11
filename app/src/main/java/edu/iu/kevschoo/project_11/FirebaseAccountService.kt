package edu.iu.kevschoo.project_11

import android.util.Log
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import edu.iu.kevschoo.project_11.model.User
import java.util.Date

class FirebaseAccountService : AccountService
{

    private val auth: FirebaseAuth = Firebase.auth
    /**
     * Provides a Flow of the current authenticated User, or null if no user is authenticated
     *
     * @return A Flow emitting the current User object or null
     */
    override val currentUser: Flow<User?>
        get() = callbackFlow {
            val listener = FirebaseAuth.AuthStateListener { auth ->
                val firebaseUser = auth.currentUser
                if (firebaseUser != null) {
                    FirebaseFirestore.getInstance().collection("users").document(firebaseUser.uid)
                        .get()
                        .addOnSuccessListener { documentSnapshot ->
                            val user = documentSnapshot.toObject(User::class.java)
                            trySend(user).isSuccess
                        }
                } else {
                    trySend(null).isSuccess
                }
            }
            auth.addAuthStateListener(listener)
            awaitClose { auth.removeAuthStateListener(listener) }
        }
    /**
     * Returns the UID of the current authenticated user, or an empty string if no user is authenticated
     *
     * @return The current user's UID, or an empty string if no user is authenticated
     */
    override val currentUserId: String
        get() = auth.currentUser?.uid.orEmpty()
    /**
     * Checks if a user is currently authenticated
     *
     * @return A Boolean indicating whether a user is currently authenticated
     */
    override fun hasUser(): Boolean {return auth.currentUser != null }
    /**
     * Signs in a user with the provided email and password
     *
     * @param email The email address of the user
     * @param password The password for the account
     */
    override suspend fun signIn(email: String, password: String) { auth.signInWithEmailAndPassword(email, password).await() }
    /**
     * Signs up a new user with the provided email and password, and creates a new user profile in Firestore
     *
     * @param email The email address for the new user
     * @param password The password for the new account
     */
    override suspend fun signUp(email: String, password: String) {
        val userCredential = auth.createUserWithEmailAndPassword(email, password).await()
        val firebaseUser = userCredential.user
        firebaseUser?.let { user ->
            val newUser = User(
                id = user.uid,
                name = "No Name",
                lastname = "No Last Name",
                email = user.email ?: "",
                signUpDate = Date(user.metadata?.creationTimestamp ?: 0)
            )
            FirebaseFirestore.getInstance().collection("users").document(user.uid)
                .set(newUser)
                .addOnSuccessListener { Log.d("FirebaseAccountService", "User profile created") }
                .addOnFailureListener { e -> Log.e("FirebaseAccountService", "Error creating user profile", e) }
        }
    }
    /**
     * Signs out the currently authenticated user
     */
    override suspend fun signOut() { auth.signOut() }
    /**
     * Deletes the account of the currently authenticated user
     */
    override suspend fun deleteAccount() { auth.currentUser?.delete()?.await() }
}

