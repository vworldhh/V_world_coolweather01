package www.v_world.com.v_world_weather02.uitl;

import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by Administrator on 2017/6/1.
 */
public class HttpUtil {

    public static void sendOkHttpRequest(String address, okhttp3.Callback callback){

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(address).build();
        client.newCall(request).enqueue(callback);

    }
}
