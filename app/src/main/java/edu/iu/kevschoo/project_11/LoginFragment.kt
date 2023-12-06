package edu.iu.kevschoo.project_11

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import edu.iu.kevschoo.project_11.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
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
        _binding = FragmentLoginBinding.inflate(inflater, container, false)

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
        binding.messageTextView.visibility = View.GONE
        viewModel.authenticationState.observe(viewLifecycleOwner, Observer { authenticationState ->
            when (authenticationState)
            {
                AuthenticationState.AUTHENTICATED -> {view?.findNavController()?.navigate(R.id.action_loginFragment_to_homeFragment)}

                AuthenticationState.UNAUTHENTICATED -> {binding.messageTextView.visibility = View.GONE }

                AuthenticationState.INVALID_AUTHENTICATION -> {
                    binding.messageTextView.visibility = View.VISIBLE
                    binding.messageTextView.text = "Invalid username or password"
                }
            }
        })

        viewModel.errorMessage.observe(viewLifecycleOwner, Observer { errorMessage ->
            if (errorMessage != null)
            {
                binding.messageTextView.visibility = View.VISIBLE
                binding.messageTextView.text = errorMessage
            }
            else
            {
                binding.messageTextView.visibility = View.GONE
            }
        })

        binding.login.setOnClickListener {
            val email = binding.usernameEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            viewModel.signIn(email, password)
        }
        binding.signup.setOnClickListener {
            val email = binding.usernameEditText.text.toString()
            val password = binding.passwordEditText.text.toString()
            viewModel.signUp(email, password)
        }

        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                viewModel.signOut()
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)

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
