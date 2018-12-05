package cn.myself.mybatis.sqlSession;

/**
 * sqlSession接口
 * @author nzp
 *
 */
public interface SqlSession {

	/*
	 * 获取dao代理对象
	 */
	public abstract <T> T getMapper(Class<T> daoClass);
	
	/*
	 * 关闭资源
	 */
	public abstract void close();
}
