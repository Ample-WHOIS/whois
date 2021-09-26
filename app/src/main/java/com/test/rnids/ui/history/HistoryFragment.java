package com.test.rnids.ui.history;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import com.test.rnids.FavAdapter;
import com.test.rnids.FavDB;
import com.test.rnids.FavItem;
import com.test.rnids.R;
import com.test.rnids.databinding.FragmentHistoryBinding;

import java.util.ArrayList;
import java.util.List;

public class HistoryFragment extends Fragment {
    private RecyclerView recyclerView;
    private FavDB favDB;
    private List<FavItem> favItemList= new ArrayList<>();
    private FavAdapter favAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,Bundle savedInstanceState){
        View root = inflater.inflate(R.layout.fragment_history,container,false);

        favDB = new FavDB(getActivity());
        recyclerView=root.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        loadData();
        return root;
    }
    private void loadData() {
        if (favItemList != null) {
            favItemList.clear();
        }
        SQLiteDatabase db = favDB.getReadableDatabase();
        Cursor cursor = favDB.select_all_favorite_list();
        try {
            while (cursor.moveToNext()) {
                @SuppressLint("Range") String title = cursor.getString(cursor.getColumnIndex(FavDB.ITEM_TITLE));
                @SuppressLint("Range") String id = cursor.getString(cursor.getColumnIndex(FavDB.KEY_ID));
                FavItem favItem = new FavItem(title, id);
                favItemList.add(favItem);
            }
        } finally {
            if (cursor != null && cursor.isClosed())
                cursor.close();
            db.close();
        }

        favAdapter = new FavAdapter(getActivity(), favItemList);

        recyclerView.setAdapter(favAdapter);

    }




}