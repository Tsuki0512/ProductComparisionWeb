package com.example.productcomparisionweb.entity;

public class Price_tracking {
    private int uid;

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    private int pid;

    @Override
    public String toString() {
        return "Price_tracking{" +
                "uid=" + uid +
                ", pid=" + pid +
                '}';
    }
}
