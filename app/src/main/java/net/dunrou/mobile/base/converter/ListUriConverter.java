package net.dunrou.mobile.base.converter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.greenrobot.greendao.converter.PropertyConverter;

import java.lang.reflect.Type;
import java.net.URI;
import java.util.List;

/**
 * Created by Stephen on 2018/9/5.
 */

public class ListUriConverter implements PropertyConverter<List<URI>, String> {
    private Gson gson = new Gson();
    @Override
    public List<URI> convertToEntityProperty(String databaseValue) {
        Type listType = new TypeToken<List<URI>>() {}.getType();
        return gson.fromJson(databaseValue, listType);
    }

    @Override
    public String convertToDatabaseValue(List<URI> entityProperty) {
        return gson.toJson(entityProperty);
    }
}
