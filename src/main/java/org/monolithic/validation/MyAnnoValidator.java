package org.monolithic.validation;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.internal.engine.valuecontext.ValueContext;
import org.monolithic.vo.com.ComVo;
import org.springframework.beans.BeanUtils;

import javax.validation.ConstraintValidatorContext;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * @author xiangqian
 * @date 21:38 2022/09/29
 */
@Slf4j
public class MyAnnoValidator implements ConstraintValidator<MyAnno, String> {

    @SneakyThrows
    @Override
    public boolean isValid(String str, ConstraintValidatorContext constraintValidatorContext, ValueContext<?, ?> valueContext) {
        log.info("isValid start");

        if (StringUtils.isEmpty(str)) {
            log.error("Monitor value is Null");
            return false;
        }

        // PropertyDescriptors
        Object currentBean = valueContext.getCurrentBean();
        PropertyDescriptor[] propertyDescriptors = BeanUtils.getPropertyDescriptors(currentBean.getClass());
        for (PropertyDescriptor propertyDescriptor : propertyDescriptors) {
            if (propertyDescriptor.getName().equals(valueContext.getPropertyPath().getLeafNode().getName())) {
                Method writeMethod = propertyDescriptor.getWriteMethod();
                Class<?>[] parameterTypes = writeMethod.getParameterTypes();
                if (ArrayUtils.isNotEmpty(parameterTypes) && parameterTypes.length == 1) {
                    Class<?> parameterType = parameterTypes[0];
                    if (String.class.isAssignableFrom(parameterType)) {
                        writeMethod.invoke(currentBean, StringUtils.trimToNull(str));
                    }
                }
                break;
            }
        }

        return true;
    }

}
