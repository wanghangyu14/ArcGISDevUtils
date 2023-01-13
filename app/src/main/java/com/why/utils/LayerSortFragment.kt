package com.why.utils

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.esri.arcgisruntime.mapping.view.MapView
import kotlinx.android.synthetic.main.fragment_layer_sort.*


class LayerSortFragment(private val mapview: MapView) : Fragment() {
    private val viewModel:MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_layer_sort, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        sortLayer.bind(mapview)
        val list = viewModel.selectedLayerOptions
        sortLayer.setLayers(list)
    }

    override fun onDestroyView() {
        sortLayer.unbind()
        super.onDestroyView()
    }
}