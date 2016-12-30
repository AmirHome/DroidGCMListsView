package com.amirhome.droidgcmlistsview;

/**
 * Created by o9125 on 12/29/2016.
 */
public class Player {
    public String getOrder_cost() {
        return order_cost;
    }

    public void setOrder_cost(String order_cost) {
        this.order_cost = order_cost;
    }

    public String getOrder_date() {
        return order_date;
    }

    public void setOrder_date(String order_date) {
        this.order_date = order_date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status_order, String status_delivery) {
        if (status_order.equals("0")) {
            this.status = DetailActivity.ONAYLI_BEKLIYOR;
        } else {
            if (status_order.equals(DetailActivity.CONST_REJECT) || status_order.equals(DetailActivity.CONST_REJECT_AUTO)) {
                this.status = status_order;
            } else {
                if (status_delivery.equals("0")) {
//                    this.status = status_order;
                    this.status = DetailActivity.TESLIM_BEKLIYOR;
                } else {
                    this.status = status_delivery;
                }
            }
        }
    }

    public String getOrder_no() {
        return order_no;
    }

    public void setOrder_no(String order_no) {
        this.order_no = order_no;
    }


    public String order_no, time, cost, status, foodTitle;
    public String menu_count, menu_title, menu_size, menu_description, menu_radios, menu_options, menu_contents;
    public String address, customer, delivery_date, description, order_cost, order_date, status_delivery, status_order;


}
