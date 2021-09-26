package com.test.rnids;
import androidx.recyclerview.widget.RecyclerView;
import androidx.annotation.NonNull;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class DomainAdapter extends RecyclerView.Adapter<DomainAdapter.ViewHolder> {
    private ArrayList<DomainItem> domainItems;
    private Context context;
    private FavDB favDB;
    public DomainAdapter(ArrayList<DomainItem> domainItems, Context context){
        this.domainItems=domainItems;
        this.context=context;
    }
    @NonNull
    @Override
    public DomainAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,int viewType){
        favDB = new FavDB(context);
        SharedPreferences prefs=context.getSharedPreferences("prefs",Context.MODE_PRIVATE);
        boolean firstStart=prefs.getBoolean("firstStart",true);
        if(firstStart){
            createTableOnFirstStart();

        }
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item,parent,false);
        return new ViewHolder(view);
    }
    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView;
        Button favBtn;


        public ViewHolder(@NonNull View itemView){
            super(itemView);
            titleTextView=itemView.findViewById(R.id.textView);
            favBtn=itemView.findViewById(R.id.favButton);
            favBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                        public void onClick(View view){
                    int position=getAdapterPosition();
                    DomainItem domainItem=domainItems.get(position);
                    if(domainItem.getFavStatus().equals("0")){
                        domainItem.setFavStatus("1");
                        favDB.insertIntoTheDatabase(domainItem.getTitle(),
                                domainItem.getKey_id(),domainItem.getFavStatus());
                        favBtn.setBackgroundResource(R.drawable.ic_baseline_red_24);
                    } else {
                        domainItem.setFavStatus("0");
                        favDB.remove_fav(domainItem.getKey_id());
                        favBtn.setBackgroundResource(R.drawable.ic_baseline_shadow_24);
                    }

                }
            });
        }

    }
    @Override
    public void onBindViewHolder(@NonNull DomainAdapter.ViewHolder holder,int position){
        final DomainItem domainItem = domainItems.get(position);
        readCursorData(domainItem,holder);
        holder.titleTextView.setText(domainItem.getTitle());
    }

    private void readCursorData(DomainItem domainItem,ViewHolder viewHolder) {
        Cursor cursor= favDB.read_all_data(domainItem.getKey_id());
        SQLiteDatabase db=favDB.getReadableDatabase();
        try{
            while(cursor.moveToNext()){
                @SuppressLint("Range") String item_fav_status = cursor.getString(cursor.getColumnIndex(FavDB.FAVORITE_STATUS));
                domainItem.setFavStatus(item_fav_status);
                //check fav status
                if(item_fav_status!=null&&item_fav_status.equals("1")){
                    viewHolder.favBtn.setBackgroundResource(R.drawable.ic_baseline_red_24);

                } else if (item_fav_status!=null&&item_fav_status.equals("0")){
                    viewHolder.favBtn.setBackgroundResource(R.drawable.ic_baseline_shadow_24);
                }
            }
        }finally{
            if(cursor!=null&&cursor.isClosed())
                cursor.close();
                db.close();
        }

    }

    @Override
    public int getItemCount(){
        return domainItems.size();
    }
    private void createTableOnFirstStart(){
        favDB.insertEmpty();
        SharedPreferences prefs=context.getSharedPreferences("prefs",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=prefs.edit();
        editor.putBoolean("firstStart",false);
        editor.apply();

    }
}
