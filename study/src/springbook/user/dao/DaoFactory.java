package springbook.user.dao;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DaoFactory {
	@Bean
	public UserDao userDao() {
		ConnectionMaker connectionMaker = connectionMaker();
		UserDao userDao = new UserDao(connectionMaker);
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
		return new DConnectionMaker();
	}
}
