package com.linkage.app.utils;

import android.content.Context;
import android.os.RemoteException;
import android.widget.Toast;

import com.linkage.lib.util.LogUtils;
import com.morgoo.droidplugin.pm.PluginManager;
import com.morgoo.helper.compat.PackageManagerCompat;

public class APKUtils {
	/**
	 *
	 * @param context
	 * @param packageName
	 * @param apkfile
	 * @param upload_flag
	 */
	public  void  installAPK(Context context, String apkfile,String packageName,boolean upload_flag) {
		if (!PluginManager.getInstance().isConnected()) {
			Toast.makeText(context, "插件服务正在初始化，请稍后再试。。。", Toast.LENGTH_SHORT).show();
			return;
		}

		try {
		if (!upload_flag && (PluginManager.getInstance().getPackageInfo(packageName, 0) != null)) {
			Toast.makeText(context, "已经安装了，不能再安装", Toast.LENGTH_SHORT).show();
			return;
		}
			int ret = 0;
 			if(upload_flag)
			{
				ret = PluginManager.getInstance().installPackage(apkfile, 0);
			}else {
				ret = PluginManager.getInstance().installPackage(apkfile, PackageManagerCompat.INSTALL_REPLACE_EXISTING);
			}


				Toast.makeText(context, ret == PluginManager.INSTALL_FAILED_NO_REQUESTEDPERMISSION ? "安装失败，文件请求的权限太多" : "安装完成", Toast.LENGTH_SHORT).show();
			LogUtils.e("==================, ret");
		} catch (RemoteException e) {
			e.printStackTrace();
		}catch (Exception ex){
			ex.printStackTrace();
		}
	}


}