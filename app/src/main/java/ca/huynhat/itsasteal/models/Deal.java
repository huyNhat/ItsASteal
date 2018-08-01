package ca.huynhat.itsasteal.models;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Deal {
    private String deal_id;
    private String dealName;
    private int quantity;
    private Double price;
    private String storeName;
    private Double latitude;
    private Double longtitude;
    private String timeStamp;
    private String deal_img_url;
    private String user_id;
    private List<Comment> comments;
    private int thumpsUp;
    private int thumpsDown;


    public Deal(){

    }

    public Deal(String deal_id, String dealName, int quantity, Double price, String storeName,
                Double latitude, Double longtitude, String timeStamp, String deal_img_url, String user_id) {
        this.deal_id = deal_id;
        this.dealName = dealName;
        this.quantity = quantity;
        this.price = price;
        this.storeName = storeName;
        this.latitude = latitude;
        this.longtitude = longtitude;
        this.timeStamp = timeStamp;
        this.deal_img_url = deal_img_url;
        this.user_id = user_id;
        this.comments = new ArrayList<Comment>();
        this.thumpsUp=0;
        this.thumpsDown=0;
    }

    public int getThumpsUp() {
        return thumpsUp;
    }

    public void setThumpsUp(int thumpsUp) {
        this.thumpsUp = thumpsUp;
    }

    public int getThumpsDown() {
        return thumpsDown;
    }

    public void setThumpsDown(int thumpsDown) {
        this.thumpsDown = thumpsDown;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getDeal_id() {
        return deal_id;
    }

    public void setDeal_id(String deal_id) {
        this.deal_id = deal_id;
    }

    public String getDealName() {
        return dealName;
    }

    public void setDealName(String dealName) {
        this.dealName = dealName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongtitude() {
        return longtitude;
    }

    public void setLongtitude(Double longtitude) {
        this.longtitude = longtitude;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getDeal_img_url() {
        return deal_img_url;
    }

    public void setDeal_img_url(String deal_img_url) {
        this.deal_img_url = deal_img_url;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public List<Comment> getComments() {
        return comments;
    }


}

