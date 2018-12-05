package cn.myself.mybatis.sqlSession.defaults;

import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import cn.myself.mybatis.mappers.Mapper;
import cn.myself.mybatis.proxy.ProxyMethodInvocationHandler;
import cn.myself.mybatis.sqlSession.SqlSession;

/**
 * sqlSession接口的默认实现
 * 
 * @author nzp
 *
 */
public class DefaultSqlSession implements SqlSession {

	// 能够连接数据库 并且 映射信息即执行sql
	// 连接
	private DataSource dataSource;// 如果配置文件中配置了数据源后 使用
	private Connection connection;// 如果配置文件中没有配置数据源 使用
	// 映射文件信息 key唯一标识 value是映射的内容<执行的语句和封装的结果>
	private Map<String, Mapper> mappers = new HashMap<>();// 存储映射信息

	/*
	 * 获得dao的代理对象
	 */
	@Override
	public <T> T getMapper(Class<T> daoClass) {
		
		try {
			//调用这个方法传入被代理对象接口的字节码对象，获得目标对象的代理对象Object后强转成目标对象，
			//之后在调用接口对象(实际是代理对象)的方法执行。
			//1.使用jdk的动态代理
			return (T)Proxy.newProxyInstance(daoClass.getClassLoader(), 
									new Class[] {daoClass}, 
									new ProxyMethodInvocationHandler(getConnection(), mappers));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		
	}

	@Override
	public void close() {
		try {
			if (connection != null) {
				connection.close();
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public DataSource getDataSource() {
		return dataSource;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	public Connection getConnection() throws SQLException {
		if (dataSource != null) {
			connection = dataSource.getConnection();
		}
		return connection;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	public Map<String, Mapper> getMappers() {
		return mappers;
	}

	public void setMappers(Map<String, Mapper> mappers) {
		this.mappers.putAll(mappers);
	}

}
