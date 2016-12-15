package com.amirhome.droidgcmlistsview;

/**
 * Created by www.AmirHome.com on 12/14/2016.
 */
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
@JsonIgnoreProperties(ignoreUnknown = true)

public class Order {
    public String order_no, time, cost, status;
    private String foods;
    public String address, customer, delivery_date, description, order_cost, order_date, status_delivery, status_order;
    public Order() {

    }

/*    public Order(String order_no, String time, String order_cost, String status) {
        Log.d("MainActivity", "public order " + order_cost);

        this.order_no = order_no;
        this.time = time;
        this.cost = order_cost;
        this.status = status;
    }*/

    public String getStatus() {
        return status;
    }

    public void setStatus(String status_order, String status_delivery) {
        if (status_order.equals("0")) {
            this.status = "Onayli Bekliyor";
        } else {
            if (status_order.equals("Reject")) {
                this.status = status_order;
            } else {
                if (status_delivery.equals("0")) {
                    this.status = status_order;
                } else {
                    this.status = status_delivery;
                }
            }
        }
    }

    public String getOrderNo() {
        return order_no;
    }

    public void setOrderNo(String order_no) {
        this.order_no = order_no;
    }

    public String getCost() {
        return cost;
    }

    public void setCost(String cost) {
        this.cost = cost;
    }

    public String getOrderTime() {
        return time;
    }

    public void setOrderTime(String time) {
        this.time = time;
    }
}