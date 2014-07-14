package com.suitscreen.batchinstall;

import java.util.ArrayList;
import java.util.Vector;

public class DeviceBuffer {

    private static volatile int port = 10001;

    public static DeviceBuffer getInstance() {

        if (instance == null) {
            instance = new DeviceBuffer();
        }

        return instance;
    }

    private DeviceBuffer() {
        list = new Vector<DeviceInfo>( );
    }

    public synchronized boolean add(DeviceInfo devInfo) {

        for (int i = 0; i < list.size(); i++) {
            DeviceInfo info = list.get(i);
            if ( info.getDevSerial().equals(devInfo.getDevSerial() ) ) {
                return false;
            }
        }

        list.add(devInfo);
        return true;
    }

    public synchronized void refresh(ArrayList<Device> devList) {

        DeviceInfo info = null;
        // 正向轮询加入新的设备
        for (int i = 0; i < devList.size(); i++) {
            Device dev = devList.get(i);
            boolean isExist = false;
            for (int j = 0; j < list.size(); j++) {

                info = list.get(j);
                if ( info.getDevSerial().equals(dev.serialNo)) {
                    isExist = true;
                    if (dev.status == DeviceInfo.STATUS_OFFLINE) {
                        info.setStatus(DeviceInfo.STATUS_OFFLINE);
                    }

                    if (   dev.status == DeviceInfo.STATUS_PLUGIN
                        && info.getStatus() == DeviceInfo.STATUS_OFFLINE) {
                        info.setStatus(dev.status);
                    }
                    break;
                }
            }

            if (!isExist) {
                if (port++ > 19999) {
                    port = 10001;
                }
                list.add( new DeviceInfo(dev.serialNo, dev.status, port) );
            }
        }

        // 反向轮询删除被拔出的设备
        for (int i = 0; i < list.size(); i++) {

            boolean isPulled = true; // 默认已被拔出
            info = list.get(i);
            for (int j = 0; j < devList.size(); j++) {

                if (info.getDevSerial().equals(devList.get(j).serialNo)) {
                    isPulled = false;
                    continue;
                }
            }

            if (isPulled) {
                info.setStatus(DeviceInfo.STATUS_PULLED); // 标识设备已经拔出
            }
        }
    }

    public synchronized DeviceInfo get(int index) {
        return list.get(index);
    }

    public synchronized void remove(DeviceInfo devInfo) {
        list.remove(devInfo);
    }

    public synchronized void remove(int index) {
        list.remove(index);
    }

    public synchronized void clear() {
        list.clear();
    }

    public synchronized int size() {
        return list.size();
    }

    static private DeviceBuffer instance;
    private Vector<DeviceInfo>  list;
}
