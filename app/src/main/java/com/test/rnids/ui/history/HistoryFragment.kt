package com.test.rnids.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.test.rnids.databinding.FragmentHistoryBinding
import androidx.recyclerview.widget.LinearLayoutManager




class HistoryFragment : Fragment() {
    private var _binding: FragmentHistoryBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)

        val testList: MutableList<HistoryEntryModel> = mutableListOf()
        testList.add(HistoryEntryModel("rnids.rs", "2021-09-09", "2021-09-10"))
        testList.add(HistoryEntryModel("yandex.ru", "2021-09-09", "2021-09-10"))
        testList.add(HistoryEntryModel("test.org", "2021-09-09", "2021-09-10"))
        val adapter = HistoryAdapter(requireContext(), testList)

        val linearLayoutManager = LinearLayoutManager(requireContext(),
            LinearLayoutManager.VERTICAL, false)

        _binding!!.recycler.layoutManager = linearLayoutManager
        _binding!!.recycler.adapter = adapter

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}