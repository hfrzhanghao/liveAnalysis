package com.result;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlSeeAlso;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

import com.dto.AllLiveDto;
import com.dto.StatRowDto;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "root")
@XmlSeeAlso(
{ PagedItemsResult.class,StatRowDto.class,AllLiveDto.class})
public class Result<T>
{

    @XmlElement
    private int result = 0;

    @XmlElement
    @JsonSerialize(include = Inclusion.NON_NULL)
    @JsonProperty(value = "ret_info")
    private String info;

    public int getResult()
    {
        return result;
    }

    public void setResult(int result)
    {
        this.result = result;
    }

    public String getInfo()
    {
        return info;
    }

    public void setInfo(String info)
    {
        this.info = info;
    }

    public String toString()
    {
        return "[result:" + result + ",info:" + info + ".]";
    }

}