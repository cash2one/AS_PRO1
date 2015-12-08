package com.linkage.mobile72.sh.view;

import java.util.HashMap;
import java.util.List;

public class DataSeries {
	
	private String[] keyStrings;
	private HashMap<String, DataElement> map;

	public DataSeries() {
		map = new HashMap<String, DataElement>();
	}

	public void addSeries(String[] keys, List<DataElement> dataElements) {
		if (keys == null || dataElements== null || keys.length != dataElements.size()) throw new IllegalArgumentException();
		this.keyStrings = keys;
		for (int i = 0; i < keys.length; i++) {
			map.put(keys[i], dataElements.get(i));
		}
	}

	/**
	 * 
	 * @return
	 */
	public DataElement getItems(String key) {
		return map.get(key);
	}

	/**
	 * 
	 * @return
	 */
	public int getSeriesCount() {
		return keyStrings.length;
	}

	/**
	 * 
	 * @return
	 */
	public String[] getSeriesKeys() {
		return keyStrings;
	}

}
