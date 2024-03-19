package com.avs.pantrychef.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.navigation.fragment.findNavController
import com.avs.pantrychef.R

class HomeFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate el layout para este fragmento
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val searchButton: FrameLayout = view.findViewById(R.id.searchButton)

        searchButton.setOnClickListener {
           val action = HomeFragmentDirections.actionHomeFragmentToSearchIngredientDialogFragment()
            findNavController().navigate(action)
        }
    }
}