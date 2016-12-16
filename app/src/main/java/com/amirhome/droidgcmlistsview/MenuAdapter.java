package com.amirhome.droidgcmlistsview;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by o9125 on 12/16/2016.
 */

public class MenuAdapter extends RecyclerView.Adapter<MenuAdapter.MyViewHolder> {

    private List<Order> moviesList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView order_no, time, cost, status, menu_title, menu_description, menu_radios, menu_options, menu_contents;

        public MyViewHolder(View view) {
            super(view);
            order_no = (TextView) view.findViewById(R.id.order_no);
            time = (TextView) view.findViewById(R.id.time);
            cost = (TextView) view.findViewById(R.id.cost);
            status = (TextView) view.findViewById(R.id.status);
            menu_title = (TextView) view.findViewById(R.id.menu_title);
            menu_radios = (TextView) view.findViewById(R.id.menu_radios);
            menu_options = (TextView) view.findViewById(R.id.menu_options);
            menu_contents = (TextView) view.findViewById(R.id.menu_contents);
            menu_description = (TextView) view.findViewById(R.id.menu_description);

        }
    }


    public MenuAdapter(List<Order> moviesList) {
        this.moviesList = moviesList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_food, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Order order = moviesList.get(position);
        holder.order_no.setText(order.getOrderNo());
        holder.time.setText(order.getOrderTime());
        holder.cost.setText(order.getCost());
        holder.status.setText(order.getStatus());
        holder.menu_title.setText(order.getMenuTitle());
        holder.menu_radios.setText(order.getMenuRadios());
        holder.menu_options.setText(order.getMenuOptions());
        holder.menu_contents.setText(order.getMenuContents());
        holder.menu_description.setText(order.getMenuDescription());
    }

    @Override
    public int getItemCount() {
        return moviesList.size();
    }
}

