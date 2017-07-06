package com.external.proxy;

public class Result
{
    private int result;

    private String info;

    private String json;

    public Result(int result, String json)
    {
        this.result = result;
        this.json = json;
    }

    public int getResult()
    {
        return result;
    }

    public void setResult(int result)
    {
        this.result = result;
    }

    public String getJson()
    {
        return json;
    }

    public void setJson(String json)
    {
        this.json = json;
    }

    public String getInfo()
    {
        return info;
    }

    public void setInfo(String info)
    {
        this.info = info;
    }

}
