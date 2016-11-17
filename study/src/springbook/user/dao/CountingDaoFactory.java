package springbook.user.dao;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CountingDaoFactory {
	@Bean
	public UserDao userDao() {
		UserDao userDao = new UserDao();
		userDao.setConnectionMaker(connectionMaker());
		return userDao;
	}
	
	@Bean
	public AccountDao accountDao() {
		ConnectionMaker connectionMaker = connectionMaker();
		AccountDao accountDao = new AccountDao(connectionMaker);
		return accountDao;
	}
	
	@Bean
	public MessageDao messageDao() {
		ConnectionMaker connectionMaker = connectionMaker();
		MessageDao messageDao = new MessageDao(connectionMaker);
		return messageDao;
	}
	
	@Bean
	public ConnectionMaker connectionMaker() {
		return new CountingConnectionMaker(realConnectionMaker());
	}
	
	public ConnectionMaker realConnectionMaker() {
		return new DConnectionMaker();
	}
}
