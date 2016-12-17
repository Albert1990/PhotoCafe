package com.brain_socket.photocafe.model;

import com.brain_socket.photocafe.PhotoCafeApp;

import org.json.JSONObject;

/**
 * Created by Albert on 12/13/16.
 */
public class ProductModel extends AppBaseModel{
    private String ID;
    private String ArabicName;
    private String EnglishName;
    private String ArabicDescription;
    private String EnglishDescription;
    private String Image;
    private String Price;
    private String Status;
    private String Order;

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

    public String getDescription(){
        if(PhotoCafeApp.getCurrentLanguage() == PhotoCafeApp.SUPPORTED_LANGUAGE.AR)
            return ArabicDescription;
        return EnglishDescription;
    }

    public String getImage() {
        return Image;
    }

    public void setImage(String image) {
        Image = image;
    }

    public String getPrice() {
        return Price+" AED";
    }

    public void setPrice(String price) {
        Price = price;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getOrder() {
        return Order;
    }

    public void setOrder(String order) {
        Order = order;
    }

    public static ProductModel fromJson(JSONObject json) {
        try {
            ProductModel productModel = PhotoCafeApp.getSharedGsonParser().fromJson(json.toString(), ProductModel.class);
            return productModel;
        }catch (Exception ignored){}
        return new ProductModel();
    }

    public static ProductModel fromJsonString(String json) {
        try {
            ProductModel productModel = PhotoCafeApp.getSharedGsonParser().fromJson(json, ProductModel.class);
            return productModel;
        }catch (Exception ignored){}
        return new ProductModel();
    }
}
