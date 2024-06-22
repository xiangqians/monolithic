package org.xiangqian.monolithic.websocket.sys.receiver;

import org.xiangqian.monolithic.websocket.Endpoint;
import org.xiangqian.monolithic.websocket.Receiver;

import java.util.List;
import java.util.Map;

/**
 * @author xiangqian
 * @date 21:48 2024/06/17
 */
@Receiver("/test")
public interface TestReceiver {

    @Receiver("/get")
    Map get(Endpoint endpoint, List<Map> message);

    void test();

}
