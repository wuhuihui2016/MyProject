//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.fengyang.process;

import com.android.volley.Request.Priority;

import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

public final class RequestParams {
        private String charset = "UTF-8";
        private List<NameValuePair> queryStringParams;
        private HttpEntity bodyEntity;
        private Priority priority;

        public RequestParams() {
        }

        public void addParameter(String name, String value) {
                if(this.queryStringParams == null) {
                        this.queryStringParams = new ArrayList();
                }

                this.queryStringParams.add(new BasicNameValuePair(name, value));
        }

        public List<NameValuePair> getParams() {
                return this.queryStringParams;
        }
}
