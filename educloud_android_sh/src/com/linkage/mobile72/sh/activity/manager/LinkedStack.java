package com.linkage.mobile72.sh.activity.manager;

public interface LinkedStack<Activity>
{
    /**
     * 判断栈是否为空
     */
    boolean isEmpty();
    
    /**
     * 清空栈
     */
    void clear();
    
    /**
     * 栈的长度
     */
    int length();
    
    /**
     * 数据入栈
     */
    boolean push(Activity data);
    
    /**
     * 数据出栈
     */
    Activity pop();
}
