package edu.iu.kevschoo.project_11

import android.net.Uri
import edu.iu.kevschoo.project_11.model.Property
import kotlinx.coroutines.flow.Flow
interface StorageService
{
    /**
     * Fetches a list of user trips based on the user's ID
     *
     * @param userId The unique identifier of the user
     * @return A Flow emitting a list of Property objects representing user trips
     */
    fun fetchUserTrips(userId: String): Flow<List<Property>>
    /**
     * Uploads an image to a remote storage and returns the Uri of the uploaded image
     *
     * @param imageUri The Uri of the image to be uploaded
     * @param onComplete A callback function that is invoked with the Uri of the uploaded image
     */
    fun uploadImage(imageUri: Uri, onComplete: (Uri?) -> Unit)
    /**
     * Fetches a list of all properties
     * @return A Flow emitting a list of Property objects
     */
    fun fetchPropertyList(): Flow<List<Property>>
    /**
     * Creates a new property in the data store
     *
     * @param property The Property object to be created
     * @param onComplete A callback function that is invoked with a Boolean indicating success or failure
     */
    fun createProperty(property: Property, onComplete: (Boolean) -> Unit)

}