package com.ruoyi.dts.util;

import com.ruoyi.dts.db.domain.DtsBrand;

import java.io.Serializable;

public class DtsBrandVo extends DtsBrand implements Serializable{

	private static final long serialVersionUID = 6530090986580196500L;
	
	private Integer[] categoryIds;

	public Integer[] getCategoryIds() {
		return categoryIds;
	}

	public void setCategoryIds(Integer[] categoryIds) {
		this.categoryIds = categoryIds;
	}
	
	

}
