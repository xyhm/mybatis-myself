package cn.myself.mybatis.sqlSession.defaults;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import cn.myself.mybatis.annotations.Select;
import cn.myself.mybatis.io.Resources;
import cn.myself.mybatis.mappers.Mapper;
import cn.myself.mybatis.sqlSession.SqlSession;
import cn.myself.mybatis.sqlSession.SqlSessionFactory;

/**
 * sqlSessionFactory接口的默认实现
 * @author admin
 *
 */
public class DefaultSqlSessionFactory implements SqlSessionFactory {

	//定义配置信息的流对象
	private InputStream config;
	//数据库连接信息
	private String driver;
	private String url;
	private String username;
	private String password;
	
	/*
	 * 给流对象赋值
	 */
	public void setConfig(InputStream config) {
		this.config = config;
	}
	
	/*
	 * 创建sqlsession
	 */
	@Override
	public SqlSession openSession() {
		
		//1.创建默认的sqlSession对象
		DefaultSqlSession sqlSession = new DefaultSqlSession();
		//2.解析主配置xml文件为sqlSession填充必要的信息
		loadConfigration(sqlSession);
		//3.返回
		return sqlSession;
	}

	/*
	 * 解析xml,读取配置文件内容，sqlSession填充必要的信息
	 */
	private void loadConfigration(DefaultSqlSession sqlSession) {
		try {
			//1.使用dom4j解析xml
			Document doucment = new SAXReader().read(config);
			//2.获取根节点
			Element root = doucment.getRootElement();
			//3.使用xPath解析得到所有property
			List<Element> propElements = root.selectNodes("//dataSource/property");
			//4.遍历并赋值给数据库信息
			for (Element propElement : propElements) {
				String name = propElement.attributeValue("name");
				String value = propElement.attributeValue("value");
				if ("driver".equalsIgnoreCase(name)) {
					driver = value;
				}
				if ("url".equalsIgnoreCase(name)) {
					url = value;
				}
				if ("username".equalsIgnoreCase(name)) {
					username = value;
				}
				if ("password".equalsIgnoreCase(name)) {
					password = value;
				}
			}
			//5.判断是否使用数据源
			Node node = root.selectSingleNode("//environment/dataSource");
			String valueOf = node.valueOf("@type");
			if ("POOLED".equalsIgnoreCase(valueOf)) {
				//使用数据源
				DataSource ds = createDateSource();
				//6.给sqlSession的连接属性赋值
				sqlSession.setDataSource(ds);
			} else if ("UNPOOLED".equalsIgnoreCase(valueOf)) {
				//不使用数据源
				Connection connection = createConnection();
				//6.给sqlSession的连接属性赋值
				sqlSession.setConnection(connection);
			} else {
				System.out.println("不支持的数据源!!");
			}
			Map<String, Mapper> mappers = null;
			//6.判断mapper使用注解还是xml方式
			List<Element> mapperElements = root.selectNodes("//mappers/mapper");
			for (Element mapperElement : mapperElements) {
				Attribute resource = mapperElement.attribute("resource");
				if (null != resource) {
					//xml方式
					String mapperPath = resource.getValue();//cn/itcast/dao/IUserDao.xml
					//7.加载mapper进行解析并返回mapper
					mappers = loadXMLMapperConfig(mapperPath);
				} else {//判断是class和url
					System.out.println("注解方式");
					//注解方式
					String daoClassPath = mapperElement.attributeValue("class");
					//7.加载mapper进行解析并返回mapper
					mappers = loadAnnotationMapperConfig(daoClassPath);
				}
				//8.为sqlSession的映射信息属性赋值
				sqlSession.setMappers(mappers);
			}
			
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	/*
	 * xml方式：加载mapper映射文件进行配置
	 * 解析映射配置文件 例如：IUserDao.xml
	 * 准备sqlSession必须的映射文件信息
	 * 参数：cn/itcast/dao/IUserDao.xml
	 */
	private Map<String, Mapper> loadXMLMapperConfig(String mapperPath) throws Exception {
		
		Map<String, Mapper> map = new HashMap<>();
		InputStream resourceAsStream = null;
		try {
			//1.根据mapper路径获取输入流
			resourceAsStream = Resources.getResourceAsStream(mapperPath);
			//2.获取Document对象
			Document document = new SAXReader().read(resourceAsStream);
			//3.获取根节点
			Element root = document.getRootElement();
			//4.得到namespace
			String namespace = root.attributeValue("namespace");
			//5.取出所有select节点
			Mapper mapper = null;
			String key = null;
			List<Element> selectElements = root.elements("select");
			for (Element selectElement : selectElements) {
				String id = selectElement.attributeValue("id");
				String resultType = selectElement.attributeValue("resultType");
				String selectSql = selectElement.getText();
				//6.创建map的value：创建mapper对象 
				mapper = new Mapper();
				mapper.setQueryString(selectSql);
				mapper.setResultType(resultType);
				//7.拼接map的key
				key = namespace.concat(".").concat(id);
				//8.放入map中
				map.put(key, mapper);
			}
			
			return map;
		} finally {
			resourceAsStream.close();
		}
	}
	
	/*
	 * 注解方式：解析配置
	 */
	private Map<String, Mapper> loadAnnotationMapperConfig(String daoClassPath) throws Exception {
		Map<String, Mapper> map = new HashMap<>();
		//1.根据路径获取class字节码对象
		Class daoClass = Class.forName(daoClassPath);
		String calssName = daoClass.getName();
		//2.获取其中所有方法
		Method[] methods = daoClass.getMethods();
		//3.遍历判断是否有指定注解
		for (Method method : methods) {
			boolean isAnnotation = method.isAnnotationPresent(Select.class);
			if (!isAnnotation) {
				continue;
			} 
			String mathodName = method.getName();
			//4.得到map中的key
			String key = calssName.concat(".").concat(mathodName);
			Select select = method.getAnnotation(Select.class);
			//5.得到map中的value 即mapper
			Mapper mapper = new Mapper();
			//得到sql语句
			String querySql = select.value();
			mapper.setQueryString(querySql);
			//得到返回值类型全限定名称
			Type type = method.getGenericReturnType();
			if (type instanceof ParameterizedType) {
				ParameterizedType ptype = (ParameterizedType) type;
				Type[] types = ptype.getActualTypeArguments();
				Class domainClass = (Class) types[0];
				//给mapper中的resultType赋值
				mapper.setResultType(domainClass.getName());
			}
			//6.添加到map
			map.put(key, mapper);
		}
		//7.返回
		return map;
	}

	/*
	 * 创建连接对象
	 */
	private Connection createConnection() throws Exception {
		Class.forName(driver);
		return DriverManager.getConnection(url, username, password);
	}

	/*
	 * 创建数据源对象
	 */
	private DataSource createDateSource() throws Exception  {
		//由于jar冲突 不使用这个方式
		return null;
	}
	

}
