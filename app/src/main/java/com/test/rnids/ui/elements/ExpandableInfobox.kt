package com.test.rnids.ui.elements

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import com.test.rnids.databinding.ExpandableInfoboxBinding

class ExpandableInfobox(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {
    private var _binding: ExpandableInfoboxBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    init {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        addView(inflater.inflate(com.test.rnids.R.layout.expandable_infobox, this, false))
    }
}