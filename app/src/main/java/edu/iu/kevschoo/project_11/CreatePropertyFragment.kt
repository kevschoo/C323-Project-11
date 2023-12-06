package edu.iu.kevschoo.project_11

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import edu.iu.kevschoo.project_11.databinding.FragmentCreatePropertyBinding
import edu.iu.kevschoo.project_11.model.Property

class CreatePropertyFragment : Fragment() {

    private var _binding: FragmentCreatePropertyBinding? = null
    private val binding get() = _binding!!
    private val viewModel: SharedViewModel by viewModels({requireActivity()})
    /**
     * Creates and returns the view hierarchy associated with the fragment
     *
     * @param inflater The LayoutInflater object that can be used to inflate views in the fragment
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state
     * @return The View for the fragment's UI, or null
     */
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View
    {
        _binding = FragmentCreatePropertyBinding.inflate(inflater, container, false)
        return binding.root
    }
    /**
     * Called immediately after onCreateView() has returned, but before any saved state has been restored in to the view
     * This method initializes the UI components and sets up event listeners
     *
     * @param view The View returned by onCreateView(LayoutInflater, ViewGroup, Bundle)
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                view.findNavController().navigate(R.id.action_propertyInfoFragment3_to_homeFragment)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
        binding.buttonSubmit.setOnClickListener {
            createProperty()
        }

        binding.buttonExplore.setOnClickListener {
            view.findNavController().navigate(R.id.action_createPropertyFragment_to_homeFragment)
        }

        binding.buttonTrips.setOnClickListener {
            view.findNavController().navigate(R.id.action_createPropertyFragment_to_tripsFragment)
        }

        binding.buttonProfile.setOnClickListener {
            view.findNavController().navigate(R.id.action_createPropertyFragment_to_profileFragment)
        }
    }

    private fun createProperty() {
        val name = binding.editTextPropertyName.text.toString().trim()
        val host = binding.editTextHost.text.toString().trim()
        val roomInfo = binding.editTextRoomInfo.text.toString().trim()
        val propertyInfo = binding.editTextPropertyInfo.text.toString().trim()
        val ratingText = binding.editTextRating.text.toString()
        val costText = binding.editTextCost.text.toString()

        if (name.isEmpty() || host.isEmpty() || roomInfo.isEmpty() || propertyInfo.isEmpty() ||
            ratingText.isEmpty() || costText.isEmpty()) {

            Toast.makeText(context, "All fields are required", Toast.LENGTH_LONG).show()
            return
        }

        val rating = ratingText.toFloatOrNull() ?: 0f
        val cost = costText.toFloatOrNull() ?: 0f

        val property = Property("", name, host, roomInfo, propertyInfo, rating, cost)

        viewModel.createProperty(property) { isSuccess ->
            if (isSuccess) {
                Log.d("CreatePropertyFragment", "Property created successfully")

                view?.findNavController()?.navigate(R.id.action_createPropertyFragment_to_homeFragment)
            } else {
                Log.e("CreatePropertyFragment", "Failed to create property")
                view?.findNavController()?.navigate(R.id.action_createPropertyFragment_to_homeFragment)
            }
        }
    }
    /**
     * Called when the view is destroyed
     */
    override fun onDestroyView()
    {
        super.onDestroyView()
        _binding = null
    }
}
