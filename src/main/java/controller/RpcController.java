package controller;

import service.Downstreaam;
import service.Upstream;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
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
                while(!resp.containsKey(name)){
//                    System.out.println(resp.size());
                    Thread.sleep(100);
                }
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
            try {
                Thread.sleep(500);
                Upstream us = new Upstream();
                for (int i = 0; i < 100; i ++) {
                    int x = new Random().nextInt(100);
                    String endPoint = String.valueOf(x);
                    us.resolution(name + endPoint);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public static void main(String[] args) {
//        String shortAddress = "TEST";
//        String endPoint = "1";
        // 模拟指令返回
        new Thread(new Runner("TEST")).start();

        for(int i = 0; i < 100; i++) {
            String shortAddress = "TEST";
            String endPoint = String.valueOf(i);
            String deviceId = shortAddress + endPoint;
            Downstreaam ds = new Downstreaam();
            Upstream us = new Upstream();
            // 向网关发送指令
            ds.send2Gateway(shortAddress, endPoint);

            // 新建线程获取指令返回
            ExecutorService excutor = Executors.newSingleThreadExecutor();
            Future<Boolean> future = excutor.submit(new Caller(deviceId));
            try {
                // 查询指令返回，最多等待固定时间
                future.get(1000, TimeUnit.MILLISECONDS);
                System.out.printf("resp of device %s is %s\n", deviceId, resp.get(deviceId));
            } catch (TimeoutException e) { // 超时
                System.out.println("timeout");
            } catch (Exception e) {
                e.printStackTrace();
            } finally {  // 关闭线程
                excutor.shutdownNow();
                resp.remove(deviceId);
            }
            System.out.println(resp.size());
        }
    }
}
