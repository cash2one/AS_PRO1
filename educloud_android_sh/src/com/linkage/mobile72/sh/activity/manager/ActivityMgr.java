package com.linkage.mobile72.sh.activity.manager;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;

public class ActivityMgr implements LinkedStack<Activity>
{
    private static ActivityMgr instance;
    
    /**
     * 栈顶指针
     */
    private Node top;
    
    private List<Node> activites = new ArrayList<Node>();
    
    public static ActivityMgr getInstance()
    {
        if (null == instance)
        {
            instance = new ActivityMgr();
        }
        return instance;
    }
    
    private ActivityMgr()
    {
        top = null;
    }
    
    @Override
    public boolean isEmpty()
    {
        return activites.isEmpty();
    }
    
    @Override
    public void clear()
    {
        Activity p = pop();
        while (null != p)
        {
            System.out.println("clear act=" + p);
            p.finish();
            
            p = pop();
        }
    }
    
    @Override
    public int length()
    {
        return activites.size();
    }
    
    @Override
    public boolean push(Activity data)
    {
        Node node = new Node();
        node.data = data;
        node.pre = top;
        node.next = null;
        if (top != null)
        {
            top.next = node;
        }
        
        activites.add(node);
        // 改变栈顶指针
        top = node;
        return true;
    }
    
    @Override
    public Activity pop()
    {
        Node node;
        if (top != null)
        {
            node = top;
            // 改变栈顶指针
            top = node.pre;
            if (null != top)
            {
                top.next = null;
            }
            else
            {
                System.out.println("stack is empty.");
            }
            
            activites.remove(node);
            
            return node.data;
        }
        return null;
    }
    
    public void removeActivity(Activity act)
    {
        if (null != act && top != null)
        {
            Node node = top;
            if (act.equals(top.data))
            {
                top = top.pre;
                if (top != null) {
                	top.next = null;
                	activites.remove(node);
				}
                //                node.data.finish();
                
                System.out.println("remove top, update top...");
            }
            else
            {
                while (node != null)
                {
                    if (node.data.equals(act))
                    {
                        if (null != node.pre)
                        {
                            node.pre.next = node.next;
                        }
                        else
                        {
                            System.out.println("pre is null, no need to update");
                        }
                        
                        if (null != node.next)
                        {
                            node.next.pre = node.pre;
                        }
                        else
                        {
                            System.out.println("next is null, no need to update");
                        }
                        
                        activites.remove(node);
                        //                        node.data.finish();
                    }
                    node = node.pre;
                }
            }
        }
        else
        {
            System.out.println("remove act=" + act + " top=" + top);
        }
    }
    
    void printStack()
    {
        if (activites != null)
        {
            if (activites.size() == 0)
            {
                System.out.println("stack is 0, can not print.");
                return;
            }
            for (Node node : activites)
            {
                System.out.println("activity=" + node.data);
            }
        }
        else
        {
            System.out.println("stack is empty, can not print.");
        }
        
    }
    
    /**
     * 将数据封装成结点
     */
    private final class Node
    {
        private Node pre, next;
        
        private Activity data;
    }
}
