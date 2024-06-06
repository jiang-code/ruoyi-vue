package com.ruoyi.dts.dao;

import java.io.Serializable;

public class UserOrderCntVo implements Serializable {

	private static final long serialVersionUID = -5460904409450124808L;
	
	private String[] dayData;//日期数据
	
	private int[] userCnt;//每日用户新增量

	private int[] orderCnt;//每日订单量

	public String[] getDayData() {
		return dayData;
	}

	public void setDayData(String[] dayData) {
		this.dayData = dayData;
	}

	public int[] getUserCnt() {
		return userCnt;
	}

	public void setUserCnt(int[] userCnt) {
		this.userCnt = userCnt;
	}

	public int[] getOrderCnt() {
		return orderCnt;
	}

	public void setOrderCnt(int[] orderCnt) {
		this.orderCnt = orderCnt;
	}
	
    
}
