package eist.tum_social.tum_social.model;

/**
 * Every Model should override this class. Therefore, always only the id is used for comparison.
 */
public abstract class UniquelyIdentifiable {

    public abstract int getId();

    @Override
    public boolean equals(Object other) {
        if (other instanceof UniquelyIdentifiable oth) {
            return getId() == oth.getId();
        }
        return false;
    }

}
