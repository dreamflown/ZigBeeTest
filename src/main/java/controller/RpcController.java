package controller;

import service.Downstreaam;
import service.Upstream;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

public class RpcController {

    public static Map<String, String> resp = new HashMap<String, String>();


    public static class Caller implements Callable<Boolean> {
        private String name;

        public Caller(String name) {
            this.name = name;
        }

        public Boolean call() {
            try {
                while(!resp.containsKey(name));
                /***
                    tips:
                    it will loop util resp has the name 
                ***/
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }
    }

    private static class Runner implements Runnable {

        private String name;

        public Runner(String deviceId) {
            this.name = deviceId;
        }

        public void run() {
//            ExecutorService excutor = Executors.newSingleThreadExecutor();
//            Future<Boolean> future = excutor.submit(new Caller(name));
//            try {
//                future.get(1500, TimeUnit.MILLISECONDS);
//            } catch (TimeoutException e) {
//              System.out.println("timeout");
//            } catch (Exception e) {
//                e.printStackTrace();
//            } finally {
//                excutor.shutdownNow();
//            }
            try {
                Upstream us = new Upstream();

                us.resolution();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public static void main(String[] args) {
        String shortAddress = "TEST";
        String endPoint = "1";
        String deviceId = shortAddress + endPoint;
        Downstreaam ds = new Downstreaam();
        Upstream us = new Upstream();
        // 向网关发送指令
        ds.send2Gateway(shortAddress, endPoint);
//        new Thread(new Runner(deviceId)).start();
        // 模拟指令返回
        new Thread(new Runner(deviceId)).start();
        // 新建线程获取指令返回
        ExecutorService excutor = Executors.newSingleThreadExecutor();
        Future<Boolean> future = excutor.submit(new Caller(deviceId));
        try {
            // 查询指令返回，最多等待固定时间
            future.get(1500, TimeUnit.MILLISECONDS);
            System.out.printf("resp of device %s is %s\n", deviceId, resp.get(deviceId));
        } catch (TimeoutException e) { // 超时
            System.out.println("timeout");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {  // 关闭线程
            System.out.println("finally");
            excutor.shutdownNow();
//            resp.remove(deviceId);
        }
    }
}
