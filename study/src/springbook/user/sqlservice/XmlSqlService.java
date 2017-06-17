package springbook.user.sqlservice;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import springbook.dao.UserDao;
import springbook.user.sqlservice.jaxb.SqlType;
import springbook.user.sqlservice.jaxb.Sqlmap;

public class XmlSqlService implements SqlService, SqlRegistry, SqlReader {
	private Map<String, String> sqlMap = new HashMap<String, String>();
	private String sqlmapFile;
	private SqlReader sqlReader;
	private SqlRegistry sqlRegistry;
	
	public void setSqlReader(SqlReader sqlReader) {
		this.sqlReader = sqlReader;
	}
	
	public void setSqlRegisry(SqlRegistry sqlRegistry) {
		this.sqlRegistry = sqlRegistry;
	}
	
	public XmlSqlService() {
//		String contextPath = Sqlmap.class.getPackage().getName();
//		try {
//			JAXBContext context = JAXBContext.newInstance(contextPath);
//			Unmarshaller unmarshaller = context.createUnmarshaller();
//			InputStream is = UserDao.class.getResourceAsStream("sqlmap.xml");
//			Sqlmap sqlmap = (Sqlmap)unmarshaller.unmarshal(is);
//			
//			for(SqlType sql : sqlmap.getSql()) {
//				sqlMap.put(sql.getKey(), sql.getValue());
//			}
//		} catch (JAXBException e) {
//			throw new RuntimeException(e);
//		}
	}

	@Override
	public String getSql(String key) throws SqlRetrievalFailureException {
//		String sql = sqlMap.get(key);
//		if(sql == null) throw new SqlRetrievalFailureException(key + "를 이용해서 SQL을 찾을 수 없습니다");
//		else return sql;
		try {
			return this.sqlRegistry.findSql(key);
		} catch(SqlNotFoundException e) {
			throw new SqlRetrievalFailureException(e);
		}
	}

	public void setSqlmapFile(String sqlmapFile) {
		this.sqlmapFile = sqlmapFile;
	}
	
	@PostConstruct
	public void loadSql() {
//		String contextPath = Sqlmap.class.getPackage().getName();
//		try {
//			JAXBContext context = JAXBContext.newInstance(contextPath);
//			Unmarshaller unmarshaller = context.createUnmarshaller();
//			InputStream is = UserDao.class.getResourceAsStream(this.sqlmapFile);
//			Sqlmap sqlmap = (Sqlmap)unmarshaller.unmarshal(is);
//			
//			for(SqlType sql : sqlmap.getSql()) {
//				sqlMap.put(sql.getKey(), sql.getValue());
//			}
//		} catch (JAXBException e) {
//			throw new RuntimeException(e);
//		}
		this.sqlReader.read(this.sqlRegistry);
	}
	
	@Override
	public String findSql(String key) throws SqlNotFoundException {
		String sql = sqlMap.get(key);
		if (sql == null) throw new SqlNotFoundException(key + "에 대한 SQL을 찾을 수 없습니다.");
		else return sql;
	}
	
	@Override
	public void registerSql(String key, String sql) {
		sqlMap.put(key, sql);
	}
	
	@Override
	public void read(SqlRegistry sqlRegistry) {
		String contextPath = Sqlmap.class.getPackage().getName();
		try {
			JAXBContext context = JAXBContext.newInstance(contextPath);
			Unmarshaller unmarshaller = context.createUnmarshaller();
			InputStream is = UserDao.class.getResourceAsStream(this.sqlmapFile);
			Sqlmap sqlmap = (Sqlmap)unmarshaller.unmarshal(is);
			
			for(SqlType sql : sqlmap.getSql()) {
				sqlRegistry.registerSql(sql.getKey(), sql.getValue());
			}
		} catch (JAXBException e) {
			throw new RuntimeException(e);
		}
	}
}
