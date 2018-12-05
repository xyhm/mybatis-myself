package cn.nzp.dao;

import java.util.List;

import cn.myself.mybatis.annotations.Select;
import cn.nzp.domain.User;

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
	@Select("select * from user")
	public abstract List<User> selectAllUser();
}
