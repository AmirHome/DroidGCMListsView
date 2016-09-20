package com.amirhome.droidgcmlistsview.Data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by o9125 on 9/6/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Cart {
    private String orderId;
    private String address;
    public String customer;
    private String delivery_date;
    private String description;
    private String foods;
    private String order_date;
    private String status_delivery;
    public String status_order;

    public Cart() {

    }

    public String getOrderId() {
        return orderId;
    }
    public void setOrderId(String orderId) {
    this.orderId = orderId;
    }

    public String getStatusOrder() {
        return status_order;
    }
    public String getCustomer() {
        return customer;
    }
    public void setCustomer(String customer) {
        this.customer = customer;
    }
    public void setStatusOrder(String statusOrder) {
        this.status_order = statusOrder;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
}

