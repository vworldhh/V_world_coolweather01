package www.v_world.com.v_world_weather02;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import www.v_world.com.v_world_weather02.gson.Forecast;
import www.v_world.com.v_world_weather02.gson.Weather;
import www.v_world.com.v_world_weather02.uitl.HttpUtil;
import www.v_world.com.v_world_weather02.uitl.Utility;

public class WeatherActivity extends AppCompatActivity {
    private ScrollView weatherLayout;
    private TextView titleCity;
    private TextView titleUpdateTime;
    private TextView degreeText;
    private TextView weatherInfoText;
    private LinearLayout forecastLayout;
    private TextView aqiText;
    private TextView pm25Text;
    private TextView comfortText;
    private TextView carWashText;
    private TextView sportText;
    private ImageView bingPicImg;
    private SwipeRefreshLayout swipeRefresh;
    private String mWeatherId;
    public DrawerLayout drawerLayout;
    private Button navbutton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        //初始化控件
        init();
        String weatherId = getIntent().getStringExtra("weather_id");
        requestWeather(weatherId);
        getImage();//获取图片
        setSwipRefresh();//下拉刷新
        setNavHome();//设置导航
//        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
//        //缓存为空
//        emptyPre();
//        String weatherString = prefs.getString("weather",null);
//
//         requestWeather(weatherId);
//        if(weatherString != null){
//            //有缓存是直接解析天气数据
//
//            Log.e("weatherString",weatherString);
//
//        }else{
//
//            Log.e("tringExtra(weather_id", ""+weatherId);
//            weatherLayout.setVisibility(View.INVISIBLE);
//            requestWeather(weatherId);
//            Weather weather = Utility.handleWeatherResponse(weatherString);
//            showWeatherInfo(weather);
//
//        }
    }
    private void init(){
        weatherLayout =(ScrollView)findViewById(R.id.weather_layout_01);
        titleCity =(TextView)findViewById(R.id.title_city);
        titleUpdateTime = (TextView)findViewById(R.id.title_update_time);
        degreeText = (TextView)findViewById(R.id.degree_text);
        weatherInfoText= (TextView)findViewById(R.id.weather_info_text);
        forecastLayout =(LinearLayout)findViewById(R.id.forecast_layout);
        aqiText = (TextView)findViewById(R.id.aqi_text);
        pm25Text = (TextView) findViewById(R.id.pm25_text);
        comfortText =(TextView) findViewById(R.id.comfort_text);
        carWashText = (TextView) findViewById(R.id.car_wash_text);
        sportText = (TextView) findViewById(R.id.sport_text);
        bingPicImg = (ImageView) findViewById(R.id.bing_pic_img);
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.swipe_parent);
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        navbutton = (Button) findViewById(R.id.nav_button);

    }
    /**
     * 设置导航
     */
    private void setNavHome(){
        navbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }



    /**
     * 设置下拉菜单的效果
     */

    private  void setSwipRefresh(){
        mWeatherId =  getIntent().getStringExtra("weather_id");
        swipeRefresh.setColorSchemeResources(
                android.R.color.holo_blue_light,
                android.R.color.holo_red_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_green_light
        );
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestWeather(mWeatherId);
            }
        });
    }

    /**
     * 获取图片
     */
    public  void getImage(){
//     getImage   SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
//        String bingPic = prefs.getString("bing_pic",null);
//        if(bingPic != null){
//
//        }
        loadBingPic();
    }


    /**
     * 根据天气ID求城市天气信息
     *
     */
    public void requestWeather(final  String weatherId){
       // http://guolin.tech/api/weather?cityid=CN101190407&key=10c5e10a8e574bd2ae1b591b8969fe22
        Log.e("======weatherId=====", ""+weatherId);
        String WeatherURL = "http://guolin.tech/api/weather?cityid="+weatherId +"&key=10c5e10a8e574bd2ae1b591b8969fe22";


        HttpUtil.sendOkHttpRequest(WeatherURL, new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        Toast.makeText(WeatherActivity.this,"获取天气失败", Toast.LENGTH_SHORT).show();
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                Log.e("myLog", ""+responseText);
                final Weather weather = Utility.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(weather != null && "ok".equals(weather.status)){
                            SharedPreferences.Editor editor = PreferenceManager
                                    .getDefaultSharedPreferences(WeatherActivity.this)
                                    .edit();
                            editor.putString("weather", responseText);
                            editor.apply();


                            showWeatherInfo(weather);

                        }else{
                            Toast.makeText(WeatherActivity.this,"天气没有获取成功！！", Toast.LENGTH_SHORT).show();

                        }
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        });
        loadBingPic();
    }


    /**
     * 每日一图
     */

    private void loadBingPic(){
        String requestBingPic = "http://guolin.tech/api/bing_pic";
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(WeatherActivity.this).edit();
                editor.putString("bing_pic", responseText);
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(WeatherActivity.this).load(responseText).into(bingPicImg);
                    }
                });
            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }
        });
    }
    /**
     * 处理并展示Weather
     */
    private void showWeatherInfo(Weather weather){
        String CityName = weather.basic.cityName;
        String updateTime = weather.basic.update.updateTime.split("")[1];
        String degree = weather.now.temperature + "°C";
        String weatherInfo = weather.now.more.info;
        titleCity.setText(CityName);
        titleUpdateTime.setText(updateTime);
        degreeText.setText(degree);
        weatherInfoText.setText(weatherInfo);
        forecastLayout.removeAllViews();
        for(Forecast forecast : weather.forecastList){

            View view = LayoutInflater.from(this).inflate(R.layout.forecast_ietm, forecastLayout, false);
            TextView dateText = (TextView) view.findViewById(R.id.data_text);
            TextView infoText = (TextView) view.findViewById(R.id.info_text);
            TextView maxText = (TextView) view.findViewById(R.id.max_text);
            TextView minText = (TextView) view.findViewById(R.id.min_text);
            dateText.setText(forecast.date);
            infoText.setText(forecast.more.info);
            maxText.setText(forecast.temperature.max);
            minText.setText(forecast.temperature.min);
            forecastLayout.addView(view);

        }
        if(weather.aqi != null){
            aqiText.setText(weather.aqi.city.aqi);
            pm25Text.setText(weather.aqi.city.pm25);
        }
        String comfort = "舒适度：" + weather.suggestion.comfort.info;
        String carWash = "洗车指数："+weather.suggestion.carWash.info;
        String sport = "运动建议："+ weather.suggestion.sport.info;
        comfortText.setText(carWash);
        carWashText.setText(carWash);
        sportText.setText(sport);
        weatherLayout.setVisibility(View.VISIBLE);

    }
}
