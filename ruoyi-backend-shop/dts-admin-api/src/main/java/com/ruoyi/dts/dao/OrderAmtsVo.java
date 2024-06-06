package com.ruoyi.dts.dao;

import java.io.Serializable;
import java.math.BigDecimal;

public class OrderAmtsVo implements Serializable {

	private static final long serialVersionUID = 3840196229938738818L;

	private String[] dayData;//日期数据
	
	private BigDecimal[] orderAmtData;//日订单金额

	private int[] orderCntData;//日订单量

	public String[] getDayData() {
		return dayData;
	}

	public void setDayData(String[] dayData) {
		this.dayData = dayData;
	}

	public BigDecimal[] getOrderAmtData() {
		return orderAmtData;
	}

	public void setOrderAmtData(BigDecimal[] orderAmtData) {
		this.orderAmtData = orderAmtData;
	}

	public int[] getOrderCntData() {
		return orderCntData;
	}

	public void setOrderCntData(int[] orderCntData) {
		this.orderCntData = orderCntData;
	}
	
}
