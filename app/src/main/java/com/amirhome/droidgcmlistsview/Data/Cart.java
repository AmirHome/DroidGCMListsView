package com.amirhome.droidgcmlistsview.Data;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Created by o9125 on 9/6/2016.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Cart {
    public String title;
    private String name;
    private String address;
    private String customer;
    private String delivery_date;
    private String description;
    private String foods;
    private String order_date;
    private String status_delivery;
    private String status_order;
    public Cart() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

