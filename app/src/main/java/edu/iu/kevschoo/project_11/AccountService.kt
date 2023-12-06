package edu.iu.kevschoo.project_11

import edu.iu.kevschoo.project_11.model.User
import kotlinx.coroutines.flow.Flow

interface AccountService {
    /**
     * Provides a Flow of the current authenticated User, or null if no user is authenticated
     *
     * @return A Flow emitting the current User object or null
     */
    val currentUser: Flow<User?>
    /**
     * Returns the UID of the current authenticated user, or an empty string if no user is authenticated
     *
     * @return The current user's UID, or an empty string if no user is authenticated
     */
    val currentUserId: String
    /**
     * Checks if a user is currently authenticated
     *
     * @return A Boolean indicating whether a user is currently authenticated
     */
    fun hasUser(): Boolean
    /**
     * Signs in a user with the provided email and password
     * This function is a suspend function and should be called from a coroutine or another suspend function
     *
     * @param email The email address of the user
     * @param password The password for the account
     */
    suspend fun signIn(email: String, password: String)
    /**
     * Signs up a new user with the provided email and password
     * This function also creates a new user profile in the backend service
     * This function is a suspend function and should be called from a coroutine or another suspend function
     *
     * @param email The email address for the new user
     * @param password The password for the new account
     */
    suspend fun signUp(email: String, password: String)
    /**
     * Signs out the currently authenticated user
     * This function is a suspend function and should be called from a coroutine or another suspend function
     */
    suspend fun signOut()
    /**
     * Deletes the account of the currently authenticated user
     * This function is a suspend function and should be called from a coroutine or another suspend function
     */
    suspend fun deleteAccount()
}