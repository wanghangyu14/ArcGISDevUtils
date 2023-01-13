package com.why.utils

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.esri.arcgisruntime.mapping.view.MapView
import com.why.arcgisdevutils.utils.showToast
import kotlinx.android.synthetic.main.fragment_layer_controller.*

class LayerControllerFragment(private val mapview:MapView) : Fragment() {
    private val viewModel:MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_layer_controller, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapview.map.operationalLayers.clear()
        layerController.bind(mapview)
        layerController.setLayers(viewModel.layerOptions)
        val selectedLayers = viewModel.selectedLayerOptions
        layerController.setOnCheckListener { option ->
            if(option.isSelected&&!selectedLayers.contains(option)){
                selectedLayers.add(option)
            }
            if(!option.isSelected&&selectedLayers.contains(option)){
                selectedLayers.remove(option)
            }
        }
        layerController.setOnSeekBarChangeListener { option, progress,fromUser ->
            if(fromUser){
                "${option.name}当前透明度为$progress".showToast(requireContext())
            }
        }
    }
}