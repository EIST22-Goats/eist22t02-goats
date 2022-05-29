package eist.tum_social.tum_social.model;

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
