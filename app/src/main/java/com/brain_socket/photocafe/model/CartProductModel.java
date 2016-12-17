package com.brain_socket.photocafe.model;

/**
 * Created by Albert on 12/17/16.
 */
public class CartProductModel extends AppBaseModel{
    private String ID;
    private int Quantity;
    private float TotalPrice;


    public CartProductModel(){ }

    public CartProductModel(String ID,int Quantity,float TotalPrice){
        this.ID = ID;
        this.Quantity = Quantity;
        this.TotalPrice = TotalPrice;
    }

    @Override
    public String getId() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public int getQuantity() {
        return Quantity;
    }

    public void setQuantity(int quantity) {
        Quantity = quantity;
    }

    public float getTotalPrice() {
        return TotalPrice;
    }

    public void setTotalPrice(float totalPrice) {
        TotalPrice = totalPrice;
    }
}
