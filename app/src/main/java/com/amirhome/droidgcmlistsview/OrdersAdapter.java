package com.amirhome.droidgcmlistsview;

/**
 * Created by www.AmirHome.com on 12/14/2016.
 */
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.MyViewHolder> {

    private List<Order> moviesList;
    private int tmpLayout;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView order_no, time, cost, status;

        public MyViewHolder(View view) {
            super(view);
            order_no = (TextView) view.findViewById(R.id.order_no);
            time = (TextView) view.findViewById(R.id.time);
            cost = (TextView) view.findViewById(R.id.cost);
            cost = (TextView) view.findViewById(R.id.cost);
            status = (TextView) view.findViewById(R.id.status);
        }
    }


    public OrdersAdapter(List<Order> moviesList, int tmpLayout) {
        this.moviesList = moviesList;
        this.tmpLayout = tmpLayout;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(tmpLayout, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Order order = moviesList.get(position);
        holder.order_no.setText(order.getOrderNo());
        holder.time.setText(order.getOrderTime());
        holder.cost.setText(order.getCost());
        holder.status.setText(order.getStatus());
    }

    @Override
    public int getItemCount() {
        return moviesList.size();
    }
}

