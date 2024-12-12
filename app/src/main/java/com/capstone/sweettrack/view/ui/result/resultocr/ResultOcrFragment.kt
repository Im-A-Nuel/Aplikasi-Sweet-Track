package com.capstone.sweettrack.view.ui.result.resultocr

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
import com.capstone.sweettrack.data.remote.response.OcrResponse
import com.capstone.sweettrack.data.remote.response.ResponseModel
import com.capstone.sweettrack.view.ViewModelFactory
import com.capstone.sweettrack.view.ui.result.resultscanfood.ResultScanFoodViewModel
import com.coding.sweettrack.R
import com.coding.sweettrack.databinding.FragmentResultOcrBinding
import com.coding.sweettrack.databinding.FragmentScanFoodBinding

class ResultOcrFragment : Fragment() {


    private var _binding: FragmentResultOcrBinding? = null
    private val binding get() = _binding!!

    private val viewModel by viewModels<ResultOcrViewModel> {
        ViewModelFactory.getInstance(requireActivity())
    }

    private var uri: String? = null
    private var image: Uri? = null
    private var response: OcrResponse? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentResultOcrBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupView()

        uri = arguments?.getString("image_uri")
        response = arguments?.getParcelable<OcrResponse>("result")

        uri?.let {
            binding.resultImage.setImageURI(uri?.toUri())
            image = uri?.toUri()
        }

        response?.let { data ->
            val sugar = data.data?.gula
            binding.dataSugar.text = getString(R.string.g, sugar.toString())
        }



        Handler(Looper.getMainLooper()).postDelayed({
            showConfirmationDialog()
        }, 5000)
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

            viewModel.addResultToHistory(
                uri = image.toString(),
                data = response ?: OcrResponse(
                    500, true, "error", null
                )
            )
            val sugar = response?.data?.gula
            val calorie = sugar?.times(4)
            if (calorie != null) {
                viewModel.updateUserCalorieDay(calorie)
            }
        }
        builder.setNegativeButton("Tidak") { _, _ ->

        }
        builder.show()
    }
}