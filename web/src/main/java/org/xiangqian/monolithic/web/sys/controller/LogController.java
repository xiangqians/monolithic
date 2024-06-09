package org.xiangqian.monolithic.web.sys.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author xiangqian
 * @date 14:18 2024/06/09
 */
@RestController
@Tag(name = "日志接口")
@RequestMapping("/api/sys/log")
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
public class LogController {

}
