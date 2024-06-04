package com.ruoyi.dts.bean;

import java.io.Serializable;
import java.math.BigDecimal;

public class CategorySellAmts implements Serializable{

	private static final long serialVersionUID = 677901688504280013L;

	private String name;
	
	private BigDecimal value;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public BigDecimal getValue() {
		return value;
	}

	public void setValue(BigDecimal value) {
		this.value = value;
	}
}
