package org.monolithic.validation;

import org.hibernate.validator.internal.engine.valuecontext.ValueContext;

import javax.validation.ConstraintValidatorContext;
import java.lang.annotation.Annotation;

/**
 * 源码分析：
 * {@link org.springframework.web.method.annotation.ModelAttributeMethodProcessor#resolveArgument(org.springframework.core.MethodParameter, org.springframework.web.method.support.ModelAndViewContainer, org.springframework.web.context.request.NativeWebRequest, org.springframework.web.bind.support.WebDataBinderFactory)}
 * WebDataBinder binder = binderFactory.createBinder(webRequest, attribute, name);
 * {@link org.springframework.validation.DataBinder}
 * {@link org.hibernate.validator.internal.engine.constraintvalidation.ConstraintTree#validateSingleConstraint(org.hibernate.validator.internal.engine.valuecontext.ValueContext, org.hibernate.validator.internal.engine.constraintvalidation.ConstraintValidatorContextImpl, javax.validation.ConstraintValidator)}
 * {@link org.hibernate.validator.internal.engine.constraintvalidation.SimpleConstraintTree}
 * {@link org.hibernate.validator.internal.engine.constraintvalidation.ConstraintTree#of(org.hibernate.validator.internal.engine.constraintvalidation.ConstraintValidatorManager, org.hibernate.validator.internal.metadata.descriptor.ConstraintDescriptorImpl, java.lang.reflect.Type)}
 *
 * @author xiangqian
 * @date 22:56 2022/09/29
 */
public interface ConstraintValidator<A extends Annotation, T> extends javax.validation.ConstraintValidator<A, T> {

    @Override
    default boolean isValid(T t, ConstraintValidatorContext constraintValidatorContext) {
        return isValid(t, constraintValidatorContext, null);
    }

    boolean isValid(T t, ConstraintValidatorContext constraintValidatorContext, ValueContext<?, ?> valueContext);

}
