package eist.tum_social.tum_social.persistent_data_storage.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ForeignTable {

    String foreignTableName();
    String ownColumnName();

}
