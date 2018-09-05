package net.dunrou.mobile.base.converter;

import com.google.gson.Gson;

import org.greenrobot.greendao.converter.PropertyConverter;

import java.util.Date;

/**
 * Created by Stephen on 2018/9/5.
 */

public class DateConverter implements PropertyConverter<Date, String> {
    private Gson gson = new Gson();

    @Override
    public Date convertToEntityProperty(String databaseValue) {
        return gson.fromJson(databaseValue, Date.class);
    }

    @Override
    public String convertToDatabaseValue(Date entityProperty) {
        return gson.toJson(entityProperty);
    }
}
