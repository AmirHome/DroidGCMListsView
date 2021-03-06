package com.amirhome.droidgcmlistsview;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
/**
 * Created by AmirHome.com on 12/29/2016.
 */
public class MyHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

    //OUR VIEWS
    ItemClickListener itemClickListener;
    TextView order_no, time, cost, status;


    public MyHolder(View itemView) {
        super(itemView);

        this.order_no = (TextView) itemView.findViewById(R.id.order_no);
        this.time = (TextView) itemView.findViewById(R.id.time);
        this.cost = (TextView) itemView.findViewById(R.id.cost);
        this.status = (TextView) itemView.findViewById(R.id.status);
        itemView.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        this.itemClickListener.onItemClick(v,getLayoutPosition());

    }

    public void setItemClickListener(ItemClickListener ic)
    {
        this.itemClickListener=ic;
    }
}
