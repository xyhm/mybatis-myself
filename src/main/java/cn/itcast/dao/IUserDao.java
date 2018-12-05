package cn.itcast.dao;

import java.util.List;

import cn.itcast.domain.User;

/**
 * 数据访问层接口
 * @author admin
 *
 */
public interface IUserDao {
	
	/**
	 * 查询所有用户信息
	 * @return
	 */
	public abstract List<User> selectAllUser();
}
