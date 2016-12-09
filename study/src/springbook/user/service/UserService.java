package springbook.user.service;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import springbook.dao.UserDao;
import springbook.user.domain.Level;
import springbook.user.domain.User;
import springbook.user.domain.UserLevelUpgradePolicy;

public class UserService {
	UserDao userDao;
	UserLevelUpgradePolicy userLevelUpgradePolicy;
	PlatformTransactionManager transactionManager;
	
	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}
	
	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}
	
	public void setUserLevelUpgradePolicy(UserLevelUpgradePolicy userLevelUpgradePolicy) {
		this.userLevelUpgradePolicy = userLevelUpgradePolicy;
	}
	
	public void upgradeLevels() throws Exception {
		TransactionStatus status = this.transactionManager.getTransaction(new DefaultTransactionDefinition());
		try {
			List<User> users = userDao.getAll();
			for(User user : users) {
				if(userLevelUpgradePolicy.canUpgradeLevel(user)) {
					userLevelUpgradePolicy.upgradeLevel(user);
				}	
			}
			this.transactionManager.commit(status);
		} catch(Exception e) {
			this.transactionManager.rollback(status);
			throw e;
		} finally {
			
		}
		
	}

	public void add(User user) {
		if(user.getLevel() == null) {
			user.setLevel(Level.BASIC);
		}
		
		userDao.add(user);
	}
	
//	private boolean canUpgradeLevel(User user) {
//		Level currentLevel = user.getLevel();
//		switch(currentLevel) {
//			case BASIC: return (user.getLogin() >= MIN_LOGCOUNT_FOR_SILVER);
//			case SILVER: return (user.getRecommend() >= MIN_RECOMMEND_FOR_GOLD);
//			case GOLD: return false;
//			default: throw new IllegalArgumentException("Unknown Level: " + currentLevel);
//		}
//	}
//	
//	private void upgradeLevel(User user) {
//		user.upgradeLevel();
//		userDao.update(user);
//	}
}
