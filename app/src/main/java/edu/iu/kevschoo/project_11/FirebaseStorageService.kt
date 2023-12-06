package edu.iu.kevschoo.project_11

import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import com.google.android.gms.tasks.Tasks
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import edu.iu.kevschoo.project_11.model.Property
import edu.iu.kevschoo.project_11.model.User
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class FirebaseStorageService : StorageService
{

    private val storageInstance = Firebase.storage("gs://c323-projects.appspot.com")
    private val storageReference = storageInstance.reference
    private val firestore: FirebaseFirestore = Firebase.firestore
    private val userId: String? get() = FirebaseAuth.getInstance().currentUser?.uid
    /**
     * Uploads an image to Firebase storage and stores the image URL in Firestore
     *
     * @param imageUri The Uri of the image to be uploaded
     * @param onComplete A callback function that is invoked with the Uri of the uploaded image or null if the upload fails
     */
    override fun uploadImage(imageUri: Uri, onComplete: (Uri?) -> Unit)
    {
        val fileRef = storageReference.child("images/${userId}/${imageUri.lastPathSegment}")
        val uploadTask = fileRef.putFile(imageUri)

        uploadTask.addOnSuccessListener{
            fileRef.downloadUrl.addOnSuccessListener { uri ->
                val imageInfo = mapOf("url" to uri.toString())
                firestore.collection("users").document(userId.orEmpty())
                    .collection("images").add(imageInfo)

                    .addOnSuccessListener {onComplete(uri)}

                    .addOnFailureListener {
                        Log.e("FirebaseStorageService", "Failed to store image URL in Firestore", it)
                        onComplete(null)
                    }
            }
        }
            .addOnFailureListener { exception ->
                Log.e("FirebaseStorageService", "Image upload failed", exception)
                onComplete(null)
            }
    }
    /**
     * Fetches a list of all properties from Firestore
     *
     * @return A Flow emitting a list of Property objects
     */
    override fun fetchPropertyList(): Flow<List<Property>> = callbackFlow {
        val collectionRef = firestore.collection("properties")
        val listenerRegistration = collectionRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.e("FirebaseStorageService", "Listen failed.", e)
                return@addSnapshotListener
            }

            val properties = snapshot?.toObjects(Property::class.java)
            trySend(properties.orEmpty())
        }

        awaitClose { listenerRegistration.remove() }
    }
    /**
     * Creates a new property document in Firestore and sets its ID
     *
     * @param property The Property object to be created
     * @param onComplete A callback function that is invoked with a Boolean indicating success or failure
     */
    override fun createProperty(property: Property, onComplete: (Boolean) -> Unit) {
        firestore.collection("properties").add(property)
            .addOnSuccessListener { documentReference ->
                val propertyId = documentReference.id
                firestore.collection("properties").document(propertyId)
                    .update("id", propertyId)
                    .addOnSuccessListener {
                        Log.d("FirebaseStorageService", "Property ID set successfully")
                        onComplete(true)
                    }
                    .addOnFailureListener { e ->
                        Log.e("FirebaseStorageService", "Error setting property ID", e)
                        onComplete(false)
                    }
            }
            .addOnFailureListener { e ->
                Log.e("FirebaseStorageService", "Error adding property", e)
                onComplete(false)
            }
    }

    /**
     * Fetches a list of user trips based on the user's ID from Firestore
     *
     * @param userId The unique identifier of the user
     * @return A Flow emitting a list of Property objects representing user trips
     */
    override fun fetchUserTrips(userId: String): Flow<List<Property>> = callbackFlow {
        val userRef = firestore.collection("users").document(userId)
        userRef.get().addOnSuccessListener { document ->
            val user = document.toObject(User::class.java)
            val validTripProperties = user?.tripLocations.orEmpty()
                .filterNot { it.isBlank() }
                .map { firestore.collection("properties").document(it).get() }

            Tasks.whenAllSuccess<DocumentSnapshot>(validTripProperties).addOnSuccessListener { documents ->
                val properties = documents.mapNotNull { it.toObject(Property::class.java) }
                trySend(properties)
            }
        }.addOnFailureListener { e ->
            Log.e("FirebaseStorageService", "Error fetching user trips", e)
        }

        awaitClose { }
    }


}
