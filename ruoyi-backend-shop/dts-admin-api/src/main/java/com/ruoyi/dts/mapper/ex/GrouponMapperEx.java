package com.ruoyi.dts.mapper.ex;

import com.ruoyi.dts.domain.DtsGroupon;
import com.ruoyi.dts.domain.DtsGrouponRules;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 团购管理
 * @author ruoyi
 * @since 1.0.0
 */
public interface GrouponMapperEx {

	/**
	 * 按入驻店铺查询归属的团购规则信息
	 * @param goodsId
	 * @param orderBySql
	 * @param brandIdsSql
	 * @return
	 */
	List<DtsGrouponRules> queryBrandGrouponRules(@Param("goodsId") String goodsId, @Param("orderBySql") String orderBySql, @Param("brandIdsSql") String brandIdsSql);

	/**
	 * 按入驻店铺查询归属的团购记录信息
	 * @param goodsId
	 * @param orderBySql
	 * @param brandIdsSql
	 * @return
	 */
	List<DtsGroupon> queryBrandGroupons(@Param("rulesId") String rulesId, @Param("orderBySql") String orderBySql, @Param("brandIdsSql") String brandIdsSql);

}
