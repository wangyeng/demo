package cn.smbms.service.provider;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import cn.smbms.dao.BaseDao;
import cn.smbms.dao.provider.ProviderDao;
import cn.smbms.pojo.Provider;

/**
 * @author 王业龙
 * @version 创建时间：2019年11月23日 上午11:29:20
 * @ClassName 类名称
 * @Description 类描述
 */
@Service
public class ProviderServiceImpl implements ProviderService {
	@Resource
	private ProviderDao providerDao;

	@Override
	public List<Provider> getProviderList(String proName, String proCode, int currentPageNo, int pageSize) {
		// TODO Auto-generated method stub
		Connection connection = null;
		List<Provider> providerList = null;
		System.out.println("query proName ---- > " + proName);
		System.out.println("query proCode ---- > " + proCode);
		try {
			connection = BaseDao.getConnection();
			providerList = providerDao.getProviderList(connection, proName, proCode, currentPageNo, pageSize);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			BaseDao.closeResource(connection, null, null);
		}
		return providerList;
	}

	@Override
	public int getproviderCount(String proName, String proCode) {
		// TODO Auto-generated method stub
		Connection connection = null;
		int count = 0;
		try {
			connection = BaseDao.getConnection();
			count = providerDao.getproviderCount(connection, proName, proCode);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			BaseDao.closeResource(connection, null, null);
		}
		return count;
	}

	@Override
	public boolean add(Provider provider) {
		// TODO Auto-generated method stub
		boolean flag = false;
		Connection connection = null;
		try {
			connection = BaseDao.getConnection();
			connection.setAutoCommit(false);// 开启JDBC事务管理
			if (providerDao.add(connection, provider) > 0)
				flag = true;
			connection.commit();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			try {
				connection.rollback();
			} catch (SQLException e2) {
				// TODO: handle exception
				e2.printStackTrace();
			}
		} finally {
			// 在service层进行connection连接的关闭
			BaseDao.closeResource(connection, null, null);
		}
		return flag;
	}

	@Override
	public Provider getProviderById(String id) {
		// TODO Auto-generated method stub
		Provider provider = null;
		Connection connection = null;
		try {
			connection = BaseDao.getConnection();
			provider = providerDao.getProviderById(connection, id);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			provider = null;
		} finally {
			BaseDao.closeResource(connection, null, null);
		}
		return provider;
	}

	@Override
	public boolean modify(Provider provider) {
		// TODO Auto-generated method stub
		Connection connection = null;
		boolean flag = false;
		try {
			connection = BaseDao.getConnection();
			if (providerDao.modify(connection, provider) > 0)
				flag = true;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			BaseDao.closeResource(connection, null, null);
		}
		return flag;
	}

	@Override
	public boolean deleteById(Integer id) {
		// TODO Auto-generated method stub
		Connection connection = null;
		boolean flag = true;
		try {
			connection = BaseDao.getConnection();
			connection.setAutoCommit(false);
			// 先删除该条记录的上传附件
			Provider provider = providerDao.getProviderById(connection, String.valueOf(id));
			if (provider.getCompanyLicPicPath() != null && !provider.getCompanyLicPicPath().equals("")) {
				// 删除服务器上个人证件照
				File file = new File(provider.getCompanyLicPicPath());
				if (file.exists())
					flag = file.delete();
			}
			if (flag && provider.getOrgCodePicPath() != null && !provider.getOrgCodePicPath().equals("")) {
				// 删除服务器上个人工作证照片
				File file = new File(provider.getOrgCodePicPath());
				if (file.exists())
					flag = file.delete();
			}
			if (providerDao.delete(connection, id) > 0)
				flag = true;
			connection.commit();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			try {
				connection.rollback();
			} catch (Exception e2) {
				// TODO: handle exception
				e2.printStackTrace();
			}
		} finally {
			BaseDao.closeResource(connection, null, null);
		}
		return flag;
	}
}
