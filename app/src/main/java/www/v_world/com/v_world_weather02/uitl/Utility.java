package www.v_world.com.v_world_weather02.uitl;

import android.text.TextUtils;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.security.PublicKey;

import www.v_world.com.v_world_weather02.db.City;
import www.v_world.com.v_world_weather02.db.County;
import www.v_world.com.v_world_weather02.db.Province;
import www.v_world.com.v_world_weather02.gson.Weather;

/**
 * Created by Administrator on 2017/6/1.
 */
public class Utility  {
    /**
     * 解析和处理服务器返回的省级数据
     * @param reponse
     * @return
     */
    public static boolean handleProvinceResponse(String reponse){
        if(!TextUtils.isEmpty(reponse)){
            try{
                JSONArray allProvinces = new JSONArray(reponse) ;

                for(int i = 0; i < allProvinces.length(); i ++){
                    JSONObject provinceobject = allProvinces.getJSONObject(i);

                    Province province = new Province();

                    province.setProvinceName(provinceobject.getString("name"));

                    province.setProvinceCode(provinceobject.getInt("id"));

                    province.save();
                }


            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 解析和处理市级服务器的返回市级数据
     * @param response
     * @param provinceId
     * @return
     */
    public static boolean handleCityResponse(String response, int provinceId){
        if(!TextUtils.isEmpty(response)){
            try{
                JSONArray allCities = new JSONArray(response) ;

                for(int i = 0; i < allCities.length(); i ++){
                    JSONObject Cityobject = allCities.getJSONObject(i);

                    City city = new City();

                    city.setCityName(Cityobject.getString("name"));

                    city.setCityCode(Cityobject.getInt("id"));

                    city.setProvinceId(provinceId);

                    city.save();

                }


            }catch (Exception e){
                e.printStackTrace();
            }
        }


      return  false;
    }

    /**
     * 解析和处理县级服务器的返回市级数据
     * @param response
     * @param cityId
     * @return
     */
    public static boolean handleCountyResponse(String response, int cityId){
        if(!TextUtils.isEmpty(response)){
            try{
                JSONArray allCounties = new JSONArray(response) ;

                for(int i = 0; i < allCounties.length(); i ++){
                    JSONObject countyobject = allCounties.getJSONObject(i);

                    County county = new County();

                    county.setCountyName(countyobject.getString("name"));

                    county.setWeatherId(countyobject.getString("weather_id"));

                    county.setCityId(cityId);

                    county.save();

                }


            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return  false;

    }


    public static Weather handleWeatherResponse(String response){

        try{
            JSONObject jsonObject = new JSONObject(response);
            JSONArray jsonArray = jsonObject.getJSONArray("HeWeather");
            String weatherContent = jsonArray.getJSONObject(0).toString();
            return  new Gson().fromJson(weatherContent, Weather.class);
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;

    }

}


