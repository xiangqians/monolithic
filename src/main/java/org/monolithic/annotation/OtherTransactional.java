package org.monolithic.annotation;

import org.springframework.core.annotation.AliasFor;
import org.springframework.transaction.annotation.Transactional;

import java.lang.annotation.*;

/**
 * @author xiangqian
 * @date 10:34 2022/04/20
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
@Transactional
public @interface OtherTransactional {

    /**
     * {@link org.monolithic.configure.MultipleDataSourceConfiguration#otherDataSourceTransactionManager(org.springframework.core.env.Environment, java.util.Map, org.springframework.beans.factory.ObjectProvider)}
     *
     * @return
     */
    @AliasFor(annotation = Transactional.class)
    String transactionManager() default "otherDataSourceTransactionManager";

    @AliasFor(annotation = Transactional.class)
    int timeout() default -1;

    @AliasFor(annotation = Transactional.class)
    boolean readOnly() default false;

}
