package www.v_world.com.v_world_weather02;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import www.v_world.com.v_world_weather02.db.City;
import www.v_world.com.v_world_weather02.db.County;
import www.v_world.com.v_world_weather02.db.Province;
import www.v_world.com.v_world_weather02.uitl.HttpUtil;
import www.v_world.com.v_world_weather02.uitl.Utility;

/**
 * Created by Administrator on 2017/6/2.
 */
public class AreaFragmentActivity extends Fragment {
    public static final int LEVEL_PROVINCE = 0;
    public static final int LEVEL_CITY = 1;
    public static final int LEVEL_COUNTY = 2;


    private ProgressDialog progressDialog; //进度条
    private TextView titleText;
    private Button backButton;

    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> dataList = new ArrayList<>();

    /**
     * 省列表
     */
    private List<Province> provinceList;
    /**
     * 市列表
     */
    private List<City> cityList;
    /**
     * 县列表
     */
    private List<County> countyList;
    /**
     * 选中的省份
     */
    private Province selectedProvince;
    /**
     * 选中市级
     */
    private City selectedCity;
    /**
     * 选中县级
     */
    private County selectedCounty;
    /**
     * 选中的级别
     */
    private int currentLevel;

    /**
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_area, container, false);//加载一个布局文件；
        titleText = (TextView) view.findViewById(R.id.title_text);
        backButton = (Button) view.findViewById(R.id.back_button);
        listView = (ListView) view.findViewById(R.id.list_view);
        adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (currentLevel == LEVEL_PROVINCE) {
                    selectedProvince = provinceList.get(position);
                    queryCities();
                } else if (currentLevel == LEVEL_CITY) {
                    selectedCity = cityList.get(position);

                    queryCounties();
                    Log.e("----currentLevel----", ""+currentLevel);
                }else if(currentLevel == LEVEL_COUNTY){
                    String wetherId = countyList.get(position).getWeatherId();
                   Log.e("-------wetherId-------", ""+wetherId);
                    Intent intent = new Intent(getActivity(),WeatherActivity.class);
                    intent.putExtra("weather_id",wetherId);
                    startActivity(intent);
                    getActivity().finish();


                }
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentLevel == LEVEL_COUNTY) {
                    queryCities();
                } else if (currentLevel == LEVEL_CITY) {
                    queryProvince();
                }
            }
        });
        queryProvince();
    }

    /**
     * 查询省份
     */
    public void queryProvince() {
        titleText.setText("中国");
        backButton.setVisibility(View.GONE);//View.GONE---->不可见，但这个View在ViewGroup中不保留位置，
        // 会重新layout，不再占用空间，那后面的view就会取代他的位置，
        provinceList = DataSupport.findAll(Province.class);

        if (provinceList.size() > 0) {
            dataList.clear();//清除之前的数剧缓存，防止数据溢出
            for (Province province : provinceList) {
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();//方法通过一个外部的方法控制如果适配器的内容改变时需要强制调用getView来刷新每个Item的内容。
            listView.setSelection(0);
            currentLevel = LEVEL_PROVINCE;
        } else {
            String address = "http://guolin.tech/api/china";
            queryFromServer(address, "province");
        }
    }
    /**
     * 查询市级
     */
    public void queryCities(){
        titleText.setText(selectedProvince.getProvinceName());


        backButton.setVisibility(View.VISIBLE);

        cityList = DataSupport.where("provinceId = ?", String.valueOf(selectedProvince.getId())).find(City.class);

        Log.e("----cityList---",""+cityList);

        if(cityList.size() > 0){
            dataList.clear();
            for(City city : cityList){
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_CITY;
            Log.e("--City---",""+currentLevel);

        }else {
            int provinceCode = selectedProvince.getProvinceCode();


            String address = "http://guolin.tech/api/china/" + provinceCode;

            queryFromServer(address, "city");
        }
    }
    /**
     * 查询县级
     */

    public void queryCounties(){
        titleText.setText(selectedCity.getCityName());
        backButton.setVisibility(View.VISIBLE);
        countyList = DataSupport.where("cityId = ?", String.valueOf(selectedCity.getId())).find(County.class);
        Log.e("countyList",""+countyList);

        if(countyList.size() > 0) {
            dataList.clear();
            for (County county : countyList) {
                dataList.add(county.getCountyName());
            }

            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_COUNTY;
            Log.e("---currevel----", ""+currentLevel);
        }else{
                int provinceCode = selectedProvince.getProvinceCode();
                int cityCode = selectedCity.getCityCode();

                String address = "http://guolin.tech/api/china/" + provinceCode + "/"  + cityCode;
                queryFromServer(address, "county");
            }

    }
    /**
     * 根据地址写从服务器上取数据
     */
    private void queryFromServer(String address, final String type){
        showProgressDialog();
        HttpUtil.sendOkHttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();//关闭进度条
                        Toast.makeText(getActivity(), "加载失败", Toast.LENGTH_SHORT).show();

                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String responseText = response.body().string();
                boolean result = false;
                if("province".equals(type)){
                    result = Utility.handleProvinceResponse(responseText);
                }else if("city".equals(type)){
                    result = Utility.handleCityResponse(responseText, selectedProvince.getId());
                }else if("county".equals(type)){
                    result = Utility.handleCountyResponse(responseText, selectedCity.getId());
                }
                if(result){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();//关闭进度条
                            if("province".equals(type)){
                                queryProvince();
                            }else if("city".equals(type)){
                                queryCities();
                            }else if("county".equals(type)){
                                queryCounties();
                            }
                        }
                    });
                }
            }
        });
    }
    /**
     * 显示进度条
     *
     */
    private  void showProgressDialog(){
        if(progressDialog == null){
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载。。。。。。");
            progressDialog.setCanceledOnTouchOutside(false);
        }
    }
    /**
     * 关闭进度条
     */
    private void  closeProgressDialog(){

        if(progressDialog != null){
            progressDialog.dismiss();
        }

    }
}





