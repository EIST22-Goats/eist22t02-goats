package eist.tum_social.tum_social.datastorage.util;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * The annotation that specifies the table name of the bridging table.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface BridgingTable {

    String bridgingTableName();

    String ownForeignColumnName();

    String otherForeignColumnName();

    /**
     * Bidirectional means that the order of own - and otherForeignColumnName is indifferent.
     */
    boolean bidirectional() default false;

}
