package com.linkage.mobile72.sh.data;

import org.json.JSONObject;

public class AppDetailBean
{
    
    private String appName;//应用名称
    
    private long appId;//应用id
    
    private long id;// 应用主键 
    
    private String appLogo;//应用图标。
    
    private String appUrl;//应用下载的url
    
    private String appDesc;//版本介绍
    
    private String appPrice_me;//应用价格 （用户当前价格）
    
    private String appPrice;//应用原价，如果不用户是会员显示”“ 
    
    private int appDownNum;//应用下载或是使用次数 
    
    private int appType;// 应用类型，1.app,2.html 
    
    private int appAuth;// 应用是否需要授权，0：不需要，1：需要 
    
    private String appIntroduce;//版本说明 
    
    private String appLauncherPath;// 启动路径 
    
    private String appLauncherUrl;
    
    private String openid;//应用和用户相关id(如果没授权，此字段返回空字符串)
    
    private String appImg;// 介绍图片，用逗号分隔 
    
    private String cpname;// 开发商 
    
    private String updateDate;// 更新时间（2015年06月25日） 
    
    private String version;// 版本号 
    
    private String fileSize;// 大小 
    
    private String compatibilit;// 兼容性 
    
    private int inapp;// 收费类型 1: 应用内   0: 应用外
    
    private String inapp_notice;
    
    public static AppDetailBean parseFromJson(JSONObject jsonObj) {
        AppDetailBean app = new AppDetailBean();
        
        app.setAppId(jsonObj.optLong("appId"));
        
        app.setId(jsonObj.optLong("id"));
        app.setAppName(jsonObj.optString("appName"));
        app.setAppLogo(jsonObj.optString("appLogo"));
        app.setAppUrl(jsonObj.optString("appUrl"));
        app.setAppDesc(jsonObj.optString("appDesc"));
        
        
        app.setAppPrice_me(jsonObj.optString("appPrice_me"));
        app.setAppPrice(jsonObj.optString("appPrice"));
        app.setAppDownNum(jsonObj.optInt("appDownNum"));
        app.setAppType(jsonObj.optInt("appType"));
        app.setAppAuth(jsonObj.optInt("appAuth"));
        
        app.setAppIntroduce(jsonObj.optString("appIntroduce"));
        app.setAppLauncherPath(jsonObj.optString("appLauncherPath"));
        app.setAppLauncherUrl(jsonObj.optString("appLauncherUrl"));
        app.setOpenid(jsonObj.optString("openid"));
        app.setAppImg(jsonObj.optString("appImg"));
        app.setCpname(jsonObj.optString("cpname"));
        
        app.setUpdateDate(jsonObj.optString("updateDate"));
        app.setVersion(jsonObj.optString("version"));
        app.setFileSize(jsonObj.optString("fileSize"));
        app.setCompatibilit(jsonObj.optString("compatibilit"));
        app.setInapp(jsonObj.optInt("inapp"));
        app.setInapp_notice(jsonObj.optString("inapp_notice"));
        
        return app;
    }


    public String getAppName()
    {
        return appName;
    }

    public void setAppName(String appName)
    {
        this.appName = appName;
    }

    public long getAppId()
    {
        return appId;
    }

    public void setAppId(long appId)
    {
        this.appId = appId;
    }

    public long getId()
    {
        return id;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public String getAppLogo()
    {
        return appLogo;
    }

    public void setAppLogo(String appLogo)
    {
        this.appLogo = appLogo;
    }

    public String getAppUrl()
    {
        return appUrl;
    }

    public void setAppUrl(String appUrl)
    {
        this.appUrl = appUrl;
    }

    public String getAppDesc()
    {
        return appDesc;
    }

    public void setAppDesc(String appDesc)
    {
        this.appDesc = appDesc;
    }

    public String getAppPrice_me()
    {
        return appPrice_me;
    }

    public void setAppPrice_me(String appPrice_me)
    {
        this.appPrice_me = appPrice_me;
    }

    public String getAppPrice()
    {
        return appPrice;
    }

    public void setAppPrice(String appPrice)
    {
        this.appPrice = appPrice;
    }

    public int getAppDownNum()
    {
        return appDownNum;
    }

    public void setAppDownNum(int appDownNum)
    {
        this.appDownNum = appDownNum;
    }

    public int getAppType()
    {
        return appType;
    }

    public void setAppType(int appType)
    {
        this.appType = appType;
    }

    public int getAppAuth()
    {
        return appAuth;
    }

    public void setAppAuth(int appAuth)
    {
        this.appAuth = appAuth;
    }

    public String getAppIntroduce()
    {
        return appIntroduce;
    }

    public void setAppIntroduce(String appIntroduce)
    {
        this.appIntroduce = appIntroduce;
    }

    public String getAppLauncherPath()
    {
        return appLauncherPath;
    }

    public void setAppLauncherPath(String appLauncherPath)
    {
        this.appLauncherPath = appLauncherPath;
    }

    public String getOpenid()
    {
        return openid;
    }

    public void setOpenid(String openid)
    {
        this.openid = openid;
    }

    public String getAppImg()
    {
        return appImg;
    }

    public void setAppImg(String appImg)
    {
        this.appImg = appImg;
    }

    public String getCpname()
    {
        return cpname;
    }

    public void setCpname(String cpname)
    {
        this.cpname = cpname;
    }

    public String getUpdateDate()
    {
        return updateDate;
    }

    public void setUpdateDate(String updateDate)
    {
        this.updateDate = updateDate;
    }

    public String getVersion()
    {
        return version;
    }

    public void setVersion(String version)
    {
        this.version = version;
    }

    public String getFileSize()
    {
        return fileSize;
    }

    public void setFileSize(String fileSize)
    {
        this.fileSize = fileSize;
    }

    public String getCompatibilit()
    {
        return compatibilit;
    }

    public void setCompatibilit(String compatibilit)
    {
        this.compatibilit = compatibilit;
    }



	public int getInapp() {
		return inapp;
	}


	public void setInapp(int inapp) {
		this.inapp = inapp;
	}


	public String getInapp_notice() {
		return inapp_notice;
	}


	public void setInapp_notice(String inapp_notice) {
		this.inapp_notice = inapp_notice;
	}


	public String getAppLauncherUrl() {
		return appLauncherUrl;
	}


	public void setAppLauncherUrl(String appLauncherUrl) {
		this.appLauncherUrl = appLauncherUrl;
	}
    
}
