package cn.smbms.tools;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.core.convert.converter.Converter;

/**
 * @author 王业龙
 * @version 创建时间：2019年11月27日 下午6:36:08
 * @ClassName 类名称
 * @Description 类描述 将字符串转换成指定格式的时间对象 Date 的自定义转换器
 */
public class StringToDateConverter implements Converter<String, Date> {

	private String datePattern;

	public StringToDateConverter(String datePattern) {
		this.datePattern = datePattern;
	}

	@Override
	public Date convert(String s) {
		// TODO Auto-generated method stub
		Date date = null;
		try {
			date = new SimpleDateFormat(datePattern).parse(s);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return date;
	}

}
