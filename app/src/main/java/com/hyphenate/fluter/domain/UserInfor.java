package com.hyphenate.fluter.domain;

public class UserInfor {


    /**
     * code : 1
     * msg : 成功
     * data : {"id":"138","property_id":"40","username":"guanjia01","real_name":"","is_primary":"2","huanxin_user_id":"579","avatar":"http://imagetest.chinasosmart.com//default_headimg.jpg","Accesstoken":"eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiIsImp0aSI6IjE1NTkxMTUzNDUxODYifQ.eyJpc3MiOiJ6aG9uZ3NvdSIsImF1ZCI6ImFwcCIsImp0aSI6IjE1NTkxMTUzNDUxODYiLCJpYXQiOjE1NTkxMTUzNDUsImV4cCI6MTU2NDI5OTM0NSwidXNlckluZm8iOnsidXNlcklkIjoiMTM4In19.GzZutIQXK8Qqkenp0B1GPxmu-1i3o475L7C8k7xltUQ"}
     */

    private int code;
    private String msg;
    private DataBean data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * id : 138
         * property_id : 40
         * username : guanjia01
         * real_name :
         * is_primary : 2
         * huanxin_user_id : 579
         * avatar : http://imagetest.chinasosmart.com//default_headimg.jpg
         * Accesstoken : eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiIsImp0aSI6IjE1NTkxMTUzNDUxODYifQ.eyJpc3MiOiJ6aG9uZ3NvdSIsImF1ZCI6ImFwcCIsImp0aSI6IjE1NTkxMTUzNDUxODYiLCJpYXQiOjE1NTkxMTUzNDUsImV4cCI6MTU2NDI5OTM0NSwidXNlckluZm8iOnsidXNlcklkIjoiMTM4In19.GzZutIQXK8Qqkenp0B1GPxmu-1i3o475L7C8k7xltUQ
         */

        private String id;
        private String property_id;
        private String username;
        private String real_name;
        private String is_primary;
        private String huanxin_user_id;
        private String avatar;
        private String Accesstoken;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getProperty_id() {
            return property_id;
        }

        public void setProperty_id(String property_id) {
            this.property_id = property_id;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getReal_name() {
            return real_name;
        }

        public void setReal_name(String real_name) {
            this.real_name = real_name;
        }

        public String getIs_primary() {
            return is_primary;
        }

        public void setIs_primary(String is_primary) {
            this.is_primary = is_primary;
        }

        public String getHuanxin_user_id() {
            return huanxin_user_id;
        }

        public void setHuanxin_user_id(String huanxin_user_id) {
            this.huanxin_user_id = huanxin_user_id;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String getAccesstoken() {
            return Accesstoken;
        }

        public void setAccesstoken(String Accesstoken) {
            this.Accesstoken = Accesstoken;
        }
    }
}
