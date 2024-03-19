package com.avs.pantrychef.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.navigation.fragment.navArgs
import com.avs.pantrychef.R

class ReceiptListFragment : Fragment() {

    private val args: ReceiptListFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate el layout para este fragmento
        return inflater.inflate(R.layout.fragment_receipt_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Aquí puedes acceder a los argumentos
        val ingredientIds = args.ingredientIds.joinToString(separator = ", ")

        // Encuentra el TextView por su ID y asigna los IDs de los ingredientes como su texto
        val ingredientIdsTextView = view.findViewById<TextView>(R.id.receiptsText)
        ingredientIdsTextView.text = ingredientIds
    }
}