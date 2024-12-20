package com.capstone.sweettrack.view.ui.signup

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.capstone.sweettrack.util.CustomEmailEditText
import com.capstone.sweettrack.util.CustomPasswordEditText
import com.capstone.sweettrack.view.ViewModelFactory
import com.coding.sweettrack.R
import com.coding.sweettrack.databinding.FragmentSignUpBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.textfield.TextInputEditText

class SignUpFragment : Fragment() {

    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!

    private val viewModel: SignUpViewModel by viewModels {
        ViewModelFactory.getInstance(requireActivity())
    }

    private lateinit var username: TextInputEditText
    private lateinit var email: CustomEmailEditText
    private lateinit var password: CustomPasswordEditText
    private lateinit var rePassword: CustomPasswordEditText

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupView()
        playAnimation()
        setupAction()

        username = binding.usernameEditText
        email = binding.emailEditText
        password = binding.passwordEditText
        rePassword = binding.rePasswordEditText

        username.addTextChangedListener { checkFormValidity(username, email, password, rePassword) }
        email.addTextChangedListener { checkFormValidity(username, email, password, rePassword) }
        password.addTextChangedListener { checkFormValidity(username, email, password, rePassword) }
        rePassword.addTextChangedListener {
            checkFormValidity(
                username,
                email,
                password,
                rePassword
            )
        }

    }

    private fun checkFormValidity(
        username: TextInputEditText,
        email: CustomEmailEditText,
        password: CustomPasswordEditText,
        rePassword: CustomPasswordEditText,
    ) {
        val isUsernameValid = username.error == null && username.text?.isNotEmpty() == true
        val isEmailValid = email.error == null && email.text?.isNotEmpty() == true
        val isPasswordValid = password.error == null && password.text?.isNotEmpty() == true
        val isRePasswordValid = rePassword.error == null && rePassword.text?.isNotEmpty() == true

        val isPasswordsMatch = password.text.toString() == rePassword.text.toString()

        if (!isPasswordsMatch) {
            rePassword.error = "Password tidak cocok"
        } else {
            rePassword.error = null
        }

        binding.signUpButton.isEnabled =
            isUsernameValid && isEmailValid && isPasswordValid && isRePasswordValid && isPasswordsMatch
    }

    private fun register() {

        showLoading(true)

        val usernameText = username.text.toString()
        val emailText = email.text.toString()
        val passText = password.text.toString()

        viewModel.requestOTP(usernameText, emailText, passText)

        viewModel.registerResult.observe(requireActivity()) { result ->
            if (result != null) {
                println("Result $result")
                if (!result.error) {
                    showLoading(false)
                    val alertDialog = AlertDialog.Builder(requireActivity()).apply {
                        setTitle("Verifikasi Akun!")
                        setMessage("Cek email!! dan masukkan kode OTP")
                        setCancelable(false)
                        create()
                    }.show()

                    Handler(Looper.getMainLooper()).postDelayed({
                        alertDialog.dismiss()
                        val action =
                            SignUpFragmentDirections.actionFragmentSignUpToAuthenticationFragment(
                                email = emailText,
                                password = passText
                            )
                        findNavController().navigate(action)
                    }, 3000)
                } else {
                    showLoading(false)
                    val errorMessage = result.describe
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
        }


    }

    private fun setupView() {
        val window = requireActivity().window
        val decorView = window.decorView

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
            window.insetsController?.systemBarsBehavior =
                WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        } else {
            @Suppress("DEPRECATION")
            decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    )
        }
        (requireActivity() as? AppCompatActivity)?.supportActionBar?.hide()
    }

    private fun playAnimation() {

        val titleTv =
            ObjectAnimator.ofFloat(binding.signUpTV, View.ALPHA, 1f).setDuration(100)
        val usernameTv =
            ObjectAnimator.ofFloat(binding.usernameTV, View.ALPHA, 1f).setDuration(100)
        val usernameEdit =
            ObjectAnimator.ofFloat(binding.usernameEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val emailTv = ObjectAnimator.ofFloat(binding.emailTV, View.ALPHA, 1f).setDuration(100)
        val emailEdit =
            ObjectAnimator.ofFloat(binding.emailEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val password =
            ObjectAnimator.ofFloat(binding.passwordTextView, View.ALPHA, 1f).setDuration(100)
        val passwordEdit =
            ObjectAnimator.ofFloat(binding.passwordEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val rePassword =
            ObjectAnimator.ofFloat(binding.ulangPassTextView, View.ALPHA, 1f).setDuration(100)
        val rePasswordEdit =
            ObjectAnimator.ofFloat(binding.rePassEditTextLayout, View.ALPHA, 1f).setDuration(100)
        val signBtn = ObjectAnimator.ofFloat(binding.signUpButton, View.ALPHA, 1f).setDuration(100)

        AnimatorSet().apply {
            playSequentially(
                titleTv,
                usernameTv,
                usernameEdit,
                emailTv,
                emailEdit,
                password,
                passwordEdit,
                rePassword,
                rePasswordEdit,
                signBtn
            )
            startDelay = 100
        }.start()
    }

    private fun setupAction() {
        binding.signUpButton.isEnabled = false

        binding.signUpButton.setOnClickListener {
            register()
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    override fun onResume() {
        super.onResume()
        val bottomNavigationView = activity?.findViewById<BottomNavigationView>(R.id.nav_view)
        bottomNavigationView?.visibility = View.GONE
    }

    override fun onPause() {
        super.onPause()
        val bottomNavigationView = activity?.findViewById<BottomNavigationView>(R.id.nav_view)
        bottomNavigationView?.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
