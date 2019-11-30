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

import cn.smbms.pojo.Provider;
import cn.smbms.pojo.User;
import cn.smbms.service.provider.ProviderService;
import cn.smbms.tools.Constants;
import cn.smbms.tools.PageSupport;

/**
 * @author 王业龙
 * @version 创建时间：2019年11月23日 上午11:35:33
 * @ClassName 类名称
 * @Description 类描述
 */
@Controller
@RequestMapping("/provider")
public class ProviderController {
	private Logger logger = Logger.getLogger(ProviderController.class);

	@Resource
	private ProviderService providerService;

	@RequestMapping(value = "providerlist.html")
	public String getProviderList(Model model,
			@RequestParam(value = "queryProCode", required = false) String queryProCode,
			@RequestParam(value = "queryProName", required = false) String queryProName,
			@RequestParam(value = "pageIndex", required = false) String pageIndex) {
		logger.info("输出getProviderList ---- > queryProCode: " + queryProCode);
		logger.info("输出getProviderList ---- > queryProName: " + queryProName);
		logger.info("输出getProviderList ---- > pageIndex: " + pageIndex);

		List<Provider> providerList = null;
		// 设置页面容量
		int pageSize = Constants.pageSize;
		// 当前页码
		int currentPageNo = 1;

		if (queryProCode == null)
			queryProCode = "";
		if (queryProName == null)
			queryProName = "";
		if (pageIndex != null) {
			try {
				currentPageNo = Integer.valueOf(pageIndex);
			} catch (NumberFormatException e) {
				// TODO: handle exception
				return "redirect:/provider/syserror.html";
			}
		}
		// 总数量（表）
		int totalCount = providerService.getproviderCount(queryProCode, queryProName);
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

		providerList = providerService.getProviderList(queryProName, queryProCode, currentPageNo, pageSize);
		model.addAttribute("providerList", providerList);
		model.addAttribute("queryProCode", queryProCode);
		model.addAttribute("queryProName", queryProName);
		model.addAttribute("totalPageCount", totalPageCount);
		model.addAttribute("totalCount", totalCount);
		model.addAttribute("currentPageNo", currentPageNo);
		return "providerlist";
	}

	@RequestMapping(value = "/syserror.html")
	public String sysError() {
		return "syserror";
	}

	@RequestMapping(value = "/provideradd.html", method = RequestMethod.GET)
	public String addProvider(Provider provider, Model model) {
		model.addAttribute("Provider", provider);
		return "provideradd";
	}

	@RequestMapping(value = "/provideraddsave.html", method = RequestMethod.POST)
	public String addProviderSave(Provider provider, HttpSession session, HttpServletRequest request,
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
			provider.setCreatedBy(((User) session.getAttribute(Constants.USER_SESSION)).getId());
			provider.setCreationDate(new Date());
			provider.setCompanyLicPicPath(idPicPath);
			provider.setOrgCodePicPath(workPicPath);
			if (providerService.add(provider)) {
				return "redirect:/provider/providerlist.html";
			}
		}
		return "provideradd";
	}

	@RequestMapping(value = "/view/{id}", method = RequestMethod.GET)
	public String view(@PathVariable String id, Model model, HttpServletRequest request) {
		Provider provider = providerService.getProviderById(id);
		if (provider.getCompanyLicPicPath() != null && !"".equals(provider.getCompanyLicPicPath())) {
			String[] paths = provider.getCompanyLicPicPath().split("\\" + File.separator);
			logger.debug("视图输出：" + File.separator);
			logger.debug("视图输出：" + paths);
			provider.setCompanyLicPicPath(request.getContextPath() + "/statics/uploadfiles/" + paths[paths.length - 1]);
		}
		if (provider.getOrgCodePicPath() != null && !"".equals(provider.getOrgCodePicPath())) {
			String[] paths = provider.getOrgCodePicPath().split("\\" + File.separator);
			provider.setOrgCodePicPath(request.getContextPath() + "/statics/uploadfiles/" + paths[paths.length - 1]);
		}
		model.addAttribute(provider);
		return "providerview";
	}

	@RequestMapping(value = "/modify/{id}", method = RequestMethod.GET)
	public String getProviderById(@PathVariable String id, Model model) {
		Provider provider = providerService.getProviderById(id);
		model.addAttribute(provider);
		return "providermodify";
	}

	@RequestMapping(value = "/modifysave.html", method = RequestMethod.POST)
	public String modifyProviderSave(Provider provider, HttpSession session) {
		provider.setModifyBy(((User) session.getAttribute(Constants.USER_SESSION)).getId());
		provider.setModifyDate(new Date());
		if (providerService.modify(provider))
			return "redirect:/provider/providerlist.html";
		return "providermodify";
	}

	@RequestMapping(value = "/del.json", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, String> del(@RequestParam String id) {
		Map<String, String> resultMap = new HashMap<>();
		if (StringUtils.isNullOrEmpty(id)) {
			resultMap.put("delResult", "notexist");
		} else {
			if (providerService.deleteById(Integer.parseInt(id)))
				resultMap.put("delResult", "true");
			else
				resultMap.put("delResult", "false");
		}
		return resultMap;
	}

}
