//package org.xiangqian.monolithic.web.sys.controller;
//
//import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
//import jakarta.servlet.http.HttpServletRequest;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.userdetails.User;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//import org.xiangqian.monolithic.biz.sys.entity.UserEntity;
//import org.xiangqian.monolithic.biz.sys.mapper.UserMapper;
//import org.xiangqian.monolithic.web.Response;
//
///**
// * 系统认证接口
// *
// * @author xiangqian
// * @date 23:33 2024/05/30
// */
//@RestController
//@RequestMapping("/auth")
//public class AuthController {
//
//    //--header 'Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJhdWQiOlsib3JkZXItYml6IiwidXNlci1iaXoiXSwiZXhwIjoxNjQ5ODYzNzE2LCJ1c2VyX25hbWUiOiJhZG1pbiIsImp0aSI6ImI2NDQ2ZGEyLTdmYmItNDQ0Mi05OWUxLTAyYjE5YWNiZWVhOCIsImNsaWVudF9pZCI6ImNsaWVudF8xIiwic2NvcGUiOlsicmVhZCJdfQ.McajN084be_ImcMNLhiN5PhoWvTUk0P7ZKKMs7Jsdyo'
//
//    /**
//     * 授权（获取token）
//     *
//     * @return
//     */
//    @PostMapping("/token")
//    public Response<String> token(HttpServletRequest request) {
//        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken("name:test", "test"));
//        User user = (User) authentication.getPrincipal();
//        return Response.<String>builder()
//                .data(null)
//                .build();
//    }
//
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//    @Bean
//    public UserDetailsService userDetailsService(UserMapper userMapper) {
//        return username -> {
//            UserEntity userEntity = null;
//            if (username.startsWith("name:")) {
//                String name = username.substring("name:".length());
//                userEntity = userMapper.selectOne(new LambdaQueryWrapper<UserEntity>().eq(UserEntity::getName, name).last("LIMIT 1"));
//            } else if (username.startsWith("phone:")) {
//                String phone = username.substring("phone:".length());
//                userEntity = userMapper.selectOne(new LambdaQueryWrapper<UserEntity>().eq(UserEntity::getPhone, phone).last("LIMIT 1"));
//            }
//            return userEntity;
//        };
//    }
//
//    /**
//     * 撤销授权
//     *
//     * @return
//     */
//    //    @PreAuthorize("hasScope('read')")
////    @PreAuthorize("hasRole('ADMIN')")
////    @PreAuthorize("hasAnyAuthority('ADMIN')")
//    @RequestMapping("/revoke")
//    public Response<Boolean> revoke() {
//        return Response.<Boolean>builder()
//                .data(null)
//                .build();
//    }
//
//}
