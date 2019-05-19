package com.huayun.mall.error;

import com.huayun.mall.response.CommonReturnType;

public interface CommonError {
    public int getErrCode();
    public String getErrMsg();
    public CommonError setErrmsg(String errmsg);

}
