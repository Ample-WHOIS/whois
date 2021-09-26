package com.test.rnids;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FavAdapter extends RecyclerView.Adapter<FavAdapter.ViewHolder>{
    private Context context;
    private List<FavItem> favItemList;
    private FavDB favDB;
    public FavAdapter(Context context,List<FavItem> favItemList){
        this.context=context;
        this.favItemList=favItemList;
    }


    @NonNull
    @Override
    public FavAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,int viewType){
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.fav_item,
                parent,false);
        favDB=new FavDB(context);
        return new ViewHolder(view);



    }
    @Override
    public void onBindViewHolder (@NonNull ViewHolder holder,int position){
        holder.favTextView.setText(favItemList.get(position).getItem_title());



    }
    @Override
    public int getItemCount(){
        return favItemList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView favTextView;
        Button favBtn;
        public ViewHolder(@NonNull View itemView){
            super(itemView);
            favTextView=itemView.findViewById(R.id.favTextView);
            favBtn=itemView.findViewById(R.id.favButton);
            favBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                  int position=getAdapterPosition();
                  final FavItem favItem= favItemList.get(position);
                  favDB.remove_fav(favItem.getKey_id());
                  removeItem(position);
                }
            });

        }
    }
    private void removeItem(int position){
        favItemList.remove(position);
        notifyDataSetChanged();
        notifyItemRangeChanged(position,favItemList.size());


    }
}
