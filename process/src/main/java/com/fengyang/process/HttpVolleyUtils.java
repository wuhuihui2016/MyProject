package com.fengyang.process;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.fengyang.callback.ICallBack;
import com.fengyang.callback.IHttpMethod;

import org.apache.http.NameValuePair;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by FengYang on 2016/11/18.
 */

public class HttpVolleyUtils implements IHttpMethod{
    /**
     * httpGet请求
     * @param url
     * @param params
     * @param icallBack
     */
    @Override
    public void sendGETRequest(final Context context,final String url,final RequestParams params, final ICallBack icallBack) {
        if(!isNetworkAvailable(context)){
            return;
        };
        final Uri.Builder builder = Uri.parse(url).buildUpon();
        if(params != null){
            List<NameValuePair>  list = params.getParams();
            if(list != null){
                for (int i = 0 ;i < list.size();i++){
                    NameValuePair np = list.get(i);
                    builder.appendQueryParameter(np.getName(), np.getValue());
                }
            }
        }
        StringRequest stringRequest = new StringRequest(Request.Method.GET,builder.toString(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    String result = new String((response.toString()).getBytes("ISO-8859-1"),"utf-8");
                    Log.i(builder.toString() + "--response",result);
                    icallBack.onSuccess(new JSONObject(result));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("volley", volleyError.getMessage(), volleyError);
                com.fengyang.process.VolleyError.getInstance(context).
                        onErrorResponse(context,IHttpMethod.REQUEST_GET,volleyError,url,params,icallBack);
            }
        }) {
            //GET和Post参数放在此参数中
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                final Map<String, String> map = new HashMap<String, String>();
                //   map.put("parent_id", "");
                // map.put("password", Utils.SHA1(passWord));
                return null;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(Constant.TIME_OUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestManager.addRequest(stringRequest,null);
    }

    @Override
    public void sendPostRequest(final Context context,final String url,final RequestParams params,final ICallBack icallBack) {
        if(!isNetworkAvailable(context)){
            return;
        };
        final Uri.Builder builder = Uri.parse(url).buildUpon();
        StringRequest stringRequest = new StringRequest(Request.Method.POST,builder.toString(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    String result = new String((response.toString()).getBytes("ISO-8859-1"),"utf-8");
                    Log.i(builder.toString() + "--response",result);
                    icallBack.onSuccess(new JSONObject(result));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                Log.e("volley", volleyError.getMessage(), volleyError);
                com.fengyang.process.VolleyError.getInstance(context).
                        onErrorResponse(context,IHttpMethod.REQUEST_POST,volleyError,url,params,icallBack);
            }
        }) {
            //GET和Post参数放在此参数中
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                final Map<String, String> map = new HashMap<String, String>();
                map.clear();
                if(params != null){
                    List<NameValuePair>  list = params.getParams();
                    if(list != null){
                        for (int i = 0 ;i < list.size();i++){
                            NameValuePair np = list.get(i);
                            map.put(np.getName(), np.getValue());
                        }
                    }
                }
                Log.i("url :",builder.toString());
                Log.i("params :",map.toString());
                return map;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(Constant.TIME_OUT, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        RequestManager.addRequest(stringRequest,null);
    }

    /**
     * 判断是否有网络连
     */
    private boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            Toast.makeText(context, "无网络，请检查网络设置",Toast.LENGTH_SHORT).show();
            return false;
        } else {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        Toast.makeText(context, "无网络，请检查网络设置",Toast.LENGTH_SHORT).show();
        return false;
    }

}
