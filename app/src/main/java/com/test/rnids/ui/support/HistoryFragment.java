package com.test.rnids.ui.support;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.test.rnids.DomainAdapter;
import com.test.rnids.DomainItem;
import com.test.rnids.R;
import com.test.rnids.databinding.FragmentHistoryBinding;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HistoryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HistoryFragment extends Fragment {
    private ArrayList<DomainItem> domainItems= new ArrayList<>();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public HistoryFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment HistoryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static HistoryFragment newInstance(String param1, String param2) {
        HistoryFragment fragment = new HistoryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        @NonNull FragmentHistoryBinding _binding = FragmentHistoryBinding.inflate(inflater, container, false);
        View root = inflater.inflate(R.layout.fragment_history,container,false);
        RecyclerView recyclerView = _binding.recyclerView;
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(new DomainAdapter(domainItems,getActivity()));
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        domainItems.add(new DomainItem(R.drawable.lookup,"domejn1","1","0"));
        domainItems.add(new DomainItem(R.drawable.lookup,"domejn2","2","0"));
        return root;
    }
}