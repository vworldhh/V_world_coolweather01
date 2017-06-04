package www.v_world.com.v_world_weather02.gson;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Administrator on 2017/6/2.
 */
public class Basic {
    @SerializedName("city")
    public String cityName;

    @SerializedName("id")
    public  String weatherId;

    public Update update;

    public class Update{
        @SerializedName("loc")
        public  String updateTime;
    }
}
