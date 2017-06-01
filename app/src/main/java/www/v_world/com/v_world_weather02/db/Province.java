package www.v_world.com.v_world_weather02.db;

import org.litepal.crud.DataSupport;

/**
 * Created by Administrator on 2017/6/1.
 */
public class Province extends DataSupport{
    private int id;
    private String ProvinceName;
    private int ProvinceCode;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProvinceName() {
        return ProvinceName;
    }

    public void setProvinceName(String provinceName) {
        ProvinceName = provinceName;
    }

    public int getProvinceCode() {
        return ProvinceCode;
    }

    public void setProvinceCode(int provinceCode) {
        ProvinceCode = provinceCode;
    }
}
