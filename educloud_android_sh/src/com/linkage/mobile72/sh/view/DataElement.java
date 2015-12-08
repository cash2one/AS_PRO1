package com.linkage.mobile72.sh.view;

public class DataElement {  
	
	private int color;  
    private int value; 
    
    public DataElement(int value, int color) {  
        this.value = value;  
        this.color = color;  
    }  
    public int getValue() {  
        return value;  
    }  
    public void setValue(int value) {  
        this.value = value;  
    }  
      
    public void setColor(int color) {  
        this.color = color;  
    }  
      
    public int getColor() {  
        return this.color;  
    }  
}  
