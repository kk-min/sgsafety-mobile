package com.example.sg_safety_mobile

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.fragment_guide.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ManageProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class GuideFragment : Fragment() {

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var recyclerAdapter: RecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val d=inflater.inflate(R.layout.fragment_guide, container, false)
        val arrayList=ArrayList<Detail>()

        arrayList.add(Detail("1","Complete the CHECK and CALL steps"))
        arrayList.add(Detail("2","As soon as an AED is available, turn it on and follow the voice prompts"))
        arrayList.add(Detail("3","Remove clothing and attach pads correctly\n" +
                "\n" +
                "1. Remove all clothing covering the chest. If necessary, wipe the chest dry\n" +
                "2. Place one pad on the upper right side of the chest\n" +
                "3. Place the other pad on the lower left side of the chest, a few inches below the left armpit\n" +
                "4. Note: If the pads may touch, place one pad in the middle of the chest and the other pad on the back, between the shoulder blades"))
        arrayList.add(Detail("4","Plug the pad connector cable into the AED, if necessary"))
        arrayList.add(Detail("5","Prepare to let the AED analyze the heart’s rhythm\n" +
                "\n" +
                "1. Make sure no one is touching the person\n" +
                "2. Say, “CLEAR!” in a loud, commanding voice"))
        arrayList.add(Detail("6","Deliver a shock, if the AED determines one is needed\n" +
                "\n" +
                "1. Make sure no one is touching the person\n" +
                "2. Say, “CLEAR!” in a loud, commanding voice\n" +
                "3. Push the “shock” button to deliver the shock"))
        arrayList.add(Detail("7","After the AED delivers the shock, or if no shock is advised, immediately start CPR, beginning with compressions"))

        recyclerAdapter= RecyclerAdapter(arrayList,this)

        var recyclerView=d.findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.setLayoutManager(LinearLayoutManager(activity))
        recyclerView.adapter=recyclerAdapter

        return d
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ManageProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ManageProfileFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}