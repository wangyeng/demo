package cn.smbms.controller;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.math.RandomUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.mysql.jdbc.StringUtils;

import cn.smbms.pojo.Role;
import cn.smbms.pojo.User;
import cn.smbms.service.role.RoleService;
import cn.smbms.service.user.UserService;
import cn.smbms.tools.Constants;
import cn.smbms.tools.PageSupport;

/**
 * @author 王业龙
 * @version 创建时间：2019年11月21日 下午2:02:37
 * @ClassName 类名称
 * @Description 类描述
 */
@Controller
@RequestMapping("/user")
public class UserController extends BaseController {
	private Logger logger = Logger.getLogger(UserController.class);

	@Resource
	private UserService userService;
	@Resource
	private RoleService roleService;

	@RequestMapping(value = "/login.html")
	public String login() {
		logger.debug("输出：==============");
		return "login";
	}

	@RequestMapping(value = "/dologin.html", method = RequestMethod.POST)
	public String doLogin(@RequestParam String userCode, @RequestParam String userPassword, HttpServletRequest request,
			HttpSession session) {
		logger.debug("doLogin====================================");
		// 调用service方法，进行用户匹配
		User user = userService.login(userCode, userPassword);
		if (null == user) {
			throw new RuntimeException("用户名不存在！");
			// request.setAttribute("error", "用户名不正确");
			// return "login";
		} else if (null != user && !user.getUserPassword().equals(userPassword)) {
			// 页面跳转（login.jsp）带出提示信息--转发
			throw new RuntimeException("密码输入错误！");
			// request.setAttribute("error", "密码不正确");
			// return "login";
		}
		// 放入session
		session.setAttribute(Constants.USER_SESSION, user);
		// 页面跳转
		return "redirect:/user/main.html";
	}

	@RequestMapping(value = "/main.html")
	public String main(HttpSession session) {
		if (session.getAttribute(Constants.USER_SESSION) == null) {
			return "redirect:/user/login.html";
		}
		return "frame";
	}

	@RequestMapping(value = "/logout.html")
	public String logout(HttpSession session) {
		// 清除session
		session.removeAttribute(Constants.USER_SESSION);
		return "login";
	}

	@RequestMapping(value = "exlogin.html", method = RequestMethod.GET)
	public String exLogin(@RequestParam String userCode, @RequestParam String userPassword) {
		logger.debug("========================");
		// 调用service方法，进行用户匹配
		User user = userService.login(userCode, userPassword);
		if (null == user) {
			// 登录失败
			throw new RuntimeException("用户名或者密码不正确！");
		}
		return "redirect:/user/mian.html";
	}

	// 局部异常处理
//	@ExceptionHandler(value = { RuntimeException.class })
//	public String handlerException(RuntimeException e, HttpServletRequest req) {
//		req.setAttribute("e", e);
//		return "login";
//	}

	@RequestMapping(value = "/userlist.html")
	public String getUserList(Model model, @RequestParam(value = "queryname", required = false) String queryUserName,
			@RequestParam(value = "queryUserRole", required = false) String queryUserRole,
			@RequestParam(value = "pageIndex", required = false) String pageIndex) {
		int _queryUserRole = 0;
		List<User> userList = null;
		// 设置页面容量
		int pageSize = Constants.pageSize;
		// 当前页码
		int currentPageNo = 1;
		if (queryUserName == null)
			queryUserName = "";
		if (queryUserRole != null && !queryUserRole.equals(""))
			_queryUserRole = Integer.parseInt(queryUserRole);

		if (pageIndex != null) {
			try {
				currentPageNo = Integer.valueOf(pageIndex);
			} catch (NumberFormatException e) {
				return "redirect:/user/syserror.html";
				// TODO: handle exception
			}
		}
		// 总数量（表）
		int totalCount = userService.getUserCount(queryUserName, _queryUserRole);
		// 总页数
		PageSupport pages = new PageSupport();
		pages.setCurrentPageNo(currentPageNo);
		pages.setPageSize(pageSize);
		pages.setTotalCount(totalCount);
		int totalPageCount = pages.getTotalPageCount();
		// 控制首页和尾页
		if (currentPageNo < 1)
			currentPageNo = 1;
		else if (currentPageNo > totalPageCount)
			currentPageNo = totalPageCount;
		userList = userService.getUserList(queryUserName, _queryUserRole, currentPageNo, pageSize);
		model.addAttribute("userList", userList);
//		List<Role> roleList = null;
//		roleList = roleService.getRoleList();
//		model.addAttribute("roleList", roleList);
		model.addAttribute("queryUserName", queryUserName);
		model.addAttribute("queryUserRole", queryUserRole);
		model.addAttribute("totalPageCount", totalPageCount);
		model.addAttribute("totalCount", totalCount);
		model.addAttribute("currentPageNo", currentPageNo);
		return "userlist";
	}

