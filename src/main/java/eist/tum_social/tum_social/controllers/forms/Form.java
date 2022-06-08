package eist.tum_social.tum_social.controllers.forms;

import java.lang.reflect.Field;

import static eist.tum_social.tum_social.datastorage.util.BeanUtil.getValueOfField;
import static eist.tum_social.tum_social.datastorage.util.BeanUtil.setValueOfField;

public abstract class Form<T> {
    
    public void apply(T object) {
        for (Field field : getClass().getDeclaredFields()) {
            Object value = getValueOfField(field, this);
            if (value != null) {
                setValueOfField(field, object, value);
            }
        }
    }

}
