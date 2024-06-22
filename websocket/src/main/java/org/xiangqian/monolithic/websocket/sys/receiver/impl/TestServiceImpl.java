package org.xiangqian.monolithic.websocket.sys.receiver.impl;

import org.springframework.stereotype.Service;
import org.xiangqian.monolithic.websocket.Endpoint;
import org.xiangqian.monolithic.websocket.sys.receiver.TestReceiver;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * @author xiangqian
 * @date 21:48 2024/06/17
 */
@Service
public class TestServiceImpl implements TestReceiver {

    @Override
    public Map get(Endpoint endpoint, List<Map> message) {
        return Map.of("key1", "test",
                "time", LocalDateTime.now());
    }

    @Override
    public void test() {
        System.out.println("test impl");
    }

}
