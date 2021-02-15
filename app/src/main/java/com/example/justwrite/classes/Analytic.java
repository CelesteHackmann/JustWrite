package com.example.justwrite.classes;

import java.util.Objects;

public class Analytic {
    private final String mName;
    private final String mData;

    public Analytic(String name, String data) {
        mName = name;
        mData = data;
    }

    public String getData() {
        return mData;
    }

    public String getName() {
        return mName;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Analytic analytic = (Analytic) o;
        return Objects.equals(mName, analytic.mName) &&
                Objects.equals(mData, analytic.mData);
    }

    @Override
    public int hashCode() {
        return Objects.hash(mName, mData);
    }
}
