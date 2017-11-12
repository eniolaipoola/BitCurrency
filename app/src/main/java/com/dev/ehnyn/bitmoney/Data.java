package com.dev.ehnyn.bitmoney;


import java.util.Currency;

public class Data {
    public  int price;
    public  String currency;


    public Data(String cryptoCurrency,  int cryptoPrice){
        currency = cryptoCurrency;
        price = cryptoPrice;
    }
}
