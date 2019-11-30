package cn.smbms.service.user;

import java.util.List;

import cn.smbms.pojo.User;

/**
 * @author 王业龙
 * @version 创建时间：2019年11月21日 下午4:15:46
 * @ClassName 类名称
 * @Description 类描述
 */
public interface UserService {
	/**
	 * 增加用户信息
	 * 
	 * @param user
	 * @return
	 */
	public boolean add(User user);

	/**
	 * 用户登录
	 * 
	 * @param userCode
	 * @param userPassword
	 * @return
	 */
	public User login(String userCode, String userPassword);

	/**
	 * 根据条件查询用户列表
	 * 
	 * @param queryUserName
	 * @param queryUserRole
	 * @return
	 */
	public List<User> getUserList(String queryUserName, Integer queryUserRole, Integer currentPageNo, Integer pageSize);

	/**
	 * 根据条件查询用户表记录数
	 * 
	 * @param queryUserName
	 * @param queryUserRole
	 * @return
	 */
	public int getUserCount(String queryUserName, Integer queryUserRole);

	/**
	 * 根据userCode查询出User
	 * 
	 * @param userCode
	 * @return
	 */
	public User selectUserCodeExist(String userCode);

	/**
	 * 根据ID删除user
	 * 
	 * @param delId
	 * @return
	 */
	public boolean deleteUserById(Integer delId);

	/**
	 * 根据ID查找user
	 * 
	 * @param id
	 * @return
	 */
	public User getUserById(String id);

	/**
	 * 修改用户信息
	 * 
	 * @param user
	 * @return
	 */
	public boolean modify(User user);

	/**
	 * 根据userId修改密码
	 * 
	 * @param id
	 * @param pwd
	 * @return
	 */
	public boolean updatePwd(Integer id, String pwd);
}
