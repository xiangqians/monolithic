package org.xiangqian.monolithic.util;

import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * @author xiangqian
 * @date 19:17 2023/07/07
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest//(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ReqMappingTest {

    @Test
    public void test() {
        List<ReqMappingUtil.Info> infos = ReqMappingUtil.getInfos();
        for (ReqMappingUtil.Info info : infos) {
            System.out.println(info);
        }
    }

}
