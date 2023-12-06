package edu.iu.kevschoo.project_11

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import edu.iu.kevschoo.project_11.databinding.FragmentTripsBinding
import edu.iu.kevschoo.project_11.model.Property

class TripsFragment : Fragment() {

    private var _binding: FragmentTripsBinding? = null
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
        _binding = FragmentTripsBinding.inflate(inflater, container, false)
        return binding.root
    }

    /**
     * Called immediately after onCreateView() has returned, but before any saved state has been restored in to the view
     * This gives subclasses a chance to initialize themselves once they know their view hierarchy has been completely created
     *
     * @param view The View returned by onCreateView(LayoutInflater, ViewGroup, Bundle)
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?)
    {
        super.onViewCreated(view, savedInstanceState)

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                //view.findNavController().navigate(R.id.)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        binding.buttonExplore.setOnClickListener {
            view.findNavController().navigate(R.id.action_tripsFragment_to_homeFragment)
        }

        binding.buttonCreate.setOnClickListener {
            view.findNavController().navigate(R.id.action_tripsFragment_to_createPropertyFragment)
        }

        binding.buttonProfile.setOnClickListener {
            view.findNavController().navigate(R.id.action_tripsFragment_to_profileFragment)
        }

        viewModel.userTrips.observe(viewLifecycleOwner, Observer { properties ->
            setupRecyclerView(properties)
        })
    }

    /**
     * Sets up the RecyclerView with a layout manager and adapter
     *
     * @param properties The list of Property items to be displayed in the RecyclerView
     */
    private fun setupRecyclerView(properties: List<Property>) {
        binding.propertyRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.propertyRecyclerView.adapter = PropertyAdapter(properties) { property ->
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
