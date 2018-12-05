package cn.myself.mybatis.proxy;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.mysql.jdbc.util.PropertiesDocGenerator;

import cn.myself.mybatis.mappers.Mapper;

/**
 * 实现代理dao的增强部分
 * @author nzp
 *
 */
public class ProxyMethodInvocationHandler implements InvocationHandler {

	private Connection connection;
	private Map<String, Mapper> mappers;
	
	public ProxyMethodInvocationHandler(Connection connection, Map<String, Mapper> mappers) {
		this.connection = connection;
		this.mappers = mappers;
	}
	
	/**
	 * 执行被代理对象的任何方法都会经过该方法
	 * 此方法要取出映射配置中的内容 并 执行sql语句
	 */
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		PreparedStatement pstm = null;
		ResultSet rs = null;
		try {
			//1.取出map的key
			String methodName = method.getName();
			String className = method.getDeclaringClass().getName();
			String key = className.concat(".").concat(methodName);
			System.out.println(key);
			//2.取出map的value
			Mapper mapper = mappers.get(key);
			String querySql = mapper.getQueryString();
			String resultType = mapper.getResultType();
			//3.获取数据库的语句对象
			pstm = connection.prepareStatement(querySql);
			//4.执行sql
			rs = pstm.executeQuery();
			//5.调用封装的方法
			return handle(rs, resultType);
		} finally {
			rs.close();
			pstm.close();
		}
	}

	/**
	 * 封装结果集
	 * @param rs
	 * @param resultType
	 * @return
	 * @throws ClassNotFoundException 
	 */
	private <E> List<E> handle(ResultSet rs, String resultType) throws Exception {
		List list = new ArrayList<>();
		//1.获取resultType所对应的字节码
		Class domainClass = Class.forName(resultType);
		//3.遍历结果集
		while (rs.next()) {
			//2.获取要封装的实体类对象
			Object domain = domainClass.newInstance();
			//取出rs中的源信息 多少列和名称等等
			ResultSetMetaData rsmd = rs.getMetaData();
			//得到当前的列
			int columnCount = rsmd.getColumnCount();
			for (int i=1; i<columnCount; i++) {
				//取出每列的名称 - 实体类中的属性名
				String columnName = rsmd.getColumnName(i);
				//得到当前列名的属性描述器
				PropertyDescriptor pd = new PropertyDescriptor(columnName, domainClass);
				//得到他的写方法
				Method writeMethod = pd.getWriteMethod();
				//取出要赋的值
				Object value = rs.getObject(columnName);
				//方法执行
				writeMethod.invoke(domain, value);
			}
			//把封装的domain加入到集合中
			list.add(domain);	
		}
		return list;
	}

}
