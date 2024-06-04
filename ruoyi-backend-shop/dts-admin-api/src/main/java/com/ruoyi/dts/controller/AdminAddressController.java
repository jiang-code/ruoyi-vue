package com.ruoyi.dts.controller;

import com.alibaba.fastjson.JSONObject;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.dts.domain.DtsAddress;
import com.ruoyi.dts.service.DtsAddressService;
import com.ruoyi.dts.service.DtsRegionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/address")
@Validated
public class AdminAddressController extends BaseController {
	private static final Logger logger = LoggerFactory.getLogger(AdminAddressController.class);

	@Autowired
	private DtsAddressService addressService;

	@Autowired
	private DtsRegionService regionService;

	private Map<String, Object> toVo(DtsAddress address) {
		Map<String, Object> addressVo = new HashMap<>();
		addressVo.put("id", address.getId());
		addressVo.put("userId", address.getUserId());
		addressVo.put("name", address.getName());
		addressVo.put("mobile", address.getMobile());
		addressVo.put("isDefault", address.getIsDefault());
		addressVo.put("provinceId", address.getProvinceId());
		addressVo.put("cityId", address.getCityId());
		addressVo.put("areaId", address.getAreaId());
		addressVo.put("address", address.getAddress());
		String province = regionService.findById(address.getProvinceId()).getName();
		String city = regionService.findById(address.getCityId()).getName();
		String area = regionService.findById(address.getAreaId()).getName();
		addressVo.put("province", province);
		addressVo.put("city", city);
		addressVo.put("area", area);
		return addressVo;
	}

	@PreAuthorize("@ss.hasPermi('admin:address:list')")
	@GetMapping("/list")
	public Object list(Integer userId, String name) {
		startPage();
		List<DtsAddress> addressList = addressService.querySelective(userId, name);
		List<Map<String, Object>> addressVoList = new ArrayList<>(addressList.size());
		for (DtsAddress address : addressList) {
			Map<String, Object> addressVo = toVo(address);
			addressVoList.add(addressVo);
		}

		logger.info("【请求结束】用户管理->收货地址->查询,响应结果:{}", JSONObject.toJSONString(addressVoList));
		return getDataTable(addressVoList);
	}
}
