package com.gennan.summer.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.gennan.summer.R
import com.gennan.summer.util.LogUtil

/**
 *Created by Gennan on 2019/8/20.
 */
class AddNewDialogFragment : DialogFragment() {
    val TAG = "AddNewDialogFragment"
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_add_new, container, false)
        val addNewEt: EditText = view.findViewById(R.id.et_add_new)
        val addNewTv: TextView = view.findViewById(R.id.iv_add_new_search)
        addNewTv.setOnClickListener {
            LogUtil.d(TAG, "EditText ----> ${addNewEt.text}")
            LogUtil.d(TAG, "被点击了")
        }
        return view
    }
}