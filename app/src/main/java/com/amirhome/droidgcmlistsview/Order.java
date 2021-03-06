package com.amirhome.droidgcmlistsview;

/**
 * Created by www.AmirHome.com on 12/14/2016.
 */

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.firebase.client.DataSnapshot;

import java.util.HashMap;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)

public class Order {

    public String order_no, time, cost, status, foodTitle;
    public String menu_count, menu_title, menu_size, menu_description, menu_radios, menu_options, menu_contents;
    public String address;
    public String phone, total_donate, restaurant_order_income;
    public String customer;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public String delivery_date;
    public String description;
    public String order_cost;
    public String order_date;
    public String status_delivery;
    public String status_order;

    public Order() {

    }

    public Order(String order_no, String time, String order_cost, String status) {
//        Log.d("AmirHomeLog", "public order " + order_cost);

        this.order_no = order_no;
        this.time = time;
        this.cost = order_cost;
        this.status = status;
    }

    public void setFood(DataSnapshot food) {

        StringBuilder sb = new StringBuilder();
        for (DataSnapshot menu_radios : food.child("menu_radios").getChildren()) {
            if (menu_radios.getKey() != null) sb.append(menu_radios.getKey() + ": ");

            Map<String, String> td = (HashMap<String, String>) menu_radios.getValue();
            if (menu_radios.getValue() != null) sb.append( td.keySet().toArray()[0]+", " );

        }
        this.menu_radios = sb.toString();

        sb = new StringBuilder();
        for (DataSnapshot menu_options : food.child("menu_options").getChildren()) {
            if (menu_options.getKey() != null) sb.append(menu_options.getKey() + ": ");

            Map<String, Object> td = (HashMap<String, Object>) menu_options.getValue();
            for (Object menu_options_items : td.keySet().toArray()) {
                if (menu_options.getKey() != null) sb.append(menu_options_items + ", ");
            // Log.d("AmirHomeLog", "getChildrenCount " + menu_options_items);
            }
        }
        this.menu_options = sb.toString();

        sb = new StringBuilder();
        for (DataSnapshot menu_contents : food.child("menu_contents").getChildren()) {
            if (menu_contents.getValue() != null) sb.append(menu_contents.getValue()+", ");
        }
        this.menu_contents = sb.toString();

        this.menu_title = food.child("menu_count").getValue() + " x " + food.child("menu_title").getValue() + "   { " + food.child("menu_size").getValue() + " }";

        this.menu_description = food.child("menu_description").getValue().toString();

    }

    public String getMenuTitle() {
        return menu_title;
    }

    public String getMenuOptions() {
        return menu_options;
    }

    public String getMenuRadios() {
        return menu_radios;
    }

    public String getMenuContents() {
        return menu_contents;
    }

    public String getMenuDescription() {
        return menu_description;
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