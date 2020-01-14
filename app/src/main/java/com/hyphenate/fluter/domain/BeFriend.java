package com.hyphenate.fluter.domain;

import java.io.Serializable;
import java.util.List;

public class BeFriend implements Serializable {


    /**
     * code : 1
     * msg : 成功
     * data : [{"friend":"18310489432","avatar":"http://imagetest.chinasosmart.com//default_headimg.jpg","name":"18310489432"}]
     */

    private int code;
    private String msg;
    private List<DataBean> data;

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

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean implements Serializable{
        /**
         * friend : 18310489432
         * avatar : http://imagetest.chinasosmart.com//default_headimg.jpg
         * name : 18310489432
         */

        private String friend;
        private String avatar;
        private String name;

        public String getFriend() {
            return friend;
        }

        public void setFriend(String friend) {
            this.friend = friend;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
