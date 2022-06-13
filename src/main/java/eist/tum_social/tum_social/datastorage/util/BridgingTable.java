package eist.tum_social.tum_social.datastorage.util;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface BridgingTable {

    String bridgingTableName();
    String ownForeignColumnName();
    String otherForeignColumnName();
    boolean bidirectional() default false;

}
