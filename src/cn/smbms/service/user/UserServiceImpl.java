package cn.smbms.service.user;

import java.io.File;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import cn.smbms.dao.BaseDao;
import cn.smbms.dao.user.UserDao;
import cn.smbms.pojo.User;

/**
 * @author 王业龙
 * @version 创建时间：2019年11月21日 下午4:20:36
 * @ClassName 类名称
 * @Description 类描述service层捕获异常，进行事务处理
 *              事务处理：调用不同dao的多个方法，必须使用同一个connection（connection作为参数传递）
 *              事务完成之后，需要在service层进行connection的关闭，在dao层关闭（PreparedStatement和ResultSet对象）
 */
@Service
public class UserServiceImpl implements UserService {
	@Resource
	private UserDao userDao;

	@Override
	public boolean add(User user) {
		// TODO Auto-generated method stub
		boolean flag = false;
		Connection connection = null;
		try {
			connection = BaseDao.getConnection();
			connection.setAutoCommit(false);// 开启JDBC事务管理
			int updateRows = userDao.add(connection, user);
			if (updateRows > 0) {
				flag = true;
				System.out.println("add success!");
			} else {
				System.out.println("add failed!");
			}
			connection.commit();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			try {
				System.out.println("rollback===============");
				connection.rollback();
			} catch (SQLException e2) {
				// TODO: handle exception
				e2.printStackTrace();
			} finally {
				// 在service层进行connection连接的关闭
				BaseDao.closeResource(connection, null, null);
			}
		}
		return flag;
	}

	@Override
	public User login(String userCode, String userPassword) {
		// TODO Auto-generated method stub
		Connection connection = null;
		User user = null;
		try {
			connection = BaseDao.getConnection();
			user = userDao.getLoginUser(connection, userCode);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			BaseDao.closeResource(connection, null, null);
		}

		// 匹配密码
		if (null != user) {
			if (!user.getUserPassword().equals(userPassword))
				user = null;
		}
		return user;
	}

	@Override
	public List<User> getUserList(String queryUserName, Integer queryUserRole, Integer currentPageNo,
			Integer pageSize) {
		// TODO Auto-generated method stub
		Connection connection = null;
		List<User> userList = null;
		System.out.println("queryUserName ---- > " + queryUserName);
		System.out.println("queryUserRole ---- > " + queryUserRole);
		System.out.println("currentPageNo ---- > " + currentPageNo);
		System.out.println("pageSize ---- > " + pageSize);
		try {
			connection = BaseDao.getConnection();
			userList = userDao.getUserList(connection, queryUserName, queryUserRole, currentPageNo, pageSize);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			BaseDao.closeResource(connection, null, null);
		}
		return userList;
	}

	@Override
	public int getUserCount(String queryUserName, Integer queryUserRole) {
		// TODO Auto-generated method stub
		Connection connection = null;
		int count = 0;
		System.out.println("queryUserName ---- > " + queryUserName);
		System.out.println("queryUserRole ---- > " + queryUserRole);
		try {
			connection = BaseDao.getConnection();
			count = userDao.getUserCount(connection, queryUserName, queryUserRole);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			BaseDao.closeResource(connection, null, null);
		}
		return count;
	}

	@Override
	public User selectUserCodeExist(String userCode) {
		// TODO Auto-generated method stub
		Connection connection = null;
		User user = null;
		try {
			connection = BaseDao.getConnection();
			user = userDao.getLoginUser(connection, userCode);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			BaseDao.closeResource(connection, null, null);
		}
		return user;
	}

	@Override
	public boolean deleteUserById(Integer delId) {
		// TODO Auto-generated method stub
		Connection connection = null;
		boolean flag = true;
		try {
			connection = BaseDao.getConnection();
			connection.setAutoCommit(false);
			// 先删除该条记录的上传附件
			User user = userDao.getUserById(connection, String.valueOf(delId));
			if (user.getIdPicPath() != null && !user.getIdPicPath().equals("")) {
				// 删除服务器上个人证件照
				File file = new File(user.getIdPicPath());
				if (file.exists())
					flag = file.delete();
			}
			if (flag && user.getWorkPicPath() != null && !user.getWorkPicPath().equals("")) {
				// 删除服务器上个人工作证照片
				File file = new File(user.getWorkPicPath());
				if (file.exists())
					flag = file.delete();
			}
			if (userDao.deleteUserById(connection, delId) > 0)
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

	@Override
	public User getUserById(String id) {
		// TODO Auto-generated method stub
		User user = null;
		Connection connection = null;
		try {
			connection = BaseDao.getConnection();
			user = userDao.getUserById(connection, id);
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
			user = null;
		} finally {
			BaseDao.closeResource(connection, null, null);
		}
		return user;
	}

	@Override
	public boolean modify(User user) {
		// TODO Auto-generated method stub
		Connection connection = null;
		boolean flag = false;
		try {
			connection = BaseDao.getConnection();
			if (userDao.modify(connection, user) > 0)
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
	public boolean updatePwd(Integer id, String pwd) {
		// TODO Auto-generated method stub
		boolean flag = false;
		Connection connection = null;
		try {
			connection = BaseDao.getConnection();
			if (userDao.updatePwd(connection, id, pwd) > 0)
				flag = true;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			BaseDao.closeResource(connection, null, null);
		}
		return flag;
	}

}
