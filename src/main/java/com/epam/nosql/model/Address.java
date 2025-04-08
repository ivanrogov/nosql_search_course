package com.epam.nosql.model;

import java.util.Objects;

public final class Address {
    private final String country;
    private final String town;

    public Address(String country, String town) {
        this.country = country;
        this.town = town;
    }

    public String country() {
        return country;
    }

    public String town() {
        return town;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (Address) obj;
        return Objects.equals(this.country, that.country) &&
                Objects.equals(this.town, that.town);
    }

    @Override
    public int hashCode() {
        return Objects.hash(country, town);
    }

    @Override
    public String toString() {
        return "Address[" +
                "country=" + country + ", " +
                "town=" + town + ']';
    }

}
