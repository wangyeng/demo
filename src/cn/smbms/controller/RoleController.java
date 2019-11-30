package cn.smbms.controller;

import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import cn.smbms.pojo.Role;
import cn.smbms.service.role.RoleService;

/**
 * @author 王业龙
 * @version 创建时间：2019年11月23日 下午12:52:13
 * @ClassName 类名称
 * @Description 类描述
 */
@Controller
@RequestMapping("/role")
public class RoleController {

	private Logger logger = Logger.getLogger(RoleController.class);

	@Resource
	private RoleService roleService;

	@RequestMapping(value = "/rolelist.html")
	public String getRoleList(Model model) {
		List<Role> roleList = null;
		roleList = roleService.getRoleList();
		logger.info(roleList);
		model.addAttribute("roleList", roleList);
		return "rolelist";
	}

}
