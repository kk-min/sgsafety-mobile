package com.example.sg_safety_mobile

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import android.widget.Toast
import kotlinx.android.synthetic.main.cardview.view.*

class RecyclerAdapter(val arrayList:ArrayList<Detail>, val context: GuideFragment): RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun binditems(detail:Detail){
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
