package cn.myself.mybatis.io;

import java.io.InputStream;

/**
 * mybatis-myself资源获取类
 * @author nzp
 *
 */
public class Resources {

	/*
	 * 获取主配置文件流对象
	 */
	public static InputStream getResourceAsStream(String config) {
		if (null == config || "".equals(config.trim())) {
			throw new NullPointerException("主配置文件路径为空!!");
		}
		//加载
		InputStream resourceStream = Resources.class.getClassLoader().getResourceAsStream(config);
		
		return resourceStream;
	}
}
