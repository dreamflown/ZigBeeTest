package service;

import controller.RpcController;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class Upstream {
    public void resolution() {
        Map<String, String> resp = RpcController.resp;
        String deviceId = "TEST1";
        resp.put(deviceId, "succeed");
    }
}
