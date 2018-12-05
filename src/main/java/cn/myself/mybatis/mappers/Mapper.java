package cn.myself.mybatis.mappers;

/**
 * 映射配置信息
 * @author nzp
 *
 */
public class Mapper {

	private String queryString;//sql语句
	private String resultType;//封装的结果类型全限定类名
	
	public String getQueryString() {
		return queryString;
	}
	public void setQueryString(String queryString) {
		this.queryString = queryString;
	}
	public String getResultType() {
		return resultType;
	}
	public void setResultType(String resultType) {
		this.resultType = resultType;
	}
	
}
