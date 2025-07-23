package com.etask.saga.orderservice.model;

public record Item(String uuid, String name, double basePrice, double cost, int quality) { }
