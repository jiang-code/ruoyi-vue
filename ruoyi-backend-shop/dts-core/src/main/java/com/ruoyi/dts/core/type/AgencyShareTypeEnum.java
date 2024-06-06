package com.ruoyi.dts.core.type;

/**
 * 代理分享海报 枚举类型
 * 
 * @author suichj
 * 
 * @since 1.0.0
 */
public enum AgencyShareTypeEnum {

	GOODS_SHARE(1, "商品分享海报"), BRAND_SHARE(2, "入驻品牌店铺分享海报"),GROUPON_SHARE(3, "团购分享海报");

	private Integer type;
	private String desc;

	private AgencyShareTypeEnum(Integer type, String desc) {
		this.type = type;
		this.desc = desc;
	}

	public static AgencyShareTypeEnum getInstance(Integer type2) {
		if (type2 != null) {
			for (AgencyShareTypeEnum tmp : AgencyShareTypeEnum.values()) {
				if (tmp.type.intValue() == type2.intValue()) {
					return tmp;
				}
			}
		}
		return null;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}
}
