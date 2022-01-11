package com.why.util.widget

import android.content.Context
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.why.util.Point
import com.why.util.R
import com.why.util.widget.adapter.NaviAdapter

class NavigateDialog(context: Context) : AlertDialog(context) {
    private var start:Point? =null
    private var destination:Point? =null


    fun setStart(point: Point): NavigateDialog {
        start = point
        return this
    }

    fun setDestination(point: Point): NavigateDialog {
        destination = point
        return this
    }

    override fun show() {
        val view = LayoutInflater.from(context).inflate(R.layout.navigate_dialog, null)
        val rv = view.findViewById<RecyclerView>(R.id.rv)
        rv.adapter = NaviAdapter(start,destination)
        val builder = Builder(context)
        builder.setTitle("请选择导航地图")
            .setView(view)
        val dialog = builder.create()
        dialog.show()
    }
}