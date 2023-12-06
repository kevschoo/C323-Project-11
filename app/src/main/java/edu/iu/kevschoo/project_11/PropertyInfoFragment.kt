package edu.iu.kevschoo.project_11

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import edu.iu.kevschoo.project_11.databinding.FragmentPropertyInfoBinding
import edu.iu.kevschoo.project_11.model.Property
import kotlinx.coroutines.launch

class PropertyInfoFragment : Fragment() {

    private var _binding: FragmentPropertyInfoBinding? = null
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
        _binding = FragmentPropertyInfoBinding.inflate(inflater, container, false)
        return binding.root
    }
    /**
     * Called immediately after onCreateView() has returned, but before any saved state has been restored in to the view
     * This method initializes the UI components and sets up event listeners.
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

        viewModel.selectedProperty.observe(viewLifecycleOwner, Observer { property ->
            updateUI(property)
        })

        binding.buttonCancel.setOnClickListener {
            view.findNavController().navigate(R.id.action_propertyInfoFragment3_to_homeFragment)
        }

        binding.buttonReserve.setOnClickListener {
            viewModel.selectedProperty.value?.let { property ->
                lifecycleScope.launch {
                    val reservationSuccess = viewModel.reserveProperty(property.id)
                    if (reservationSuccess) {
                        view.findNavController().navigate(R.id.action_propertyInfoFragment3_to_tripsFragment)
                    } else {
                        view.findNavController().navigate(R.id.action_propertyInfoFragment3_to_profileFragment)
                    }
                }
            }
        }
    }
    /**
     * Updates the UI elements with the details of the selected property
     *
     * @param property The Property object containing the details to display. If null, shows a default error message
     */
    private fun updateUI(property: Property?) {
        if (property != null) {
            binding.textViewTitle.text = property.name
            binding.textViewHouseInfo.text = property.roominfo
            binding.textViewRating.text = getString(R.string.rating_format, property.rating)
            binding.textViewHost.text = getString(R.string.hosted_by_format, property.host)
            binding.textViewLongDescription.text = property.propertyinfo
            binding.textViewPrice.text = getString(R.string.price_format, property.cost)


        } else {
            binding.textViewTitle.text = "Property Incorrectly Loaded"
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
