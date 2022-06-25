package eist.tum_social;

import eist.tum_social.tum_social.datastorage.SqliteDatabase;

public class TestDatabase extends SqliteDatabase {
    public boolean updateCalled = false;
    public Object calledOn;

    public TestDatabase(String s) {
        super(s);
    }

    public void update(Object bean) {
        updateCalled = true;
        calledOn = bean;
        super.update(bean);
    }
}
