package com.chandruscm.hackatum.fragments

import android.os.Bundle
import android.support.design.widget.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.chandruscm.hackatum.R

class TrunkKeyFragmentBottomSheetDialog : BottomSheetDialogFragment()
{
    companion object
    {
        fun newInstance() = TrunkKeyFragmentBottomSheetDialog()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View?
    {
        return inflater.inflate(R.layout.fragment_trunk_key, container, false)
    }
}