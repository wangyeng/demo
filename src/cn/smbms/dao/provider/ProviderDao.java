package cn.smbms.dao.provider;

import java.sql.Connection;
import java.util.List;

import cn.smbms.pojo.Provider;

/**
 * @author 王业龙
 * @version 创建时间：2019年11月23日 上午11:16:14
 * @ClassName 类名称
 * @Description 类描述
 */
public interface ProviderDao {
	/**
	 * 通过供应商名称、编码获取供应商列表-模糊查询-providerList
	 * 
	 * @param connection
	 * @param proName
	 * @return
	 * @throws Exception
	 */
	public List<Provider> getProviderList(Connection connection, String proName, String proCode, int currentPageNo,
			int pageSize) throws Exception;

	/**
	 * 通过条件查询-供应商表记录数
	 * 
	 * @param connection
	 * @param proName
	 * @param proCode
	 * @return
	 * @throws Exception
	 */
	public int getproviderCount(Connection connection, String proName, String proCode) throws Exception;

	/**
	 * 增加供应商
	 * 
	 * @param connection
	 * @param provider
	 * @return
	 * @throws Exception
	 */
	public int add(Connection connection, Provider provider) throws Exception;

	/**
	 * 通过proId获取Provider
	 * 
	 * @param connection
	 * @param id
	 * @return
	 * @throws Exception
	 */
	public Provider getProviderById(Connection connection, String id) throws Exception;

	/**
	 * 修改用户信息
	 * 
	 * @param connection
	 * @param user
	 * @return
	 * @throws Exception
	 */
	public int modify(Connection connection, Provider provider) throws Exception;

	public int delete(Connection connection, Integer id) throws Exception;

}
