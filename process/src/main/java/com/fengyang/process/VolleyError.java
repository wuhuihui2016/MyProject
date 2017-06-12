package com.fengyang.process;

import android.content.Context;
import android.widget.Toast;

import com.fengyang.callback.ICallBack;
import com.fengyang.callback.IVolleyErro;

/**
 * Created by FengYang on 2016/11/18.
 */

public class VolleyError implements IVolleyErro{
    static VolleyError iVolleyError;
    Context context;
    private VolleyError(Context context){
        this.context = context;
    }

    /**
     * 单例
     * @return
     */
    public static VolleyError getInstance(Context context){
        synchronized (VolleyError.class){
            if(iVolleyError == null){
                iVolleyError = new VolleyError(context);
            }
            return iVolleyError;
        }
    }
    @Override
    public void onErrorResponse(final Context context, final byte request, com.android.volley.VolleyError error,final String url, final RequestParams params, final ICallBack iCallBack) {
        //刷新token
        Toast.makeText(context, "获取失败", Toast.LENGTH_SHORT).show();

    }

}
