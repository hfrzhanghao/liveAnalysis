package com.result;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

import com.dto.DTO;


@XmlRootElement(name = "page")
@JsonSerialize(include = Inclusion.NON_NULL)
public class PagedItemsResult<T extends DTO> extends ItemsResult<T>
{

    protected int pageNo;

    protected int pageSize;

    @XmlElement(name = "totalCount")
    @JsonProperty("totalCount")
    protected long total;

    protected long totalPage;

    public int getPageNo()
    {
        return pageNo;
    }

    public void setPageNo(int pageNo)
    {
        this.pageNo = pageNo;
    }

    public int getPageSize()
    {
        return pageSize;
    }

    public void setPageSize(int pageSize)
    {
        this.pageSize = pageSize;
    }

    public long getTotal()
    {
        return total;
    }

    public void setTotal(long total)
    {
        this.total = total;
    }

    public long getTotalPage()
    {
        return totalPage;
    }

    public void setTotalPage(long totalPage)
    {
        this.totalPage = totalPage;
    }

}