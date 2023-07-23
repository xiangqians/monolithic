package org.xiangqian.monolithic.sys.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotNull;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.HttpHeaders;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.xiangqian.monolithic.sys.entity.TestEntity;
import org.xiangqian.monolithic.model.Resp;
import org.xiangqian.monolithic.validation.group.Add;
import org.xiangqian.monolithic.validation.group.Upd;

/**
 * @author xiangqian
 * @date 21:35 2023/03/28
 */
@RestController
@RequestMapping("/test")
@Tag(name = "用户接口", description = "用户相关接口")
@SecurityRequirement(name = HttpHeaders.AUTHORIZATION)
public class TestController {

    // @ParameterObject
    // The usage of @ParameterObject is wrong. This annotation extracts fields from parameter object.
    // You should simply use @Parameter swagger standard annotation instead, or mark the parameter explicitly as @RequestParam.
    //
    // @PostMapping(value = "/persons")
    // public void create(@Parameter(in = ParameterIn.QUERY) Long id, @RequestBody Object o){}
    // I have added control to prevent this error on bad usage of @ParameterObject annotation.
    //
    // Additionally, with the next release, it will be detected out of the box, without any extra swagger-annotations.
    //
    // @ParameterObject is designed for HTTP GET Methods.
    // Please have a look at the documentation:
    // https://springdoc.org/faq.html#how-can-i-map-pageable-spring-date-commons-object-to-correct-url-parameter-in-swagger-ui

    @GetMapping("/get")
    @Operation(summary = "get", description = "描述get")
    public Resp<TestEntity> get(HttpServletRequest req, @ParameterObject @Validated({Add.class}) TestEntity entity123) throws Exception {
        System.out.println("-----------" + req.getHeader("Authorization") + ", " + entity123);
        return Resp.<TestEntity>builder()
                .build();
    }

    @GetMapping("/get2")
    @Operation(summary = "get2", description = "描述get2")
    public Resp<TestEntity> get2(@Parameter(description = "名称123") String name123) throws Exception {
        System.out.println("-----------" + name123);
        return Resp.<TestEntity>builder()
                .build();
    }

    @GetMapping("/get3")
    @Operation(summary = "get3", description = "描述get3")
    public @NotNull Resp<TestEntity> get3(HttpServletRequest req, @ParameterObject @Validated({Upd.class}) TestEntity entity) throws Exception {
        System.out.println("-----------" + entity);
        return Resp.<TestEntity>builder()
                .build();
    }

    @PostMapping("/post")
    @Operation(summary = "post", description = "描述post")
    public Resp<?> post(@RequestBody @Validated(Add.class) TestEntity entity) throws Exception {
        System.out.println("-----------" + entity);
        return null;
    }

}
