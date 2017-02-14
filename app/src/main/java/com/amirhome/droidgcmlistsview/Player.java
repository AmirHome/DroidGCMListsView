package com.amirhome.droidgcmlistsview;

/**
 * Created by AmirHome.com on 12/29/2016.
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
            if (status_order.equals("Reject") || status_order.equals("RejectAuto")) {
                switch (status_order) {
                    case "RejectAuto":
                        this.status = DetailActivity.CONST_REJECT_AUTO;
                        break;
                    case "Reject":
                        this.status = DetailActivity.CONST_REJECT;
                        break;
                }
            } else {
                switch (status_delivery) {
                    case "0":
                        this.status = DetailActivity.TESLIM_BEKLIYOR;
                        break;
                    case "Reject_reason1":
                        this.status = DetailActivity.REJECT_REASON1;
                        break;
                    case "Reject_reason2":
                        this.status = DetailActivity.REJECT_REASON2;
                        break;
                    case "Reject_reason3":
                        this.status = DetailActivity.REJECT_REASON3;
                        break;
                    case "Reject_reason4":
                        this.status = DetailActivity.REJECT_REASON4;
                        break;
                    case "Reject_reason5":
                        this.status = DetailActivity.REJECT_REASON5;
                        break;
                    case "Reject_reason6":
                        this.status = DetailActivity.REJECT_REASON6;
                        break;
                    case "Reject_reason7":
                        this.status = DetailActivity.REJECT_REASON7;
                        break;
                    case "Delivered":
                        this.status = DetailActivity.CONST_DELIVERD;
                        break;
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
