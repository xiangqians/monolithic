package org.xiangqian.monolithic.websocket.test.receiver;

import org.xiangqian.monolithic.websocket.Endpoint;
import org.xiangqian.monolithic.websocket.Receiver;

import java.util.List;
import java.util.Map;

/**
 * @author xiangqian
 * @date 21:48 2024/06/17
 */
@Receiver("/test2")
public interface Test2Receiver {

    @Receiver("/get")
    void get(Endpoint endpoint, List<Map> message);

    void test();

}