	@RequestMapping(value = "/syserror.html")
	public String sysError() {
		return "syserror";
	}

	@RequestMapping(value = "/useradd.html", method = RequestMethod.GET)
	public String addUser(User user, Model model) {
		model.addAttribute("user", user);
		return "useradd";
	}

	// 文件上传
	@RequestMapping(value = "/useraddsave.html", method = RequestMethod.POST)
	public String addUserSave(User user, HttpSession session, HttpServletRequest request,
			@RequestParam(value = "attachs", required = false) MultipartFile[] attachs) {

		String idPicPath = null;
		String workPicPath = null;
		String errorInfo = null;
		boolean flag = true;

		String path = request.getSession().getServletContext().getRealPath("statics" + File.separator + "uploadfiles");
		for (int i = 0; i < attachs.length; i++) {
			MultipartFile attach = attachs[i];
			// 判断文件是否为空
			if (!attach.isEmpty()) {
				if (i == 0)
					errorInfo = "uploadFileError";
				else if (i == 1)
					errorInfo = "uploadWpError";

				String oldFileName = attach.getOriginalFilename();// 原文件名
				String prefix = FilenameUtils.getExtension(oldFileName);// 原文件后缀
				int filesize = 500000;
				if (attach.getSize() > filesize) {// 上传文件大小不得超过 500k
					request.setAttribute(errorInfo, " * 上传大小不得超过 500k");
					flag = false;
				} else if (prefix.equalsIgnoreCase("jpg") || prefix.equalsIgnoreCase("png")
						|| prefix.equalsIgnoreCase("jpeg") || prefix.equalsIgnoreCase("pneg")) {
					// 上传图片格式正确
					String fileName = System.currentTimeMillis() + RandomUtils.nextInt(1000000) + "_Personal.jpg";
					File targetFile = new File(path, fileName);
					if (!targetFile.exists())
						targetFile.mkdirs();
					// 保存
					try {
						attach.transferTo(targetFile);
					} catch (Exception e) {
						// TODO: handle exception
						e.printStackTrace();
						request.setAttribute(errorInfo, " * 上传失败！");
						flag = false;
					}
					if (i == 0)
						idPicPath = path + File.separator + fileName;
					else if (i == 1)
						workPicPath = path + File.separator + fileName;
				} else {
					request.setAttribute(errorInfo, " * 上传图片格式不正确");
					flag = false;
				}
			}
		}

		if (flag) {
			user.setCreatedBy(((User) session.getAttribute(Constants.USER_SESSION)).getId());
			user.setCreationDate(new Date());
			user.setIdPicPath(idPicPath);
			user.setWorkPicPath(workPicPath);
			if (userService.add(user))
				return "redirect:/user/userlist.html";
		}
		return "useradd";
	}

	@RequestMapping(value = "/usermodify.html", method = RequestMethod.GET)
	public String getUserById(@RequestParam String uid, Model model) {
		User user = userService.getUserById(uid);
		model.addAttribute("user", user);
		return "usermodify";
	}

