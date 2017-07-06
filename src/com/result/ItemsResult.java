package com.result;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

import com.dto.AllLiveDto;
import com.dto.StatRowDto;
import com.dto.DTO;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "itemsResult")
@JsonSerialize(include = Inclusion.NON_EMPTY)
public class ItemsResult<T extends DTO> extends Result<Object>
{

    public ItemsResult()
    {

    }

	@JsonSerialize(include = Inclusion.NON_NULL)
	private T data; 
    
    private Object object;
    
    @JsonSerialize(include = Inclusion.NON_EMPTY)
    @XmlElements(value =
    {@XmlElement(type = StatRowDto.class),@XmlElement(type = AllLiveDto.class)})
    private List<T> items=new ArrayList<T>();

    public List<T> getItems()
    {
        return items;
    }

    public void setItems(List<T> items)
    {
      if(items != null)
        this.items = items;
    }

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public Object getObject() {
		return object;
	}

	public void setObject(Object object) {
		this.object = object;
	}

	
	
	
	
    
}