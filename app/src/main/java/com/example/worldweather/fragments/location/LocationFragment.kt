package com.example.worldweather.fragments.location

import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.example.worldweather.data.RemoteLocation
import com.example.worldweather.databinding.FragmentLocationBinding
import com.example.worldweather.fragments.home.HomeFragment
import org.koin.androidx.viewmodel.ext.android.viewModel

class LocationFragment : Fragment() {

    private var _binding: FragmentLocationBinding? = null
    private val binding get() = requireNotNull(_binding)

    private val locationViewModel: LocationViewModel by viewModel()

    private val locationsAdapter = LocationsAdapter(
        onLocationClicked = { remoteLocation ->
            setLocation(remoteLocation)
        }
    )

    private val handler = Handler(Looper.getMainLooper())
    private val searchDelay = 300L // Delay in milliseconds for debounce

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLocationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setListeners()
        setupLocationRecycleView()
        onObservers()
    }

    private fun setupLocationRecycleView() {
        with(binding.locationRecyclerView) {
            addItemDecoration(DividerItemDecoration(requireContext(), RecyclerView.VERTICAL))
            adapter = locationsAdapter
        }
    }

    private fun setListeners() {
        binding.imageClose.setOnClickListener { findNavController().popBackStack() }
        binding.inputSearch.editText?.setOnEditorActionListener setOnEditorActionListener@{ _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                hideSoftKeyBoard()
                val query = binding.inputSearch.editText?.text
                if (query.isNullOrBlank()) return@setOnEditorActionListener true
                searchLocation(query.toString())
            }
            return@setOnEditorActionListener true
        }

        binding.inputSearch.editText?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                handler.removeCallbacksAndMessages(null) // Clear any existing callbacks
                val runnable = Runnable {
                    if (!s.isNullOrEmpty()) {
                        searchLocation(s.toString())
                    } else {
                        clearSearchResults()
                    }
                }
                handler.postDelayed(runnable, searchDelay)
            }

            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun setLocation(remoteLocation: RemoteLocation) {
        // Handle the location selection
        with(remoteLocation) {
            val locationText = "$name, $region, $country"
            setFragmentResult(
                requestKey = HomeFragment.REQUEST_KEY_MANUAL_LOCATION_SEARCH,
                result = bundleOf(
                    HomeFragment.KEY_LOCATION_TEXT to locationText,
                    HomeFragment.KEY_LATITUDE to lat,  // Corrected key for latitude
                    HomeFragment.KEY_LONGITUDE to lon  // Corrected key for longitude
                )
            )
            findNavController().popBackStack()
        }
    }

    private fun onObservers() {
        locationViewModel.searchResult.observe(viewLifecycleOwner) {
            val searchResultDataState = it ?: return@observe
            if (searchResultDataState.isLoading) {
                binding.locationRecyclerView.visibility = View.GONE
                binding.progressBar.visibility = View.VISIBLE
            } else {
                binding.progressBar.visibility = View.GONE
            }
            searchResultDataState.locations?.let { remoteLocations ->
                binding.locationRecyclerView.visibility = View.VISIBLE
                locationsAdapter.setData(remoteLocations)
            }
            searchResultDataState.error?.let { error ->
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun searchLocation(query: String) {
        locationViewModel.searchLocation(query)
    }

    private fun clearSearchResults() {
        locationsAdapter.setData(emptyList())
        binding.locationRecyclerView.visibility = View.GONE
    }

    private fun hideSoftKeyBoard() {
        val inputManager =
            requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(
            binding.inputSearch.editText?.windowToken, 0
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacksAndMessages(null)
        _binding = null
    }
}
