package com.hyphenate.fluter.impl;

import com.hyphenate.fluter.domain.UserInfor;

public interface ZszlLoginListener {
    void loginSuccess(UserInfor userInfor);

    void loginFail(String errorMsg);
}
