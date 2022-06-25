package eist.tum_social.TestClasses;

import org.springframework.ui.Model;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class TestModel implements Model {

    Map<String, Object> values = new HashMap<>();

    @Override
    public Model addAttribute(String attributeName, Object attributeValue) {
        values.put(attributeName, attributeValue);
        return this;
    }

    @Override
    public Model addAttribute(Object attributeValue) {
        String name = attributeValue.getClass().getTypeName();
        values.put(name, attributeValue);
        return this;
    }

    @Override
    public Model addAllAttributes(Collection<?> attributeValues) {
        for (var value:attributeValues) {
            addAttribute(value);
        }
        return this;
    }

    @Override
    public Model addAllAttributes(Map<String, ?> attributes) {
        for (var key:attributes.keySet()) {
            addAttribute(key, attributes.get(key));
        }
        return this;
    }

    @Override
    public Model mergeAttributes(Map<String, ?> attributes) {
        for (var key:attributes.keySet()) {
            addAttribute(key, attributes.get(key));
        }
        return this;
    }

    @Override
    public boolean containsAttribute(String attributeName) {
        return values.containsKey(attributeName);
    }

    @Override
    public Object getAttribute(String attributeName) {
        return values.get(attributeName);
    }

    @Override
    public Map<String, Object> asMap() {
        return values;
    }
}
