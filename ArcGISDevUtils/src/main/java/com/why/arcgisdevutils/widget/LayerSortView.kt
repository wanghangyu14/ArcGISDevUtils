package com.why.arcgisdevutils.widget

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.ernestoyaquello.dragdropswiperecyclerview.DragDropSwipeRecyclerView
import com.ernestoyaquello.dragdropswiperecyclerview.listener.OnItemDragListener
import com.esri.arcgisruntime.mapping.view.MapView
import com.why.arcgisdevutils.R
import com.why.arcgisdevutils.widget.adapter.LayerOptionAdapter
import com.why.arcgisdevutils.widget.model.LayerOption
import kotlinx.android.synthetic.main.layer_sort_view.view.*

class LayerSortView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : LinearLayout(context, attrs) {
    private var mMapView: MapView? = null
    private var onUnbind: ((view: LayerSortView) -> Unit)? = null
    private var onBind: ((view: LayerSortView) -> Unit)? = null
    private val list = mutableListOf<LayerOption>()
    private val sortAdapter = LayerOptionAdapter(list)
    private val onItemDragListener = object : OnItemDragListener<LayerOption> {
        override fun onItemDragged(previousPosition: Int, newPosition: Int, item: LayerOption) {
        }

        override fun onItemDropped(initialPosition: Int, finalPosition: Int, item: LayerOption) {
            if (initialPosition != finalPosition) {
                list.removeAt(initialPosition)
                list.add(finalPosition, item)
                mMapView?.map?.operationalLayers?.clear()
                mMapView?.map?.operationalLayers?.addAll(List(list.size){index -> list[index].layers }.flatten().reversed())
            }
        }
    }
    init {
        LayoutInflater.from(context).inflate(R.layout.layer_sort_view, this, true)
        rv.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = sortAdapter
            orientation =
                DragDropSwipeRecyclerView.ListOrientation.VERTICAL_LIST_WITH_VERTICAL_DRAGGING
            dragListener = onItemDragListener
            disableSwipeDirection(DragDropSwipeRecyclerView.ListOrientation.DirectionFlag.LEFT)
            disableSwipeDirection(DragDropSwipeRecyclerView.ListOrientation.DirectionFlag.RIGHT)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setLayers(layers:List<LayerOption>){
        list.clear()
        list.addAll(layers)
        sortAdapter.refresh(list)
    }

    fun bind(mapView: MapView) {
        if(!isBind()){
            mMapView = mapView
            onBind?.invoke(this)
        }
    }

    fun unbind(){
        mMapView = null
        onUnbind?.invoke(this)
    }

    fun isBind() = mMapView != null
}