package ca.huynhat.itsasteal.models;

import com.google.android.gms.maps.model.LatLng;

public class User {
    private String user_id;
    private String user_name;
    private String instance_id;
    private LatLng current_location_coordinate;

    public User(){

    }

    public User(String user_id, String user_name, LatLng current_location_coordinate) {
        this.user_id = user_id;
        this.user_name = user_name;
        this.current_location_coordinate = current_location_coordinate;
    }


    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getInstance_id() {
        return instance_id;
    }

    public void setInstance_id(String instance_id) {
        this.instance_id = instance_id;
    }

    public LatLng getCurrent_location_coordinate() {
        return current_location_coordinate;
    }

    public void setCurrent_location_coordinate(LatLng current_location_coordinate) {
        this.current_location_coordinate = current_location_coordinate;
    }

    @Override
    public String toString() {
        return "User{" +
                "user_id='" + user_id + '\'' +
                ", user_name='" + user_name + '\'' +
                ", instance_id='" + instance_id + '\'' +
                ", current_location_coordinate=" + current_location_coordinate +
                '}';
    }
}
