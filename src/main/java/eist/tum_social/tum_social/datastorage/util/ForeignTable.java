package eist.tum_social.tum_social.datastorage.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The annotation that specifies table name of the foreign object and the name of the column that contains the foreign 
 * id.
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ForeignTable {

    String foreignTableName();
    String ownColumnName();

}
