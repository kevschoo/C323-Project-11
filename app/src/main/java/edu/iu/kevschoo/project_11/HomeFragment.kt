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
import edu.iu.kevschoo.project_11.databinding.FragmentHomeBinding
import edu.iu.kevschoo.project_11.model.Property

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
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
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
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
            override fun handleOnBackPressed() { viewModel.signOut()
                view.findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
            }
        }
        viewModel.loadProperties().observe(viewLifecycleOwner, Observer { properties ->
            setupRecyclerView(properties)
        })

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

        binding.buttonCreate.setOnClickListener {
            view.findNavController().navigate(R.id.action_homeFragment_to_createPropertyFragment)
        }

        binding.buttonTrips.setOnClickListener {
            view.findNavController().navigate(R.id.action_homeFragment_to_tripsFragment)
        }

        binding.buttonProfile.setOnClickListener {
            view.findNavController().navigate(R.id.action_homeFragment_to_profileFragment)
        }
    }

    private fun setupRecyclerView(properties: List<Property>) {
        binding.propertyRecyclerView.layoutManager = LinearLayoutManager(context)
        binding.propertyRecyclerView.adapter = PropertyAdapter(properties) { property ->
            viewModel.selectProperty(property)
            view?.findNavController()?.navigate(R.id.action_homeFragment_to_propertyInfoFragment3)
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
