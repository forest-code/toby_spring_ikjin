package springbook.user.sqlservice;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.oxm.Unmarshaller;

import springbook.dao.UserDao;
import springbook.user.sqlservice.jaxb.SqlType;
import springbook.user.sqlservice.jaxb.Sqlmap;

public class OxmSqlService implements SqlService {
	private final BaseSqlService baseSqlService = new BaseSqlService();
	
	private final OxmSqlReader oxmSqlReader= new OxmSqlReader();
	protected SqlRegistry sqlRegistry = new HashMapSqlRegistry();
	
	public void setSqlmap(Resource sqlmap) {
		this.oxmSqlReader.setSqlmap(sqlmap);
	}
	
	private class OxmSqlReader implements SqlReader {
		private Unmarshaller unmarshaller;
		private final static String DEFAULT_SQLMAP_FILE = "sqlmap.xml";
		private String sqlmapFile = DEFAULT_SQLMAP_FILE;
		
		private Resource sqlmap = new ClassPathResource("sqlmap.xml",UserDao.class);
		
		public void setSqlmap(Resource sqlmap) {
			this.sqlmap = sqlmap;
		}
		
		public void setUnmarshaller(Unmarshaller unmarshaller) {
			this.unmarshaller = unmarshaller;
		}
		
		public void setSqlmapFile(String sqlmapFile) {
			this.sqlmapFile = sqlmapFile;
		}
		
		@Override
		public void read(SqlRegistry sqlRegistry) {
			try {
				Source source = new StreamSource(sqlmap.getInputStream());
				Sqlmap sqlmap = (Sqlmap)this.unmarshaller.unmarshal(source);
				
				for(SqlType sql : sqlmap.getSql()) {
					sqlRegistry.registerSql(sql.getKey(), sql.getValue());
				}
			} catch (IOException e) {
				throw new IllegalArgumentException(this.sqlmap.getFilename() + "을 가져올 수 없습니다", e);
			}
			
		}
	}
	
	public void setUnmarshaller(Unmarshaller unmarshaller) {
		this.oxmSqlReader.setUnmarshaller(unmarshaller);
	}
	
	public void setSqlmapFile(String sqlmapFile) {
		this.oxmSqlReader.setSqlmapFile(sqlmapFile);
	}
	
	public void setSqlRegistry(SqlRegistry sqlRegistry) { this.sqlRegistry = sqlRegistry; }

	@PostConstruct
	public void loadSql() {
//		this.oxmSqlReader.read(this.sqlRegistry);
		this.baseSqlService.setSqlReader(this.oxmSqlReader);
		this.baseSqlService.setSqlRegistry(this.sqlRegistry);
		
		this.baseSqlService.loadSql();
	}

	@Override
	public String getSql(String key) throws SqlRetrievalFailureException {
//		try { return this.sqlRegistry.findSql(key); }
//		catch(SqlNotFoundException e) { throw new SqlRetrievalFailureException(e); }
		return this.baseSqlService.getSql(key);
	}

}
