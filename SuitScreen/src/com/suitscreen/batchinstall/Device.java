package com.suitscreen.batchinstall;

public class Device {

    public Device(String serialNo, int status) {

        this.serialNo = serialNo;
        this.status = status;
    }

    public String serialNo; // 终端串号
    public int    status;  // 状态
}
