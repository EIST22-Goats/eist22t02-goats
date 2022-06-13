package eist.tum_social.tum_social.controllers.forms;

import eist.tum_social.tum_social.controllers.util.FormIgnore;

import java.lang.reflect.Field;
import java.util.Arrays;

import static eist.tum_social.tum_social.datastorage.util.BeanUtil.getValueOfField;
import static eist.tum_social.tum_social.datastorage.util.BeanUtil.setValueOfField;
import static eist.tum_social.tum_social.datastorage.util.BeanUtil.hasAnnotation;

public abstract class Form<T> {

    public void apply(T object) {
        for (Field field : getClass().getDeclaredFields()) {
            if (!hasAnnotation(field, FormIgnore.class)) {
                Object value = getValueOfField(field, this);
                if (value != null) {
                    setValueOfField(field, object, value);
                }
            }
        }
    }

}
