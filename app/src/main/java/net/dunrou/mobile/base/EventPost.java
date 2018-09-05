package net.dunrou.mobile.base;

import android.location.Location;

import net.dunrou.mobile.base.converter.ListUriConverter;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Id;

import java.net.URI;
import java.util.List;

/**
 * Created by Stephen on 2018/9/5.
 */

public class EventPost {
    @Id(autoincrement = true)
    private Long eventPostId;
    private String userId;
    @Convert(converter = ListUriConverter.class, columnType = String.class)
    private List<URI> photos;
    private Location location;
    private Integer time;
}
