package net.dunrou.mobile.base.converter;

import android.util.Log;

import com.google.gson.Gson;

import org.greenrobot.greendao.converter.PropertyConverter;

import java.net.URI;

/**
 * Created by Stephen on 2018/9/5.
 */

public class UriConverter implements PropertyConverter<URI, String> {
    private Gson gson = new Gson();
    @Override
    public URI convertToEntityProperty(String databaseValue) {
        if(databaseValue != null)
            Log.d("uri test", databaseValue);
        return gson.fromJson(databaseValue, URI.class);
    }

    @Override
    public String convertToDatabaseValue(URI entityProperty) {
        return gson.toJson(entityProperty);
    }
}
