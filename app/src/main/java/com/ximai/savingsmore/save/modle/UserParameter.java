package com.ximai.savingsmore.save.modle;

import java.util.List;

/**
 * Created by caojian on 16/12/9.
 */
public class UserParameter {
    public  String UserDisplayName;
    public  String NickName;
    public  String PhotoId;
    public  String PhotoPath;
    public String Sex;
    public  String Birthday;
    public String BirthPlace;
    public String Domicile;
    public String QQ;
    public String Weibo;
    public String WeChat;
    public String Post;
    public String ProvinceId;
    public String CityId;
    public String AreaId;
    public MyUserExtInfo  UserExtInfo=new MyUserExtInfo();
    public List<BusinessScopes> BusinessScopes;

}
