//package org.xiangqian.monolithic.util;
//
//import lombok.Data;
//import lombok.Getter;
//import org.apache.commons.collections4.CollectionUtils;
//import org.springframework.stereotype.Component;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RequestMethod;
//import org.springframework.web.method.HandlerMethod;
//import org.springframework.web.servlet.mvc.condition.PathPatternsRequestCondition;
//import org.springframework.web.servlet.mvc.condition.RequestMethodsRequestCondition;
//import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
//import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
//import org.springframework.web.util.pattern.PathPattern;
//
//import java.lang.reflect.Method;
//import java.util.*;
//import java.util.stream.Collectors;
//
///**
// * {@link RequestMapping}
// *
// * @author xiangqian
// * @date 19:45 2023/07/07
// */
//@Component
//public class ReqMappingUtil {
//
//    @Getter
//    private static List<Info> infos;
//
//    public ReqMappingUtil(RequestMappingHandlerMapping mapping) {
//        Map<RequestMappingInfo, HandlerMethod> map = mapping.getHandlerMethods();
//        List<Info> infos = new ArrayList<>(map.size());
//        for (Map.Entry<RequestMappingInfo, HandlerMethod> entry : map.entrySet()) {
//            RequestMappingInfo reqMappingInfo = entry.getKey();
//            PathPatternsRequestCondition reqCondition = reqMappingInfo.getPathPatternsCondition();
//            if (Objects.isNull(reqCondition)) {
//                continue;
//            }
//
//            Set<PathPattern> patterns = reqCondition.getPatterns();
//            if (CollectionUtils.isEmpty(patterns)) {
//                continue;
//            }
//
//            Info info = new Info();
//            info.setPatterns(patterns.stream().map(PathPattern::getPatternString).collect(Collectors.toSet()));
//            RequestMethodsRequestCondition methodsCondition = reqMappingInfo.getMethodsCondition();
//            info.setReqMethods(Optional.ofNullable(methodsCondition).map(RequestMethodsRequestCondition::getMethods).orElse(null));
//            HandlerMethod method = entry.getValue();
//            info.setMethod(method.getMethod());
//            infos.add(info);
//        }
//        ReqMappingUtil.infos = Collections.unmodifiableList(infos);
//    }
//
//    @Data
//    public static class Info {
//        private Set<RequestMethod> reqMethods;
//        private Set<String> patterns;
//        private Method method;
//
//        @Override
//        public String toString() {
//            return "Info {" +
//                    "\n\treqMethods\t= " + reqMethods +
//                    "\n\tpatterns\t= " + patterns +
//                    "\n\tmethod\t\t= " + method +
//                    "\n}";
//        }
//    }
//
//}
