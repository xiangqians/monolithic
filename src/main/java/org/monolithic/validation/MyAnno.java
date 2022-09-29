package org.monolithic.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * @author xiangqian
 * @date 21:37 2022/09/29
 */
@Documented
@Target({ElementType.PARAMETER, ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {MyAnnoValidator.class})
public @interface MyAnno {

    /**
     * ValidationMessages.properties
     *
     * @return
     */
    String message() default "{javax.validation.constraints.Min.message}";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
