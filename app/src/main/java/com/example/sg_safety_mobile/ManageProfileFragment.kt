package com.example.sg_safety_mobile
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ManageProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ManageProfileFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

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
        val v= inflater.inflate(R.layout.fragment_manage_profile, container, false)
        var button2 =v.findViewById(R.id.btn_email) as Button
        var button3 =v.findViewById(R.id.btn_contact) as Button
        var button4 =v.findViewById(R.id.btn_password) as Button
        var button5 =v.findViewById(R.id.btn_cpr) as Button

        //NAVIGATE TO RELEVANT SUBPAGES


        button2.setOnClickListener{
            val intent = Intent( activity , EditEmail::class.java)
            startActivity(intent)
        }

        button3.setOnClickListener{
            val intent = Intent( activity , ContactNumber::class.java)
            startActivity(intent)
        }

        button4.setOnClickListener{
            val intent = Intent( activity , ChangePassword::class.java)
            startActivity(intent)
        }

        button5.setOnClickListener{
            val intent = Intent( activity , UpdateCPR::class.java)
            startActivity(intent)
        }

        return v
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