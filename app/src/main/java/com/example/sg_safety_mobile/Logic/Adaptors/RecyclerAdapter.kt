package com.example.sg_safety_mobile.Logic.Adaptors

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.sg_safety_mobile.Detail
import com.example.sg_safety_mobile.Presentation.Fragment.GuideFragment
import com.example.sg_safety_mobile.R
import kotlinx.android.synthetic.main.cardview.view.*

class RecyclerAdapter(val arrayList:ArrayList<Detail>, val context: GuideFragment): RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun binditems(detail: Detail){
            itemView.text1.text=detail.title
            itemView.text3.text=detail.des
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val v = LayoutInflater.from(viewGroup.context).inflate(R.layout.cardview, viewGroup, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binditems(arrayList[position])

    }

    override fun getItemCount(): Int {
        return arrayList.size
    }
}
