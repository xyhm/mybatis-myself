package cn.nzp.test;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.junit.Test;

import cn.myself.mybatis.io.Resources;
import cn.myself.mybatis.sqlSession.SqlSession;
import cn.myself.mybatis.sqlSession.SqlSessionFactory;
import cn.myself.mybatis.sqlSession.SqlSessionFactoryBuilder;
import cn.nzp.dao.IUserDao;
import cn.nzp.domain.User;

/**
 * mybatis框架环境测试类
 * @author admin
 *
 */
public class MybatisTest {

	/**
	 步骤：
	 	1.得到mybatis的流对象读取主配置文件
	 	2.使用构建者模式构建SqlSessionFactory工厂
	 	3.使用工厂生产sqlSession对象
	 	4.使用sqlSession对象创建dao接口的代理对象
	 	5.使用dao接口的代理对象执行查询所有方法
	 	6.释放资源
	 * @throws IOException 
	 */
	@Test
	public void testFindAll() throws IOException {
		InputStream resourceStream = Resources.getResourceAsStream("sqlMapConfig.xml");
		SqlSessionFactory factory = new SqlSessionFactoryBuilder().build(resourceStream);
		SqlSession sqlSession = factory.openSession();
		IUserDao userDao = sqlSession.getMapper(IUserDao.class);
		List<User> userList = userDao.selectAllUser();
		if (null != userList && !userList.isEmpty()) {
			for (User user : userList) {
				System.out.println(user);
			}
		}
		sqlSession.close();
		resourceStream.close();
	}
}
