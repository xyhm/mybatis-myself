package cn.myself.mybatis.sqlSession;

import java.io.InputStream;

import cn.myself.mybatis.sqlSession.defaults.DefaultSqlSessionFactory;

/**
 * sqlSession工厂构建
 * @author nzp
 *
 */
public class SqlSessionFactoryBuilder {

	/*
	 * 使用主配置文件字节输入流构建sqlSessionFactory工厂对象
	 */
	public SqlSessionFactory build(InputStream in) {
		//1.创建工厂对象
		DefaultSqlSessionFactory factory = new DefaultSqlSessionFactory();
		//2.给factory对象中的流对象赋值
		factory.setConfig(in);
		//3.返回
		return factory;
	}
}
