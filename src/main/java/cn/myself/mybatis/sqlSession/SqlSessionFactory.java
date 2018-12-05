package cn.myself.mybatis.sqlSession;

/**
 * sqlSession工厂接口
 * @author nzp
 *
 */
public interface SqlSessionFactory {

	/*
	 * 生成sqlSession对象
	 */
	public abstract SqlSession openSession();
}
