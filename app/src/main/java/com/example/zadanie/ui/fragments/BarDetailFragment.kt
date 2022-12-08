package com.example.zadanie.ui.fragments


import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.example.zadanie.R
import com.example.zadanie.databinding.FragmentDetailBarBinding
import com.example.zadanie.utils.Injection
import com.example.zadanie.utils.PreferenceData
import com.example.zadanie.ui.viewmodels.DetailViewModel
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions

class BarDetailFragment : Fragment() {
    private var _binding: FragmentDetailBarBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: DetailViewModel
    private lateinit var nav : NavController

    private lateinit var id: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(
            this,
            Injection.provideViewModelFactory(requireContext())
        )[DetailViewModel::class.java]
        id = arguments?.getString("id").toString()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDetailBarBinding.inflate(inflater, container, false)
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
            viewModel.bar.observe(viewLifecycleOwner){

                val cameraPosition = CameraOptions.Builder()
                    .center(Point.fromLngLat(it?.lon ?: 0.0, it?.lat ?: 0.0 ))
                    .zoom(18.0)
                    .build()
                bnd.mapView.getMapboxMap().setCamera(cameraPosition)
            }

            bnd.mapButton.setOnClickListener {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(
                            "geo:0,0,?q=" +
                                    "${viewModel.bar.value?.lat ?: 0}," +
                                    "${viewModel.bar.value?.lon ?: 0}" +
                                    "(${viewModel.bar.value?.name ?: ""}"
                        )
                    )
                )
            }
        }
        viewModel.loadBar(id)
        viewModel.getUsers(id)
        viewModel.message.observe(viewLifecycleOwner) {
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