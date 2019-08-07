package service;

import controller.RpcController;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class Upstream {
    public void resolution(String deviceId) {
        Map<String, String> resp = RpcController.resp;
        resp.put(deviceId, "succeed");
    }
}
