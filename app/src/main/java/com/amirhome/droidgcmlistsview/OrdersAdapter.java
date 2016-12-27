package com.amirhome.droidgcmlistsview;

/**
 * Created by www.AmirHome.com on 12/14/2016.
 */

import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.MyViewHolder> {

    private List<Order> oaOrderList, oafilterList;
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView order_no, time, cost, status;

        public MyViewHolder(View view) {
            super(view);
            order_no = (TextView) view.findViewById(R.id.order_no);
            time = (TextView) view.findViewById(R.id.time);
            cost = (TextView) view.findViewById(R.id.cost);
            status = (TextView) view.findViewById(R.id.status);
        }
    }


    public OrdersAdapter(List<Order> orderList) {
        oaOrderList = orderList;
        oafilterList = new ArrayList<Order>();
        // we copy the original list to the filter list and use it for setting row values
        oafilterList.addAll(oaOrderList);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.order_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
//        Order order = oaOrderList.get(position);
        Order order = oafilterList.get(position);
        holder.order_no.setText(order.getOrderNo());
        holder.time.setText(order.getOrderTime());
        holder.cost.setText(order.getCost());
        holder.status.setText(order.getStatus());
    }

    @Override
    public int getItemCount() {
        return (null != oafilterList ? oafilterList.size() : 0);
//        return oaOrderList.size();
    }
    // Do Search...
    public void filter(final String text) {

        // Searching could be complex..so we will dispatch it to a different thread...
        new Thread(new Runnable() {
            @Override
            public void run() {

                // Clear the filter list
                oafilterList.clear();

                // If there is no search value, then add all original list items to filter list
                if (TextUtils.isEmpty(text)) {
                    oafilterList.addAll(oaOrderList);

                } else {
                    // Iterate in the original List and add it to filter list...
                    for (Order item : oaOrderList) {
                        if (item.status.toLowerCase().equals(text.toLowerCase()) ) {
                            // Adding Matched items
                            oafilterList.add(item);
                        }
                    }
                }
                // Set on UI Thread
/*                ((Activity) mContext).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // Notify the List that the DataSet has changed...
                        notifyDataSetChanged();
                    }
                });*/
//                Log.d("AmirHomeLog", "filter " + filterList.size() + orderList.size() );
//                notifyDataSetChanged();
            }
        }).start();

    }
}

