package eist.tum_social.tum_social.datastorage.util;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public class BeanUtil {

    public static Object getValueOfField(Field field, Object bean) {
        try {
            return new PropertyDescriptor(field.getName(), bean.getClass()).getReadMethod().invoke(bean);
        } catch (IllegalAccessException | InvocationTargetException | IntrospectionException e) {
            throw new BeanFieldException("Failed accessing Field " + field.getName() + " of Object " + bean);
        }
    }

    public static void setValueOfField(Field field, Object bean, Object value) {
        try {
            if (value != null) {
                new PropertyDescriptor(field.getName(), bean.getClass()).getWriteMethod().invoke(bean, value);
            }
        } catch (IllegalAccessException | InvocationTargetException | IntrospectionException e) {
            throw new BeanFieldException(e + " Failed accessing Field " + field.getName() + " of Object " + bean);
        }
    }


}
