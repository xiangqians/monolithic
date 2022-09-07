package org.monolithic.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.monolithic.annotation.DocketGroup;
import org.monolithic.constant.DocketGroupName;
import org.monolithic.constant.DocketGroupTag;
import org.monolithic.pagination.Page;
import org.monolithic.resp.DefaultStatusCode;
import org.monolithic.resp.Response;
import org.monolithic.service.UserService;
import org.monolithic.vo.user.UserVo;
import org.monolithic.vo.user.param.UserAddVoParam;
import org.monolithic.vo.user.param.UserModifyVoParam;
import org.monolithic.vo.user.param.UserPageVoParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 用户表 前端控制器
 *
 * @author xiangqian
 * @date 22:04 2022/09/06
 */
@RestController
@RequestMapping("/user")
@Api(value = "user", tags = "用户信息管理")
public class UserController {

    @Autowired
    private UserService userService;

    @ResponseBody
    @GetMapping("/page")
    @ApiOperation("分页查询用户信息")
    @PreAuthorize("@pre.hasPerm('/user/page')")
    @DocketGroup(name = DocketGroupName.TEST, tags = {DocketGroupTag.USER})
    public Response<Page<UserVo>> page(@Valid UserPageVoParam voParam) throws Exception {
        return Response.<Page<UserVo>>builder()
                .statusCode(DefaultStatusCode.OK)
                .body(userService.queryForPage(voParam))
                .build();
    }

    @ResponseBody
    @ApiOperation("根据id查询用户信息")
    @GetMapping("/queryById/{id}")
    @PreAuthorize("@pre.hasPerm('/user/queryById/{id}')")
    @DocketGroup(name = DocketGroupName.TEST, tags = {DocketGroupTag.USER})
    @ApiImplicitParam(name = "id", value = "用户id", required = true)
    public Response<UserVo> queryById(@PathVariable("id") Long id) throws Exception {
        return Response.<UserVo>builder()
                .statusCode(DefaultStatusCode.OK)
                .body(userService.queryById(id))
                .build();
    }

    @ResponseBody
    @ApiOperation("修改用户信息")
    @PutMapping("/updateById")
    @PreAuthorize("@pre.hasPerm('/user/updateById')")
    public Response<Boolean> updateById(@RequestBody @Valid UserModifyVoParam voParam) throws Exception {
        return Response.<Boolean>builder()
                .statusCode(DefaultStatusCode.OK)
                .body(userService.updateById(voParam))
                .build();
    }

    @ApiOperation("删除用户信息")
    @DeleteMapping("/delete/{id}")
    @PreAuthorize("@pre.hasPerm('/user/delete/{id}')")
    @ApiImplicitParam(name = "id", value = "用户id", required = true)
    public Response<Boolean> deleteById(@PathVariable("id") Long id) throws Exception {
        return Response.<Boolean>builder()
                .statusCode(DefaultStatusCode.OK)
                .body(userService.deleteById(id))
                .build();
    }

    @PostMapping("/save")
    @ApiOperation("新增用户信息")
    @PreAuthorize("@pre.hasPerm('/user/save')")
    public Response<Boolean> save(@RequestBody @Valid UserAddVoParam voParam) throws Exception {
        return Response.<Boolean>builder()
                .statusCode(DefaultStatusCode.OK)
                .body(userService.save(voParam))
                .build();
    }

}
