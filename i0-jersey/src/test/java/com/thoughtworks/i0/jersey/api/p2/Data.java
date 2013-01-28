package com.thoughtworks.i0.jersey.api.p2;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Data {
    private String value;

    public Data() {
    }

    public Data(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Data data = (Data) o;

        if (!value.equals(data.value)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
