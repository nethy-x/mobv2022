package com.example.zadanie.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.example.zadanie.R
import com.example.zadanie.databinding.FragmentSignUpBinding
import com.example.zadanie.utils.Injection
import com.example.zadanie.utils.PreferenceData
import com.example.zadanie.ui.viewmodels.AuthViewModel
import com.example.zadanie.utils.PasswordUtils
import com.example.zadanie.utils.SystemUtils
import com.google.android.material.bottomnavigation.BottomNavigationView

class SignUpFragment : Fragment() {
    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!
    private lateinit var authViewModel: AuthViewModel
    private lateinit var nav: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        authViewModel = ViewModelProvider(
            this,
            Injection.provideViewModelFactory(requireContext())
        ).get(AuthViewModel::class.java)
        activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)?.visibility =
            View.INVISIBLE
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        nav = view.findNavController()
        val x = PreferenceData.getInstance().getUserItem(requireContext())
        if ((x?.uid ?: "").isNotBlank()) {
            nav.navigate(R.id.action_to_bars)
            return
        }

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            model = authViewModel
            navController = nav
        }.also { bnd ->
            bnd.signup.setOnClickListener {
                if (bnd.username.text.toString().isNotBlank() && bnd.password.text.toString()
                        .isNotBlank()
                    && bnd.password.text.toString()
                        .compareTo(bnd.repeatPassword.text.toString()) == 0
                ) {
                    val hashPass = PasswordUtils.hash(bnd.password.text.toString())
                    authViewModel.signup(
                        bnd.username.text.toString(),
                        String(hashPass)
                    )
                } else if (bnd.username.text.toString().isBlank() || bnd.password.text.toString()
                        .isBlank()
                ) {
                    authViewModel.show("Fill in name and password")
                } else {
                    authViewModel.show("Passwords must be same")
                }
                SystemUtils.closeKeyboard(it)
            }

            bnd.login.setOnClickListener {
                nav.navigate(R.id.action_to_login)
            }


        }
        authViewModel.user.observe(viewLifecycleOwner) {
            it?.let {
                PreferenceData.getInstance().putUserItem(requireContext(), it)
                nav.navigate(R.id.action_to_bars)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}