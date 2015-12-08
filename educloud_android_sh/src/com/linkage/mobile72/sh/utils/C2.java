package com.linkage.mobile72.sh.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.linkage.mobile72.sh.http.ParamItem;
import com.linkage.lib.util.LogUtils;


public class C2 {

    private static final String sigkey = "acsiof0(D";

	public static String getSig(List<ParamItem> params) {
        if(params != null && params.size() > 0) {
            Map<String,String> map = new HashMap<String, String>();
            for (int i = 0; i < params.size(); i++) {
                ParamItem pi = params.get(i);
                if(pi.getType() == ParamItem.TYPE_TEXT) {
                    LogUtils.e(pi.getKey() + ":" + pi.getValue());
                    map.put(pi.getKey(), pi.getValue());
                }
            }
            return getSig(map);
        }
        return null;
    }

	public static String getSig(Map<String, String> signatureParams) {
		String paramData = generateSignature(generateAuthSignature(signatureParams));
		return paramData;
	}
	
	private static String generateSignature(String data) {
		MessageDigest md = null;
		String outStr = null;
		String str = "";
		String tempStr = "";
		try {
			md = MessageDigest.getInstance("MD5");
		}
		catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		byte[] digest = md.digest(data.getBytes());
		for (int i = 0 ; i < digest.length ; i++) {
			tempStr = (Integer.toHexString(digest[i] & 0xff));
			if (tempStr.length() == 1) {
				str = str + "0" + tempStr;
			} else {
				str = str + tempStr;
			}
		}
		outStr = str.toLowerCase();
		return outStr != null ? outStr.toUpperCase() : outStr;
	}

	private static String generateAuthSignature(Map<String, String> signatureParams) {
        StringBuffer sb = new StringBuffer().append(getSortedString(signatureParams));
        String s = "";
        try {
            s = java.net.URLEncoder.encode(sb.toString(), "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        System.out.println(s);
        //LogUtils.e("before sig---", s);
        return s;
	}

	private static String getSortedString(Map<String, String> map) {
		List<String> list = new ArrayList<String>();
		Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, String> entry = it.next();
			list.add(entry.getKey() + "=" + entry.getValue());
		}

		Collections.sort(list);

		StringBuffer sb = new StringBuffer();
		for (String item : list) {
			sb.append(item);
		}

//		sb.append(PropertiesUtil.getString("CLIENT_SECRET"));
		sb.append(sigkey);
		return sb.toString();
	}
	
	/*public static void main(String[] args) {
        //commandtype=login&origin=1&extend=,444,777&account=13333333333&password=123456q

        //88D144BD56F5279444C9AC63FEDA534E

        Map<String,String> map = new HashMap<String, String>();
        //map.put("accessToken","26bc4a5a51e6228fe769cd996667b994");
        map.put("account", "18012966411");
        map.put("commandtype", "login");
        map.put("extend", "9TGQ6dvZ1mgQ0HSu4yLodyOKPtI4jPer");
        map.put("origin", "p01i01v11");
        map.put("password", "111111");
        map.put("term_manufacturer", "ios");

        System.out.print(getSig(map));

	}*/
	public static void main(String[] args) {
		String s = "commandtype%3DsendMessageAttachmentextend%3DeF2iNjSVdqYn3FU6piO4925OqGO03tFO3zbobOT7fHQL8G%2FQQ8KqEnw5dZtb+lFvnHHjoPbFddgIN5gZKQy0uqw%3D%3Dorigin%3DaaF2f65547aE8cA0cEf4Ee";
		String ss = generateSignature(s);
		System.out.println(ss);
	}
}
