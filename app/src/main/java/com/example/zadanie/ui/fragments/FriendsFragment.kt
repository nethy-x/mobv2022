package com.example.zadanie.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import com.example.zadanie.R
import com.example.zadanie.databinding.FragmentFriendsBinding
import com.example.zadanie.utils.Injection
import com.example.zadanie.utils.PreferenceData
import com.example.zadanie.ui.viewmodels.FriendsViewModel


class FriendsFragment : Fragment() {
    private var _binding: FragmentFriendsBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: FriendsViewModel
    private lateinit var nav: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this,
            Injection.provideViewModelFactory(requireContext())
        )[FriendsViewModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFriendsBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        nav = view.findNavController()
        val x = PreferenceData.getInstance().getUserItem(requireContext())
        if ((x?.uid ?: "").isBlank()) {
            nav.navigate(R.id.action_to_login)
            return
        }

        binding.apply {
            lifecycleOwner = viewLifecycleOwner
            model = viewModel
            navController = nav
        }.also { bnd ->
            bnd.topAppBar.setNavigationOnClickListener { nav.popBackStack() }
            bnd.topAppBar.setOnMenuItemClickListener { menuItem ->
                when (menuItem.itemId) {
                    R.id.logout -> {
                        PreferenceData.getInstance().clearData(requireContext())
                        nav.navigate(R.id.action_to_login)
                        true
                    }
                    else -> false
                }
            }
            bnd.swiperefresh.setOnRefreshListener { viewModel.refreshData()  }
            bnd.addFriend.setOnClickListener{
                nav.navigate(R.id.action_to_add_friend)
            }
        }

        viewModel.loading.observe(viewLifecycleOwner) {
            binding.swiperefresh.isRefreshing = it
        }

        viewModel.message.observe(viewLifecycleOwner) {
            if (PreferenceData.getInstance().getUserItem(requireContext()) == null) {
                Navigation.findNavController(requireView()).navigate(R.id.action_to_login)
            }
        }

    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}