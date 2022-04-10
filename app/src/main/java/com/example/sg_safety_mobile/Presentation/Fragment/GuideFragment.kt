package com.example.sg_safety_mobile.Presentation.Fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.sg_safety_mobile.Detail
import com.example.sg_safety_mobile.Logic.Adaptors.RecyclerAdapter
import com.example.sg_safety_mobile.R




class GuideFragment : Fragment() {



    private lateinit var recyclerAdapter: RecyclerAdapter
    private lateinit var recyclerView:RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val v=inflater.inflate(R.layout.fragment_guide, container, false)
        val arrayList=ArrayList<Detail>()
        viewEInitializations(v)
        arrayList.add(Detail("1","Complete the CHECK and CALL steps\n"))
        arrayList.add(Detail("2","As soon as an AED is available, turn it on and follow the voice prompts\n"))
        arrayList.add(Detail("3","Remove clothing and attach pads correctly\n" +
                "\n" +
                "1. Remove all clothing covering the chest. If necessary, wipe the chest dry\n" +
                "2. Place one pad on the upper right side of the chest\n" +
                "3. Place the other pad on the lower left side of the chest, a few inches below the left armpit\n" +
                "4. Note: If the pads may touch, place one pad in the middle of the chest and the other pad on the back, between the shoulder blades\n"))
        arrayList.add(Detail("4","Plug the pad connector cable into the AED, if necessary\n"))
        arrayList.add(Detail("5","Prepare to let the AED analyze the heart’s rhythm\n" +
                "\n" +
                "1. Make sure no one is touching the person\n" +
                "2. Say, “CLEAR!” in a loud, commanding voice\n"))
        arrayList.add(Detail("6","Deliver a shock, if the AED determines one is needed\n" +
                "\n" +
                "1. Make sure no one is touching the person\n" +
                "2. Say, “CLEAR!” in a loud, commanding voice\n" +
                "3. Push the “shock” button to deliver the shock\n"))
        arrayList.add(Detail("7","After the AED delivers the shock, or if no shock is advised, immediately start CPR, beginning with compressions\n"))

        recyclerAdapter= RecyclerAdapter(arrayList,this)

        recyclerView.setLayoutManager(LinearLayoutManager(activity))
        recyclerView.adapter=recyclerAdapter

        return v
    }
    private fun viewEInitializations(v:View) {
        recyclerView=v.findViewById(R.id.recycler_view)
    }


}