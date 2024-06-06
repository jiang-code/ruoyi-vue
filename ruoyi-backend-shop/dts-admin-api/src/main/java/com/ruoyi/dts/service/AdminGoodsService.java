package com.ruoyi.dts.service;

import com.alibaba.fastjson2.JSONObject;
import com.github.pagehelper.PageInfo;
import com.ruoyi.common.constant.HttpStatus;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.dts.dao.GoodsAllinone;
import com.ruoyi.dts.util.CatVo;
import com.ruoyi.dts.core.qcode.QCodeService;
import com.ruoyi.dts.db.domain.*;
import com.ruoyi.dts.db.service.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class AdminGoodsService {
	private static final Logger logger = LoggerFactory.getLogger(AdminGoodsService.class);

	@Autowired
	private DtsGoodsService goodsService;
	@Autowired
	private DtsGoodsSpecificationService specificationService;
	@Autowired
	private DtsGoodsAttributeService attributeService;
	@Autowired
	private DtsGoodsProductService productService;
	@Autowired
	private DtsCategoryService categoryService;
	@Autowired
	private DtsBrandService brandService;
	@Autowired
	private DtsCartService cartService;
	@Autowired
	private DtsOrderGoodsService orderGoodsService;

	@Autowired
	private QCodeService qCodeService;
	
	@Autowired
	private AdminDataAuthService adminDataAuthService;

	public Object list(String goodsSn, String name, Integer page, Integer limit, String sort, String order, List<Integer> brandIds) {
		List<DtsGoods> goodsList = goodsService.querySelective(goodsSn, name, brandIds);
		long total = PageInfo.of(goodsList).getTotal();
		Map<String, Object> data = new HashMap<>();
		data.put("total", total);
		data.put("items", goodsList);

		logger.info("【请求结束】商品管理->商品管理->查询,响应结果:{}", JSONObject.toJSONString(data));
		return AjaxResult.success(data);
	}

	private Object validate(GoodsAllinone goodsAllinone) {
		DtsGoods goods = goodsAllinone.getGoods();
		String name = goods.getName();
		if (StringUtils.isEmpty(name)) {
			return AjaxResult.error(HttpStatus.BAD_REQUEST,"参数不对");
		}
		String goodsSn = goods.getGoodsSn();
		if (StringUtils.isEmpty(goodsSn)) {
			return AjaxResult.error(HttpStatus.BAD_REQUEST,"参数不对");
		}
		// 品牌商可以不设置，如果设置则需要验证品牌商存在
		Integer brandId = goods.getBrandId();
		if (brandId != null && brandId != 0) {
			if (brandService.findById(brandId) == null) {
				return AjaxResult.error(HttpStatus.BAD_REQUEST,"参数不对");
			}
		}
		// 分类可以不设置，如果设置则需要验证分类存在
		Integer categoryId = goods.getCategoryId();
		if (categoryId != null && categoryId != 0) {
			if (categoryService.findById(categoryId) == null) {
				return AjaxResult.error(HttpStatus.BAD_REQUEST,"参数不对");
			}
		}

		DtsGoodsAttribute[] attributes = goodsAllinone.getAttributes();
		for (DtsGoodsAttribute attribute : attributes) {
			String attr = attribute.getAttribute();
			if (StringUtils.isEmpty(attr)) {
				return AjaxResult.error(HttpStatus.BAD_REQUEST,"参数不对");
			}
			String value = attribute.getValue();
			if (StringUtils.isEmpty(value)) {
				return AjaxResult.error(HttpStatus.BAD_REQUEST,"参数不对");
			}
		}

		DtsGoodsSpecification[] specifications = goodsAllinone.getSpecifications();
		for (DtsGoodsSpecification specification : specifications) {
			String spec = specification.getSpecification();
			if (StringUtils.isEmpty(spec)) {
				return AjaxResult.error(HttpStatus.BAD_REQUEST,"参数不对");
			}
			String value = specification.getValue();
			if (StringUtils.isEmpty(value)) {
				return AjaxResult.error(HttpStatus.BAD_REQUEST,"参数不对");
			}
		}

		DtsGoodsProduct[] products = goodsAllinone.getProducts();
		for (DtsGoodsProduct product : products) {
			Integer number = product.getNumber();
			if (number == null || number < 0) {
				return AjaxResult.error(HttpStatus.BAD_REQUEST,"参数不对");
			}

			BigDecimal price = product.getPrice();
			if (price == null) {
				return AjaxResult.error(HttpStatus.BAD_REQUEST,"参数不对");
			}

			String[] productSpecifications = product.getSpecifications();
			if (productSpecifications.length == 0) {
				return AjaxResult.error(HttpStatus.BAD_REQUEST,"参数不对");
			}
		}

		return null;
	}

	/**
	 * 编辑商品
	 * <p>
	 * TODO 目前商品修改的逻辑是 1. 更新Dts_goods表 2.
	 * 逻辑删除Dts_goods_specification、Dts_goods_attribute、Dts_goods_product 3.
	 * 添加Dts_goods_specification、Dts_goods_attribute、Dts_goods_product
	 * <p>
	 * 这里商品三个表的数据采用删除再添加的策略是因为 商品编辑页面，支持管理员添加删除商品规格、添加删除商品属性，因此这里仅仅更新是不可能的，
	 * 只能删除三个表旧的数据，然后添加新的数据。 但是这里又会引入新的问题，就是存在订单商品货品ID指向了失效的商品货品表。
	 * 因此这里会拒绝管理员编辑商品，如果订单或购物车中存在商品。 所以这里可能需要重新设计。
	 */
	@Transactional
	public Object update(GoodsAllinone goodsAllinone) {
		Object error = validate(goodsAllinone);
		if (error != null) {
			return error;
		}
		DtsGoods goods = goodsAllinone.getGoods();
		DtsGoodsAttribute[] attributes = goodsAllinone.getAttributes();
		DtsGoodsSpecification[] specifications = goodsAllinone.getSpecifications();
		DtsGoodsProduct[] products = goodsAllinone.getProducts();

		Integer id = goods.getId();
		// 检查是否存在购物车商品或者订单商品
		// 如果存在则拒绝修改商品。
		if (orderGoodsService.checkExist(id) || cartService.checkExist(id)) {
			return AjaxResult.error(HttpStatus.BAD_REQUEST,"拒绝修改");

		}

		// 将生成的分享图片地址写入数据库
		String url = qCodeService.createGoodShareImage(null,goods.getId().toString(), goods.getPicUrl(), goods.getName(),goods.getCounterPrice(),goods.getRetailPrice());
		goods.setShareUrl(url);

		// 商品基本信息表Dts_goods
		if (goodsService.updateById(goods) == 0) {
			logger.error("商品管理->商品管理->编辑错误:{}", "更新数据失败");
			throw new RuntimeException("更新数据失败");
		}

		Integer gid = goods.getId();
		specificationService.deleteByGid(gid);
		attributeService.deleteByGid(gid);
		productService.deleteByGid(gid);

		// 商品规格表Dts_goods_specification
		for (DtsGoodsSpecification specification : specifications) {
			specification.setGoodsId(goods.getId());
			specificationService.add(specification);
		}

		// 商品参数表Dts_goods_attribute
		for (DtsGoodsAttribute attribute : attributes) {
			attribute.setGoodsId(goods.getId());
			attributeService.add(attribute);
		}

		// 商品货品表Dts_product
		for (DtsGoodsProduct product : products) {
			product.setGoodsId(goods.getId());
			productService.add(product);
		}
		//qCodeService.createGoodShareImage(goods.getId().toString(), goods.getPicUrl(), goods.getName());

		logger.info("【请求结束】商品管理->商品管理->编辑,响应结果:{}", "成功!");
		return AjaxResult.success();
	}

	@Transactional
	public Object delete(DtsGoods goods) {
		Integer id = goods.getId();
		if (id == null) {
			return AjaxResult.error(HttpStatus.BAD_REQUEST,"参数不对");
		}

		Integer gid = goods.getId();
		goodsService.deleteById(gid);
		specificationService.deleteByGid(gid);
		attributeService.deleteByGid(gid);
		productService.deleteByGid(gid);

		logger.info("【请求结束】商品管理->商品管理->删除,响应结果:{}", "成功!");
		return AjaxResult.success();
	}

	@Transactional
	public Object create(GoodsAllinone goodsAllinone) {
		Object error = validate(goodsAllinone);
		if (error != null) {
			return error;
		}

		DtsGoods goods = goodsAllinone.getGoods();
		DtsGoodsAttribute[] attributes = goodsAllinone.getAttributes();
		DtsGoodsSpecification[] specifications = goodsAllinone.getSpecifications();
		DtsGoodsProduct[] products = goodsAllinone.getProducts();

		String name = goods.getName();
		if (goodsService.checkExistByName(name)) {
			return AjaxResult.error(HttpStatus.BAD_REQUEST,"上架错误");
		}

		// 商品基本信息表Dts_goods
		goodsService.add(goods);

		// 将生成的分享图片地址写入数据库
		String url = qCodeService.createGoodShareImage(null,goods.getId().toString(), goods.getPicUrl(), goods.getName(),goods.getCounterPrice(),goods.getRetailPrice());
		if (!StringUtils.isEmpty(url)) {
			goods.setShareUrl(url);
			if (goodsService.updateById(goods) == 0) {
				logger.error("商品管理->商品管理->上架错误:{}", "更新数据失败");
				throw new RuntimeException("更新数据失败");
			}
		}

		// 商品规格表Dts_goods_specification
		for (DtsGoodsSpecification specification : specifications) {
			specification.setGoodsId(goods.getId());
			specificationService.add(specification);
		}

		// 商品参数表Dts_goods_attribute
		for (DtsGoodsAttribute attribute : attributes) {
			attribute.setGoodsId(goods.getId());
			attributeService.add(attribute);
		}

		// 商品货品表Dts_product
		for (DtsGoodsProduct product : products) {
			product.setGoodsId(goods.getId());
			productService.add(product);
		}

		logger.info("【请求结束】商品管理->商品管理->上架,响应结果:{}", "成功!");
		return AjaxResult.success();
	}

	public Object catAndBrand() {
		// http://element-cn.eleme.io/#/zh-CN/component/cascader
		// 管理员设置“所属分类”
		List<DtsCategory> l1CatList = categoryService.queryL1();
		List<CatVo> categoryList = new ArrayList<>(l1CatList.size());

		for (DtsCategory l1 : l1CatList) {
			CatVo l1CatVo = new CatVo();
			l1CatVo.setValue(l1.getId());
			l1CatVo.setLabel(l1.getName());

			List<DtsCategory> l2CatList = categoryService.queryByPid(l1.getId());
			List<CatVo> children = new ArrayList<>(l2CatList.size());
			for (DtsCategory l2 : l2CatList) {
				CatVo l2CatVo = new CatVo();
				l2CatVo.setValue(l2.getId());
				l2CatVo.setLabel(l2.getName());
				children.add(l2CatVo);
			}
			l1CatVo.setChildren(children);

			categoryList.add(l1CatVo);
		}
		
		//品牌商获取需要控制数据权限，如果是店铺管理员下拉的品牌商只能选择当前用户可管理的品牌商
		List<DtsBrand> list = new ArrayList<>();
		List<Map<String, Object>> brandList = new ArrayList<>();
		List<Integer> brandIds = null;
		if (adminDataAuthService.isBrandManager()) {
			list = brandService.getAdminBrands(SecurityUtils.getUserId());
			logger.info("运营商管理角色操作，需控制数据权限，brandIds:{}", JSONObject.toJSONString(brandIds));
		} else {
			list = brandService.all();
			brandList = new ArrayList<>(list.size());
		}
		
		for (DtsBrand brand : list) {
			Map<String, Object> b = new HashMap<>(2);
			b.put("value", brand.getId());
			b.put("label", brand.getName());
			brandList.add(b);
		}

		Map<String, Object> data = new HashMap<>();
		data.put("categoryList", categoryList);
		data.put("brandList", brandList);
		return AjaxResult.success(data);
	}

	public Object detail(Integer id) {
		DtsGoods goods = goodsService.findById(id);
		List<DtsGoodsProduct> products = productService.queryByGid(id);
		List<DtsGoodsSpecification> specifications = specificationService.queryByGid(id);
		List<DtsGoodsAttribute> attributes = attributeService.queryByGid(id);

		//用于展示商品归属的类目（页面级联下拉控件数据展示）
		Integer categoryId = goods.getCategoryId();
		DtsCategory category = categoryService.findById(categoryId);
		Integer[] categoryIds = new Integer[] {};
		if (category != null) {
			Integer parentCategoryId = category.getPid();
			categoryIds = new Integer[] { parentCategoryId, categoryId };
		}

		Map<String, Object> data = new HashMap<>();
		data.put("goods", goods);
		data.put("specifications", specifications);
		data.put("products", products);
		data.put("attributes", attributes);
		data.put("categoryIds", categoryIds);

		logger.info("【请求结束】商品管理->商品管理->详情,响应结果:{}", "成功!");
		return AjaxResult.success(data);
	}

}
