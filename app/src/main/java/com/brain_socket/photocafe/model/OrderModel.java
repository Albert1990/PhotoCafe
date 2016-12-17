package com.brain_socket.photocafe.model;

import com.brain_socket.photocafe.PhotoCafeApp;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Albert on 12/15/16.
 */
public class OrderModel extends AppBaseModel{
    private String TableNo;
    private ArrayList<ProductModel> Products;

    public String getTableNo() {
        return TableNo;
    }

    public void setTableNo(String tableNo) {
        TableNo = tableNo;
    }

    public ArrayList<ProductModel> getProducts() {
        return Products;
    }

    public void setProducts(ArrayList<ProductModel> products) {
        Products = products;
    }

    @Override
    public String getId() {
        return null;
    }

    public static OrderModel fromJson(JSONObject json) {
        try {
            OrderModel brand = PhotoCafeApp.getSharedGsonParser().fromJson(json.toString(), OrderModel.class);
            return brand;
        }catch (Exception ignored){}
        return new OrderModel();
    }

    public static OrderModel fromJsonString(String json) {
        try {
            OrderModel brand = PhotoCafeApp.getSharedGsonParser().fromJson(json, OrderModel.class);
            return brand;
        }catch (Exception ignored){}
        return new OrderModel();
    }
}
