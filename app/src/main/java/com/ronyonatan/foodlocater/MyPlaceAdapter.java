package com.ronyonatan.foodlocater;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;


public class MyPlaceAdapter extends RecyclerView.Adapter<MyPlaceAdapter.SinglePlaceVH> {
    List<MyPlace> allPlaces = MyPlace.listAll(MyPlace.class);
    Context c;



    public MyPlaceAdapter(List<MyPlace> allPlaces, Context c) {
        this.allPlaces = allPlaces;
        this.c = c;
    }

    @Override
    public SinglePlaceVH onCreateViewHolder(ViewGroup parent, int viewType) {
        View singeLview = LayoutInflater.from(c).inflate(R.layout.single_place, null);
        // 9. create new ViewHolder with the view
        SinglePlaceVH singlePlaceVH = new SinglePlaceVH(singeLview);


        return singlePlaceVH;
    }

    @Override
    public void onBindViewHolder(SinglePlaceVH singlePlaceVH, int position) {
        MyPlace currentPlace = allPlaces.get(position);
        singlePlaceVH.bindThePlaceData(currentPlace);
    }


    @Override
    public int getItemCount() {
        return allPlaces.size();
    }

    class SinglePlaceVH extends RecyclerView.ViewHolder {
ImageView placeIV;
        TextView nameTV;
        TextView distanceTV;

        public SinglePlaceVH(View itemView) {
            super(itemView);
            placeIV=(ImageView)itemView.findViewById(R.id.placeIV);
            nameTV=(TextView)itemView.findViewById(R.id.nameTV);
            distanceTV=(TextView)itemView.findViewById(R.id.distanceTV);

        }



        public void bindThePlaceData(MyPlace myPlace)
        {
            for (int i = 0; i <allPlaces.size() ; i++) {
                nameTV.setText(myPlace.name);
                distanceTV.setText( myPlace.distance+"km");
                String image=myPlace.icon;
                Glide.with(c)
                        .load(image)
                        .into(placeIV);
            }



        }







    }




}
