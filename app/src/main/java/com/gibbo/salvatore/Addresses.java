package com.gibbo.salvatore;

import java.util.ArrayList;

public class Addresses {
    private String address;
    private ArrayList<String> prices, carburanti;

    private Addresses(String address, ArrayList<String> prices, ArrayList<String> carburanti){
        this.address = address;
        this.prices = prices;
        this.carburanti = carburanti;
    }

    public Addresses(){

    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public ArrayList<String> getPrices() {
        return prices;
    }

    public void setPrices(ArrayList<String> prices) {
        this.prices = prices;
    }

    public ArrayList<String> getCarburanti() {
        return carburanti;
    }

    public void setCarburanti(ArrayList<String> carburanti) {
        this.carburanti = carburanti;
    }
}
