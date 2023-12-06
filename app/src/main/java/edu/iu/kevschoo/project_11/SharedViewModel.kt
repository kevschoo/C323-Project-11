package edu.iu.kevschoo.project_11

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import androidx.lifecycle.switchMap
import com.google.firebase.firestore.FieldValue
import edu.iu.kevschoo.project_11.model.Property
import edu.iu.kevschoo.project_11.model.User
import kotlinx.coroutines.tasks.await

class SharedViewModel(application: Application) : AndroidViewModel(application)
{

    private val accountService: AccountService = FirebaseAccountService()
    private val storageService: StorageService = FirebaseStorageService()

    private val _authenticationState = MutableLiveData<AuthenticationState>()
    val authenticationState: LiveData<AuthenticationState>
        get() = _authenticationState

    private val _currentUser = MutableLiveData<User?>()
    val currentUser: LiveData<User?> get() = _currentUser

    private val _selectedProperty = MutableLiveData<Property>()
    val selectedProperty: LiveData<Property> = _selectedProperty

    val userTrips: LiveData<List<Property>> = _currentUser.switchMap { user ->
        liveData {
            if (user != null) {
                emitSource(storageService.fetchUserTrips(user.id).asLiveData())
            } else {
                emit(emptyList<Property>())
            }
        }
    }

    init {
        viewModelScope.launch {
            accountService.currentUser.collect { user ->
                _currentUser.value = user
                if (user != null) {
                    _authenticationState.value = AuthenticationState.AUTHENTICATED
                } else {
                    _authenticationState.value = AuthenticationState.UNAUTHENTICATED
                }
            }
        }
    }

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?>
        get() = _errorMessage
    /**
     * Signs in a user with the provided email and password
     *
     * @param email The email address of the user
     * @param password The password for the account
     */
    fun signIn(email: String, password: String)
    {
        viewModelScope.launch {
            try
            {
                accountService.signIn(email, password)
            }
            catch (e: FirebaseAuthException)
            {
                _authenticationState.value = AuthenticationState.INVALID_AUTHENTICATION
                _errorMessage.value = handleFirebaseAuthException(e)
            }
            catch (e: Exception)
            {
                _errorMessage.value = "An unexpected error occurred. Please try again later."
                Log.e("SharedViewModel", "Sign In Error: ", e)
            }
        }
    }
    /**
     * Signs up a new user with the provided email and password
     *
     * @param email The email address for the new user
     * @param password The password for the new account
     */
    fun signUp(email: String, password: String)
    {
        viewModelScope.launch {
            try
            {
                accountService.signUp(email, password)
            }
            catch (e: FirebaseAuthException)
            {
                _authenticationState.value = AuthenticationState.INVALID_AUTHENTICATION
                _errorMessage.value = handleFirebaseAuthException(e)
            }
            catch (e: Exception)
            {
                _errorMessage.value = "An unexpected error occurred. Please try again later."
                Log.e("SharedViewModel", "Sign Up Error: ", e)
            }
        }
    }
    /**
     * Reserves a property for the current user
     *
     * @param propertyId The unique identifier of the property to be reserved
     * @return A Boolean indicating whether the reservation was successful
     */
    suspend fun reserveProperty(propertyId: String): Boolean {
        val user = _currentUser.value
        return if (user != null && !user.tripLocations.contains(propertyId)) {
            try {
                FirebaseFirestore.getInstance().collection("users").document(user.id)
                    .update("tripLocations", FieldValue.arrayUnion(propertyId))
                    .await()
                true
            } catch (e: Exception) {
                Log.e("SharedViewModel", "Error reserving property", e)
                false
            }
        } else if (user == null) {
            false
        } else {
            false
        }
    }
    /**
     * Selects a property to be used in other operations
     *
     * @param property The Property object to be selected
     */
    fun selectProperty(property: Property) {
        _selectedProperty.value = property
    }
    /**
     * Handles exceptions thrown by Firebase authentication operations
     *
     * @param e The FirebaseAuthException to be handled
     * @return A String message describing the error
     */
    private fun handleFirebaseAuthException(e: FirebaseAuthException): String
    {
        return when (e.errorCode)
        {
            "ERROR_INVALID_EMAIL" -> "The email address is badly formatted."
            "ERROR_USER_DISABLED" -> "The user account has been disabled."
            "ERROR_USER_NOT_FOUND", "ERROR_WRONG_PASSWORD" -> "Invalid email or password."
            else -> "An unknown error occurred. Please try again."
        }
    }
    /**
     * Signs out the current user
     */
    fun signOut()
    {
        viewModelScope.launch {
            accountService.signOut()
            _authenticationState.value = AuthenticationState.UNAUTHENTICATED
        }
    }

    fun uploadImage(imageUri: Uri, onComplete: (Uri?) -> Unit) {storageService.uploadImage(imageUri, onComplete)}
    /**
     * Updates the user profile with a new name and last name
     *
     * @param name The new name of the user
     * @param lastName The new last name of the user
     */
    fun updateUserProfile(name: String, lastName: String) {
        viewModelScope.launch {
            val userId = FirebaseAuth.getInstance().currentUser?.uid.orEmpty()
            val userUpdates = mapOf("name" to name, "lastname" to lastName)

            FirebaseFirestore.getInstance().collection("users").document(userId)
                .update(userUpdates)
                .addOnSuccessListener {
                    Log.d("SharedViewModel", "User profile updated")
                    fetchUpdatedUser(userId)
                }
                .addOnFailureListener {
                    Log.e("SharedViewModel", "Error updating user profile", it)
                }
        }
    }
    /**
     * Updates the user profile with a new name and last name
     *
     * @param name The new name of the user
     * @param lastName The new last name of the user
     */
    private fun fetchUpdatedUser(userId: String) {
        FirebaseFirestore.getInstance().collection("users").document(userId)
            .get()
            .addOnSuccessListener { documentSnapshot ->
                val updatedUser = documentSnapshot.toObject(User::class.java)
                _currentUser.value = updatedUser
            }
            .addOnFailureListener {
                Log.e("SharedViewModel", "Error fetching updated user data", it)
            }
    }

    /**
     * Loads properties and returns them as LiveData
     *
     * @return LiveData containing a list of Property objects
     */
    fun loadProperties(): LiveData<List<Property>> {
        return liveData {
            emitSource(storageService.fetchPropertyList().asLiveData())
        }
    }
    /**
     * Creates a new property and executes a callback upon completion
     *
     * @param property The Property object to create
     * @param onComplete A callback function to execute upon completion of the creation
     */
    fun createProperty(property: Property, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            storageService.createProperty(property, onComplete)
        }
    }

    fun cleanUpUserTripLocations() {
        viewModelScope.launch {
            val user = _currentUser.value ?: return@launch
            val validLocations = user.tripLocations.filterNot { it.isBlank() }
            FirebaseFirestore.getInstance().collection("users").document(user.id)
                .update("tripLocations", validLocations)
                .addOnSuccessListener {
                    Log.d("SharedViewModel", "User trip locations cleaned up")
                }
                .addOnFailureListener { e ->
                    Log.e("SharedViewModel", "Error cleaning up user trip locations", e)
                }
        }
    }
}
enum class AuthenticationState {
    AUTHENTICATED, UNAUTHENTICATED, INVALID_AUTHENTICATION
}