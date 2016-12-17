package com.brain_socket.photocafe.model;

import com.brain_socket.photocafe.PhotoCafeApp;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Albert on 12/13/16.
 */
public class CategoryModel extends AppBaseModel{
    private String ID;
    private String ArabicName;
    private String EnglishName;
    private String Status;
    private String Icon;
    private String Order;
    private ArrayList<ProductModel> Products;

    @Override
    public String getId() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getName(){
        if(PhotoCafeApp.getCurrentLanguage() == PhotoCafeApp.SUPPORTED_LANGUAGE.AR)
            return ArabicName;
        return EnglishName;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getIcon() {
        return Icon;
    }

    public void setIcon(String icon) {
        Icon = icon;
    }

    public String getOrder() {
        return Order;
    }

    public void setOrder(String order) {
        Order = order;
    }

    public ArrayList<ProductModel> getProducts() {
        return Products;
    }

    public void setProducts(ArrayList<ProductModel> products) {
        Products = products;
    }

    public static CategoryModel fromJson(JSONObject json) {
        try {
            CategoryModel brand = PhotoCafeApp.getSharedGsonParser().fromJson(json.toString(), CategoryModel.class);
            return brand;
        }catch (Exception ignored){}
        return new CategoryModel();
    }

    public static CategoryModel fromJsonString(String json) {
        try {
            CategoryModel brand = PhotoCafeApp.getSharedGsonParser().fromJson(json, CategoryModel.class);
            return brand;
        }catch (Exception ignored){}
        return new CategoryModel();
    }
}