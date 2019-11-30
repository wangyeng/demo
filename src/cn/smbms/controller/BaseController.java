package cn.smbms.controller;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;

/**
 * @author 王业龙
 * @version 创建时间：2019年11月27日 下午6:52:59
 * @ClassName 类名称
 * @Description 类描述 通过 @InitBinder 添加自定义的编辑器
 */
public class BaseController {

	/**
	 * 使用 @InitBinder 解决 SpringMVC 日期类型无法绑定的问题
	 * 
	 * @param dataBinder
	 */
	@InitBinder
	public void initBinder(WebDataBinder dataBinder) {
		dataBinder.registerCustomEditor(Date.class, new CustomDateEditor(new SimpleDateFormat("yyyy-MM-dd"), true));
	}

}
