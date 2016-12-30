package com.amirhome.droidgcmlistsview;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.ArrayList;

/**
 * Created by o9125 on 12/29/2016.
 */
public class MyAdapter extends RecyclerView.Adapter<MyHolder> implements Filterable {

    Context c;
    ArrayList<Player> players,filterList;
    CustomFilter filter;


    public MyAdapter(Context ctx,ArrayList<Player> players)
    {
        this.c=ctx;
        this.players=players;
        this.filterList=players;
    }


    @Override
    public MyHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //CONVERT XML TO VIEW ONBJ
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.model,null);

        //HOLDER
        MyHolder holder=new MyHolder(v);

        return holder;
    }

    //DATA BOUND TO VIEWS
    @Override
    public void onBindViewHolder(MyHolder holder, int position) {

        //BIND DATA
        holder.order_no.setText(players.get(position).getOrder_no());
        holder.cost.setText(players.get(position).getOrder_cost());
        holder.time.setText(players.get(position).getOrder_date());
        holder.status.setText(players.get(position).getStatus());


        //IMPLEMENT CLICK LISTENET
        holder.setItemClickListener(new ItemClickListener() {
            @Override
            public void onItemClick(View v, int pos) {
                Intent intent = new Intent(v.getContext(), DetailActivity.class);
                intent.putExtra("id", players.get(pos).getOrder_no());
                ((Activity)v.getContext()).startActivityForResult(intent,0);

                Snackbar.make(v,players.get(pos).getOrder_no(),Snackbar.LENGTH_SHORT).show();
            }
        });

    }

    //GET TOTAL NUM OF PLAYERS
    @Override
    public int getItemCount() {
        return players.size();
    }

    //RETURN FILTER OBJ
    @Override
    public Filter getFilter() {
        if(filter==null)
        {
            filter=new CustomFilter(filterList,this);
        }

        return filter;
    }
}
