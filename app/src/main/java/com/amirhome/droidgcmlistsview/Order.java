package com.amirhome.droidgcmlistsview;

/**
 * Created by www.AmirHome.com on 12/14/2016.
 */
public class Order {
    private String order_no, time, cost, status;

    public Order() {
    }

    public Order(String order_no, String time, String cost, String status) {
        this.order_no = order_no;
        this.time = time;
        this.cost = cost;
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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