package org.xiangqian.monolithic.validation;

import jakarta.validation.ConstraintValidator;
import org.hibernate.validator.internal.engine.ValidatorImpl;
import org.hibernate.validator.internal.engine.constraintvalidation.ConstraintTree;
import org.hibernate.validator.internal.metadata.core.MetaConstraint;
import org.springframework.boot.autoconfigure.validation.ValidatorAdapter;
import org.springframework.validation.DataBinder;
import org.springframework.validation.beanvalidation.SpringValidatorAdapter;
import org.springframework.web.method.annotation.ModelAttributeMethodProcessor;
import org.springframework.web.method.support.InvocableHandlerMethod;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.mvc.method.AbstractHandlerMethodAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod;

/**
 * {@link DispatcherServlet#doDispatch(jakarta.servlet.http.HttpServletRequest, jakarta.servlet.http.HttpServletResponse)}
 * {@link AbstractHandlerMethodAdapter#handle(jakarta.servlet.http.HttpServletRequest, jakarta.servlet.http.HttpServletResponse, java.lang.Object)}
 * {@link RequestMappingHandlerAdapter#handleInternal(jakarta.servlet.http.HttpServletRequest, jakarta.servlet.http.HttpServletResponse, org.springframework.web.method.HandlerMethod)}
 * {@link RequestMappingHandlerAdapter#invokeHandlerMethod(jakarta.servlet.http.HttpServletRequest, jakarta.servlet.http.HttpServletResponse, org.springframework.web.method.HandlerMethod)}
 * {@link ServletInvocableHandlerMethod#invokeAndHandle(org.springframework.web.context.request.ServletWebRequest, org.springframework.web.method.support.ModelAndViewContainer, java.lang.Object...)}
 * {@link InvocableHandlerMethod#getMethodArgumentValues(org.springframework.web.context.request.NativeWebRequest, org.springframework.web.method.support.ModelAndViewContainer, java.lang.Object...)}
 * {@link ModelAttributeMethodProcessor#resolveArgument(org.springframework.core.MethodParameter, org.springframework.web.method.support.ModelAndViewContainer, org.springframework.web.context.request.NativeWebRequest, org.springframework.web.bind.support.WebDataBinderFactory)}
 * {@link ModelAttributeMethodProcessor#validateIfApplicable(org.springframework.web.bind.WebDataBinder, org.springframework.core.MethodParameter)}
 * {@link DataBinder#validate(java.lang.Object...)}
 * {@link ValidatorAdapter#validate(java.lang.Object, org.springframework.validation.Errors, java.lang.Object...)}
 * {@link SpringValidatorAdapter#validate(java.lang.Object, org.springframework.validation.Errors, java.lang.Object...)}
 * {@link SpringValidatorAdapter#processConstraintViolations(java.util.Set, org.springframework.validation.Errors)}
 * {@link ValidatorImpl#validate(java.lang.Object, java.lang.Class[])}
 * {@link ValidatorImpl#validateInContext(org.hibernate.validator.internal.engine.validationcontext.BaseBeanValidationContext, org.hibernate.validator.internal.engine.valuecontext.BeanValueContext, org.hibernate.validator.internal.engine.groups.ValidationOrder)}
 * {@link ValidatorImpl#validateConstraintsForCurrentGroup(org.hibernate.validator.internal.engine.validationcontext.BaseBeanValidationContext, org.hibernate.validator.internal.engine.valuecontext.BeanValueContext)}
 * {@link ValidatorImpl#validateConstraintsForNonDefaultGroup(org.hibernate.validator.internal.engine.validationcontext.BaseBeanValidationContext, org.hibernate.validator.internal.engine.valuecontext.BeanValueContext)}
 * {@link ValidatorImpl#validateMetaConstraints(org.hibernate.validator.internal.engine.validationcontext.BaseBeanValidationContext, org.hibernate.validator.internal.engine.valuecontext.ValueContext, java.lang.Object, java.lang.Iterable)}
 * {@link ValidatorImpl#validateMetaConstraint(org.hibernate.validator.internal.engine.validationcontext.BaseBeanValidationContext, org.hibernate.validator.internal.engine.valuecontext.ValueContext, java.lang.Object, org.hibernate.validator.internal.metadata.core.MetaConstraint)}
 * {@link MetaConstraint#validateConstraint(org.hibernate.validator.internal.engine.validationcontext.ValidationContext, org.hibernate.validator.internal.engine.valuecontext.ValueContext)}
 * {@link org.hibernate.validator.internal.engine.constraintvalidation.SimpleConstraintTree#validateConstraints(org.hibernate.validator.internal.engine.validationcontext.ValidationContext, org.hibernate.validator.internal.engine.valuecontext.ValueContext, java.util.Collection)}
 * {@link ConstraintTree#validateSingleConstraint(org.hibernate.validator.internal.engine.valuecontext.ValueContext, org.hibernate.validator.internal.engine.constraintvalidation.ConstraintValidatorContextImpl, jakarta.validation.ConstraintValidator)}
 * {@link ConstraintValidator#isValid(java.lang.Object, jakarta.validation.ConstraintValidatorContext)}
 * 综上所述，只要覆盖 {@link  org.hibernate.validator.internal.engine.constraintvalidation.SimpleConstraintTree} 即可处理增强校验注解。
 * 如何覆盖 {@link  org.hibernate.validator.internal.engine.constraintvalidation.SimpleConstraintTree} ？
 * {@link org.hibernate.validator.internal.engine.constraintvalidation.SimpleConstraintTree#SimpleConstraintTree(org.hibernate.validator.internal.engine.constraintvalidation.ConstraintValidatorManager, org.hibernate.validator.internal.metadata.descriptor.ConstraintDescriptorImpl, java.lang.reflect.Type)}
 * {@link ConstraintTree#of(org.hibernate.validator.internal.engine.constraintvalidation.ConstraintValidatorManager, org.hibernate.validator.internal.metadata.descriptor.ConstraintDescriptorImpl, java.lang.reflect.Type)}
 *
 * @author xiangqian
 * @date 21:15 2023/05/12
 */
public class EnhancedValidator {
}
