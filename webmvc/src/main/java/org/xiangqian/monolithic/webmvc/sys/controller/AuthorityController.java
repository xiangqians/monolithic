package org.xiangqian.monolithic.webmvc.sys.controller;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.xiangqian.monolithic.webmvc.WebmvcController;

/**
 * @author xiangqian
 * @date 13:02 2024/06/02
 */
@RestController
@Tag(name = "权限接口")
@RequestMapping("/api/sys/authority")
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
public class AuthorityController extends WebmvcController {

}
