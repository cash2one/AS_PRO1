package com.linkage.mobile72.sh.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Score implements Serializable
{
    /**
     * 
     */
    private static final long serialVersionUID = -9145526520536881337L;
    
    private String id;
    
    private String date;
    
    private String name;
    
    private String score;
    
    private int rank;
    
    private int rankup;
    
    private int persent;
    
    private String everage;
    
    private String highest;
    
    private int total;
    
    public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	/**
     * @return the date
     */
    public String getDate()
    {
        return date;
    }
    
    /**
     * @param date the date to set
     */
    public void setDate(String date)
    {
        this.date = date;
    }
    
    /**
     * @return the name
     */
    public String getName()
    {
        return name;
    }
    
    /**
     * @param name the name to set
     */
    public void setName(String name)
    {
        this.name = name;
    }
    
    /**
     * @return the score
     */
    public String getScore()
    {
        return score;
    }
    
    /**
     * @param score the score to set
     */
    public void setScore(String score)
    {
        this.score = score;
    }
    
    /**
     * @return the rank
     */
    public int getRank()
    {
        return rank;
    }
    
    /**
     * @param rank the rank to set
     */
    public void setRank(int rank)
    {
        this.rank = rank;
    }
    
    /**
     * @return the rankup
     */
    public int getRankup()
    {
        return rankup;
    }
    
    /**
     * @param rankup the rankup to set
     */
    public void setRankup(int rankup)
    {
        this.rankup = rankup;
    }
    
    /**
     * @return the persent
     */
    public int getPersent()
    {
        return persent;
    }
    
    /**
     * @param persent the persent to set
     */
    public void setPersent(int persent)
    {
        this.persent = persent;
    }
    
    /**
     * @return the everage
     */
    public String getEverage()
    {
        return everage;
    }
    
    /**
     * @param everage the everage to set
     */
    public void setEverage(String everage)
    {
        this.everage = everage;
    }
    
    /**
     * @return the highest
     */
    public String getHighest()
    {
        return highest;
    }
    
    /**
     * @param highest the highest to set
     */
    public void setHighest(String highest)
    {
        this.highest = highest;
    }
    
    /**
     * @return the total
     */
    public int getTotal()
    {
        return total;
    }
    
    /**
     * @param total the total to set
     */
    public void setTotal(int total)
    {
        this.total = total;
    }
    
    public static Score parseJson(JSONObject jsonObj) throws JSONException
    {
        Score score = new Score();
        score.setId(jsonObj.optString("id"));
        score.setDate(jsonObj.optString("date"));
        score.setName(jsonObj.optString("name"));
        score.setScore(jsonObj.optString("score"));
        score.setRank(jsonObj.optInt("rank"));
        score.setRankup(jsonObj.optInt("rankup"));
        score.setPersent(jsonObj.optInt("persent"));
        score.setEverage(jsonObj.optString("everage"));
        score.setHighest(jsonObj.optString("highest"));
        score.setTotal(jsonObj.optInt("total"));
        
        return score;
    }
    
    public static List<Score> parseFromJson(JSONArray jsonArray)
            throws JSONException
    {
        List<Score> scList = new ArrayList<Score>();
        if (jsonArray != null && jsonArray.length() > 0)
        {
            for (int i = 0; i < jsonArray.length(); i++)
            {
                Score clazz = parseJson(jsonArray.optJSONObject(i));
                if (clazz != null)
                    scList.add(clazz);
            }
        }
        return scList;
    }
    
}
