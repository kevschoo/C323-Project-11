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
import edu.iu.kevschoo.project_11.databinding.FragmentHomeBinding
import edu.iu.kevschoo.project_11.databinding.FragmentProfileBinding
import edu.iu.kevschoo.project_11.model.User
import java.text.SimpleDateFormat
import java.util.Locale

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
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
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }
    /**
     * Called immediately after onCreateView() has returned, but before any saved state has been restored in to the view
     * This method initializes the UI components and sets up event listeners
     *
     * @param view The View returned by onCreateView(LayoutInflater, ViewGroup, Bundle)
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state
     */
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.authenticationState.observe(viewLifecycleOwner, Observer { authState ->
            if (authState == AuthenticationState.AUTHENTICATED) {
                viewModel.currentUser.observe(viewLifecycleOwner, Observer { user ->
                    user?.let {
                        showProfileDetails(true)
                        updateUIWithUserData(it)
                    } ?: showProfileDetails(false)
                })
            } else {
                showProfileDetails(false)
            }
        })

        binding.nameBox.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                updateUserName()
            }
        }

        binding.lastnameBox.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                updateUserName()
            }
        }

        binding.buttonLogout.setOnClickListener {
            viewModel.signOut()
        }

        binding.buttonLogin.setOnClickListener {
            navigateToLogin()
        }


        binding.buttonCreate.setOnClickListener {
            view.findNavController().navigate(R.id.action_profileFragment_to_createPropertyFragment)
        }

        binding.buttonExplore.setOnClickListener {
            view.findNavController().navigate(R.id.action_profileFragment_to_homeFragment)
        }

        binding.buttonTrips.setOnClickListener {
            view.findNavController().navigate(R.id.action_profileFragment_to_tripsFragment)
        }

    }
    /**
     * Updates the user's name based on the input fields
     */
    private fun updateUserName() {
        val name = binding.nameBox.text.toString()
        val lastName = binding.lastnameBox.text.toString()
        if (viewModel.authenticationState.value == AuthenticationState.AUTHENTICATED) {
            viewModel.updateUserProfile(name, lastName)
        }
    }
    /**
     * Toggles the visibility of profile details based on the user's logged-in status
     *
     * @param isLoggedIn A Boolean indicating whether the user is logged in or not
     */
    private fun showProfileDetails(isLoggedIn: Boolean) {
        binding.linearData.visibility = if (isLoggedIn) View.VISIBLE else View.GONE
        binding.linearLogin.visibility = if (isLoggedIn) View.GONE else View.VISIBLE
    }
    /**
     * Updates the UI elements with the details of the current user
     *
     * @param user The User object containing the details to display
     */
    private fun updateUIWithUserData(user: User) {
        binding.nameBox.setText(user.name)
        binding.lastnameBox.setText(user.lastname)
        binding.textViewEmail.text = user.email

        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val formattedDate = dateFormat.format(user.signUpDate)
        binding.textViewDate.text = formattedDate
    }
    /**
     * Navigates to the login fragment
     */
    private fun navigateToLogin() {
        view?.findNavController()?.navigate(R.id.action_profileFragment_to_loginFragment)
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
