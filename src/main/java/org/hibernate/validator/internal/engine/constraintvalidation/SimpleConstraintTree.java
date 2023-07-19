/*
 * Hibernate Validator, declare and validate application constraints
 *
 * License: Apache License, Version 2.0
 * See the license.txt file in the root directory or <http://www.apache.org/licenses/LICENSE-2.0>.
 */
package org.hibernate.validator.internal.engine.constraintvalidation;

import jakarta.validation.ConstraintValidator;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.internal.constraintvalidators.bv.NotBlankValidator;
import org.hibernate.validator.internal.engine.validationcontext.ValidationContext;
import org.hibernate.validator.internal.engine.valuecontext.BeanValueContext;
import org.hibernate.validator.internal.engine.valuecontext.ValueContext;
import org.hibernate.validator.internal.metadata.aggregated.BeanMetaData;
import org.hibernate.validator.internal.metadata.aggregated.PropertyMetaData;
import org.hibernate.validator.internal.metadata.descriptor.ConstraintDescriptorImpl;
import org.hibernate.validator.internal.metadata.location.ConstraintLocation;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;
import org.springframework.beans.BeanUtils;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Objects;

/**
 * A constraint tree for a simple constraint i.e. not a composing one.
 * <p>
 * v8.0.0.FinalF
 * https://github.com/hibernate/hibernate-validator/blob/8.0.0.Final/engine/src/main/java/org/hibernate/validator/internal/engine/constraintvalidation/SimpleConstraintTree.java
 *
 * @author Hardy Ferentschik
 * @author Federico Mancini
 * @author Dag Hovland
 * @author Kevin Pollet &lt;kevin.pollet@serli.com&gt; (C) 2012 SERLI
 * @author Guillaume Smet
 * @author Marko Bekhta
 * @author xiangqian revised in 20:22 2023/05/12
 */
class SimpleConstraintTree<B extends Annotation> extends ConstraintTree<B> {

    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());

    public SimpleConstraintTree(ConstraintValidatorManager constraintValidatorManager, ConstraintDescriptorImpl<B> descriptor, Type validatedValueType) {
        super(constraintValidatorManager, descriptor, validatedValueType);
    }

    @Override
    protected void validateConstraints(ValidationContext<?> validationContext,
                                       ValueContext<?, ?> valueContext,
                                       Collection<ConstraintValidatorContextImpl> violatedConstraintValidatorContexts) {

        if (LOG.isTraceEnabled()) {
            if (validationContext.isShowValidatedValuesInTraceLogs()) {
                LOG.tracef("Validating value %s against constraint defined by %s.", valueContext.getCurrentValidatedValue(), descriptor);
            } else {
                LOG.tracef("Validating against constraint defined by %s.", descriptor);
            }
        }

        // find the right constraint validator
        ConstraintValidator<B, ?> validator = getInitializedConstraintValidator(validationContext, valueContext);

        // create a constraint validator context
        ConstraintValidatorContextImpl constraintValidatorContext =
                validationContext.createConstraintValidatorContextFor(descriptor, valueContext.getPropertyPath());

        // 增强
        try {
            enhance(valueContext, validator);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        // validate
        if (validateSingleConstraint(valueContext, constraintValidatorContext, validator).isPresent()) {
            violatedConstraintValidatorContexts.add(constraintValidatorContext);
        }
    }

    /**
     * 增强
     *
     * @param valueContext
     * @param validator
     */
    private void enhance(ValueContext<?, ?> valueContext, ConstraintValidator<B, ?> validator) throws InvocationTargetException, IllegalAccessException {
        // BeanValueContext ?
        if (!(valueContext instanceof BeanValueContext)) {
            return;
        }

        // 只校验注解使用在属性上的属性
        if (ConstraintLocation.ConstraintLocationKind.FIELD != valueContext.getConstraintLocationKind()) {
            return;
        }

        // BeanValueContext
        BeanValueContext<?, ?> beanValueContext = (BeanValueContext<?, ?>) valueContext;
        BeanMetaData<?> currentBeanMetaData = beanValueContext.getCurrentBeanMetaData();
        if (Objects.isNull(currentBeanMetaData)) {
            return;
        }

        // PropertyMetaData
        PropertyMetaData propertyMetaData = currentBeanMetaData.getMetaDataFor(valueContext.getPropertyPath().getLeafNode().getName());
        if (Objects.isNull(propertyMetaData)) {
            return;
        }

        // name
        String name = propertyMetaData.getName();

        // type
        Type type = propertyMetaData.getType();
        if (Objects.isNull(type)) {
            return;
        }

        // value
        Object value = valueContext.getCurrentValidatedValue();
        if (Objects.isNull(value)) {
            return;
        }

        // @NotBlank
        // 去除前后空格
        if (validator instanceof NotBlankValidator) {
            if (!(type == String.class)) {
                return;
            }

            Object object = valueContext.getCurrentBean();
            PropertyDescriptor propertyDescriptor = BeanUtils.getPropertyDescriptor(object.getClass(), name);
            Method method = propertyDescriptor.getWriteMethod();
            method.invoke(object, StringUtils.trimToEmpty(value.toString()));
            return;
        }

    }

}