	@RequestMapping(value = "/usermodifysave.html", method = RequestMethod.POST)
	public String modifyUserSave(User user, HttpSession session) {
		user.setModifyBy(((User) session.getAttribute(Constants.USER_SESSION)).getId());
		user.setModifyDate(new Date());
		if (userService.modify(user)) {
			return "redirect:/user/userlist.html";
		}
		return "usermodify";
	}

	@RequestMapping(value = "/view/{id} ", method = RequestMethod.GET)
	public String view(@PathVariable String id, Model model) {
		User user = userService.getUserById(id);
		model.addAttribute(user);
		return "userview";
	}

	@RequestMapping(value = "/view.json", method = RequestMethod.GET)
	@ResponseBody
	public User view(@RequestParam String id) {
		User user = new User();
		try {
			user = userService.getUserById(id);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return user;
	}

	@RequestMapping(value = "/ucexist.json")
	@ResponseBody
	public Map<String, String> userCodeIsExit(@RequestParam String userCode) {
		HashMap<String, String> resultMap = new HashMap<>();
		if (StringUtils.isNullOrEmpty(userCode)) {
			resultMap.put("userCode", "exist");
		} else {
			User user = userService.selectUserCodeExist(userCode);
			if (null != user)
				resultMap.put("userCode", "exist");
			else
				resultMap.put("userCode", "noexist");
		}
		return resultMap;
	}

	@RequestMapping(value = "/pwdmodify.html", method = RequestMethod.GET)
	public String pwdModify(HttpSession session) {
		if (session.getAttribute(Constants.USER_SESSION) == null)
			return "redirect:/user/login.html";
		return "pwdmodify";
	}

	@RequestMapping(value = "/pwdmodify.json", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, String> getPwdByUserId(@RequestParam String oldpassword, HttpSession session) {
		HashMap<String, String> resultMap = new HashMap<>();
		if (null == session.getAttribute(Constants.USER_SESSION)) {
			resultMap.put("result", "sessionerror");
		} else if (StringUtils.isNullOrEmpty(oldpassword)) {
			resultMap.put("result", "error");
		} else {
			String sessionPwd = ((User) session.getAttribute(Constants.USER_SESSION)).getUserPassword();
			if (oldpassword.equals(sessionPwd))
				resultMap.put("result", "true");
			else
				resultMap.put("result", "false");
		}
		return resultMap;
	}

	@RequestMapping(value = "/pwdsave.html")
	public String pwdSave(@RequestParam(value = "newpassword") String newPassword, HttpSession session,
			HttpServletRequest request) {
		boolean flag = false;
		Object o = session.getAttribute(Constants.USER_SESSION);
		if (o != null && !StringUtils.isNullOrEmpty(newPassword)) {
			flag = userService.updatePwd(((User) o).getId(), newPassword);
			if (flag) {
				request.setAttribute(Constants.SYS_MESSAGE, "修改密码成功，请退出并使用新密码重新登录！");
				return "login";
			} else {
				request.setAttribute(Constants.SYS_MESSAGE, "修改密码失败！");
			}
		} else {
			request.setAttribute(Constants.SYS_MESSAGE, "修改密码失败！");
		}
		return "pwdmodify";
	}

	@RequestMapping(value = "/rolelist.json", method = RequestMethod.GET)
	@ResponseBody
	public List<Role> getRoleList() {
		List<Role> roleList = null;
		roleList = roleService.getRoleList();
		return roleList;
	}

	@RequestMapping(value = "/deluser.json", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, String> deluser(@RequestParam String id) {
		Map<String, String> resultMap = new HashMap<>();
		if (StringUtils.isNullOrEmpty(id)) {
			resultMap.put("delResult", "notexist");
		} else {
			if (userService.deleteUserById(Integer.parseInt(id)))
				resultMap.put("delResult", "true");
			else
				resultMap.put("delResult", "false");
		}
		return resultMap;
	}

}
