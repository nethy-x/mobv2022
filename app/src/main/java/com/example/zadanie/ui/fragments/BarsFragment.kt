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
import com.example.zadanie.databinding.FragmentBarsBinding
import com.example.zadanie.utils.Injection
import com.example.zadanie.utils.PreferenceData
import com.example.zadanie.ui.viewmodels.BarsViewModel
import com.example.zadanie.ui.viewmodels.SortBy
import com.google.android.material.bottomnavigation.BottomNavigationView

class BarsFragment : Fragment() {
    private var _binding: FragmentBarsBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewmodel: BarsViewModel
    private lateinit var nav : NavController


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewmodel = ViewModelProvider(
            this,
            Injection.provideViewModelFactory(requireContext())
        ).get(BarsViewModel::class.java)
        activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)?.visibility =
            View.VISIBLE
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBarsBinding.inflate(inflater, container, false)
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
            model = viewmodel
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

            bnd.swiperefresh.setOnRefreshListener {
                viewmodel.refreshData()
            }
            bnd.chipSortDefault.setOnClickListener{
                bnd.recycleView.scrollToPosition(0)
                viewmodel.setSortBy(SortBy.DEFAULT)
                bnd.chipSortCount.isChecked = false
                bnd.chipSortName.isChecked = false
            }
            bnd.chipSortName.setOnClickListener{
                bnd.recycleView.scrollToPosition(0)
                viewmodel.setSortBy(SortBy.NAME)
                bnd.chipSortCount.isChecked = false
                bnd.chipSortDefault.isChecked = false

            }
            bnd.chipSortCount.setOnClickListener{
                bnd.recycleView.scrollToPosition(0)
                viewmodel.setSortBy(SortBy.USERS)
                bnd.chipSortDefault.isChecked = false
                bnd.chipSortName.isChecked = false
            }

        }

        viewmodel.loading.observe(viewLifecycleOwner) {
            binding.swiperefresh.isRefreshing = it
        }

        viewmodel.message.observe(viewLifecycleOwner) {
            if (PreferenceData.getInstance().getUserItem(requireContext()) == null) {
                nav.navigate(R.id.action_to_login)
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}