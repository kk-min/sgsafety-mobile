package com.example.sg_safety_mobile.Logic.Adaptors

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.sg_safety_mobile.Data.Detail
import com.example.sg_safety_mobile.Presentation.Fragment.GuideFragment
import com.example.sg_safety_mobile.R
import kotlinx.android.synthetic.main.cardview.view.*

/**
 *Adapter class for recycle view in Guide Page[com.example.sg_safety_mobile.Presentation.Fragment.GuideFragment]
 *
 * @since 2022-4-15
 */
class RecyclerAdapter(private val arrayList:ArrayList<Detail>, val context: GuideFragment): RecyclerView.Adapter<RecyclerAdapter.ViewHolder>() {

    /**
     *Array List of Details
     */

    /**
     *Application context of Guide Fragment
     */

    /**
     *View Holder Class
     *
     * @param itemView  Item in the View of the page
     */
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        /**
         *Put the details title and description into the UI of the page
         *
         * @param detail    Details of the guide
         */
        fun binditems(detail: Detail){
            itemView.text1.text=detail.title
            itemView.text3.text=detail.des
        }
    }

    /**
     *Function that runs when the page is created
     *
     * @param viewGroup     View group of the page
     * @param i
     */
    override fun onCreateViewHolder(viewGroup: ViewGroup, i: Int): ViewHolder {
        val v = LayoutInflater.from(viewGroup.context).inflate(R.layout.cardview, viewGroup, false)
        return ViewHolder(v)
    }

    /**
     *Function that runs when the ViewHolder is bound
     *
     * @param   holder  ViewHolders
     * @param   position    position that binds
     */
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binditems(arrayList[position])

    }

    /**
     *Get the item that is inside the Recycle View
     *
     * @return return the no. of items in the view
     */
    override fun getItemCount(): Int {
        return arrayList.size
    }
}
