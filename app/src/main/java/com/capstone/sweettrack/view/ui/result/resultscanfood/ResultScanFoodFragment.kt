package com.capstone.sweettrack.view.ui.result.resultscanfood

import android.net.Uri
import androidx.fragment.app.viewModels
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.core.view.MenuProvider
import androidx.navigation.fragment.findNavController
import com.capstone.sweettrack.data.remote.response.ResponseModel
import com.capstone.sweettrack.view.ViewModelFactory
import com.coding.sweettrack.R
import com.coding.sweettrack.databinding.FragmentResultScanFoodBinding

class ResultScanFoodFragment : Fragment() {

    private var _binding: FragmentResultScanFoodBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<ResultScanFoodViewModel> {
        ViewModelFactory.getInstance(requireActivity())
    }

    private var uri: String? = null
    private var image: Uri? = null
    private var response: ResponseModel? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentResultScanFoodBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupView()

        uri = arguments?.getString("image_uri")
        response = arguments?.getParcelable<ResponseModel>("result")

        uri?.let {
            binding.resultImage.setImageURI(uri?.toUri())
            image = uri?.toUri()
        }

        response?.let { result ->
            val data = result.data
            binding.tvName.text = data?.makanan
            binding.tvKalori.text = getString(R.string.kalori, data?.kalori.toString())
            binding.tvGula.text = getString(R.string.gula, data?.gula.toString())
            binding.tvLemak.text = getString(R.string.lemak, data?.lemak.toString())
            binding.tvProtein.text = getString(R.string.protein, data?.protein.toString())
        }

        binding.resultImage.setOnClickListener {
            showConfirmationDialog()
        }

        Handler(Looper.getMainLooper()).postDelayed({
            showConfirmationDialog()
        }, 3000)
    }

    private fun setupView() {
        (activity as AppCompatActivity).supportActionBar?.apply {
            setDisplayShowHomeEnabled(true)
            setDisplayHomeAsUpEnabled(true)
            setHomeButtonEnabled(true)
            show()
        }

        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {}
            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    android.R.id.home -> {
                        findNavController().navigateUp()
                        true
                    }

                    else -> false
                }
            }
        }, viewLifecycleOwner)
    }

    private fun showConfirmationDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setMessage("Apakah anda ingin mengonsumsi ini?")
        builder.setPositiveButton("Ya") { _, _ ->

            Toast.makeText(requireContext(), "Anda memilih ya", Toast.LENGTH_SHORT).show()

            val calorie = response?.data?.kalori
            if (calorie != null) {
                viewModel.updateUserCalorieDay(calorie)
                observeUpdateResult()
            }

            val resultScan = response?.data

            image?.let {
                viewModel.addResultToHistory(
                    it,
                    resultScan?.makanan ?: "",
                    calorie ?: 0.0,
                    resultScan?.gula ?: 0.0,
                    resultScan?.lemak ?: 0.0,
                    resultScan?.protein ?: 0.0,
                    requireContext()
                )

            }
            observeAddResult()
        }
        builder.show()
    }

    private fun observeUpdateResult() {
        viewModel.updateResult.observe(viewLifecycleOwner) { result ->
            println(result)
            if (!result.error) {

                val alertDialog = AlertDialog.Builder(requireActivity()).apply {
                    setTitle("Informasi")
                    setMessage(result.message)
                    setCancelable(false)
                    create()
                }.show()

                Handler(Looper.getMainLooper()).postDelayed({
                    alertDialog.dismiss()
//                    findNavController().popBackStack()
                }, 1000)
            } else {
                val alertDialog = AlertDialog.Builder(requireActivity()).apply {
                    setTitle(result.message)
                    setMessage(result.describe)
                    setCancelable(false)
                    create()
                }.show()

                Handler(Looper.getMainLooper()).postDelayed({
                    alertDialog.dismiss()
                }, 2000)
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun observeAddResult() {
        viewModel.addResult.observe(viewLifecycleOwner) { result ->
            println(result)
            if (!result.error) {

                val alertDialog = AlertDialog.Builder(requireActivity()).apply {
                    setTitle("Informasi")
                    setMessage(result.message)
                    setCancelable(false)
                    create()
                }.show()

                Handler(Looper.getMainLooper()).postDelayed({
                    alertDialog.dismiss()
                    findNavController().popBackStack()
                }, 1000)
            } else {
                val alertDialog = AlertDialog.Builder(requireActivity()).apply {
                    setTitle(result.message)
                    setMessage(result.describe)
                    setCancelable(false)
                    create()
                }.show()

                Handler(Looper.getMainLooper()).postDelayed({
                    alertDialog.dismiss()
                }, 2000)
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }
}