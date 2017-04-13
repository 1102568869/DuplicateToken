package tech.washmore.util.duplicate.annotations;

import java.lang.annotation.*;

/**
 * Created by Washmore on 2017/4/10.
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface DuplicateToken {
}
