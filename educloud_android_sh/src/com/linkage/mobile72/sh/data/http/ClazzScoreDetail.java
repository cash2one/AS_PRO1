package com.linkage.mobile72.sh.data.http;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

public class ClazzScoreDetail implements Serializable {

	private static final long serialVersionUID = 1L;

	private String name;
	private String result;

	public static ClazzScoreDetail parseFromJson(JSONObject jsonObj) {
		ClazzScoreDetail clazzResult = new ClazzScoreDetail();
		clazzResult.setName(jsonObj.optString("name"));
		clazzResult.setResult(jsonObj.optString("result"));
		return clazzResult;
	}

	public static List<ClazzScoreDetail> parseFromJson(JSONArray jsonArray) {
		List<ClazzScoreDetail> clazzResults = new ArrayList<ClazzScoreDetail>();
//		ClazzScoreDetail clazzResult1 = new ClazzScoreDetail();
//		clazzResult1.setName("姓名");
//		clazzResult1.setResult("分数");
//		clazzResults.add(clazzResult1);
		if (jsonArray != null && jsonArray.length() > 0) {
			for (int i = 0; i < jsonArray.length(); i++) {
				ClazzScoreDetail clazzResult = parseFromJson(jsonArray
						.optJSONObject(i));
				if (clazzResult != null)
					clazzResults.add(clazzResult);
			}
		}
		return clazzResults;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getResult() {
		return result;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public static class ScoreDescComparator implements Comparator<ClazzScoreDetail>{
		@Override
		public int compare(ClazzScoreDetail lhs, ClazzScoreDetail rhs) {

			return compares(new BigDecimal(Double.parseDouble(lhs.getResult())), new BigDecimal(Double.parseDouble(rhs.getResult())));
		}
	}
	
	public static class ScoreAcsComparator implements Comparator<ClazzScoreDetail>{
		@Override
		public int compare(ClazzScoreDetail lhs, ClazzScoreDetail rhs) {

			return compares(new BigDecimal(Double.parseDouble(rhs.getResult())), new BigDecimal(Double.parseDouble(lhs.getResult())));
		}
	}

	public static int compares(BigDecimal val1, BigDecimal val2) {  
	    int result = 0;  
	    if (val1.compareTo(val2) < 0) {
	        result = -1;  
	    }  
	    if (val1.compareTo(val2) == 0) {  
	        result = 0;  
	    }  
	    if (val1.compareTo(val2) > 0) {  
	        result = 1; 
	    }  
	    return result;  
	}
}
