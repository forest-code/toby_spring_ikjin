package springbook.user.service;

import java.util.List;

import org.springframework.mail.MailSender;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import springbook.dao.UserDao;
import springbook.user.domain.Level;
import springbook.user.domain.User;
import springbook.user.domain.UserLevelUpgradePolicy;

//@Transactional
public class UserServiceImpl implements UserService{
	UserDao userDao;
	UserLevelUpgradePolicy userLevelUpgradePolicy;
	MailSender mailSender;
	
	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}
	
	public void setUserLevelUpgradePolicy(UserLevelUpgradePolicy userLevelUpgradePolicy) {
		this.userLevelUpgradePolicy = userLevelUpgradePolicy;
	}

	public void setMailSender(MailSender mailSender) {
		this.mailSender = mailSender;
	}
	
	public void upgradeLevels() {
		try {
			List<User> users = userDao.getAll();
			for(User user : users) {
				if(userLevelUpgradePolicy.canUpgradeLevel(user)) {
					user = userLevelUpgradePolicy.upgradeLevel(user);

					userDao.update(user);
					sendUpgradeEmail(user);
				}	
			}
		} catch(Exception e) {
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
	protected void sendUpgradeEmail(User user) {
		SimpleMailMessage mailMessage = new SimpleMailMessage();
		mailMessage.setTo(user.getEmail());
		mailMessage.setFrom("useradmin@ksug.org");
		mailMessage.setSubject("Upgrade 안내");
		mailMessage.setText("사용자님의 등급이 " + user.getLevel().name());
		
		this.mailSender.send(mailMessage);
	}

	@Override
	public User get(String id) {
		return userDao.get(id);
	}

	@Override
	public List<User> getAll() {
		return userDao.getAll();
	}

	@Override
	public void deleteAll() {
		userDao.deleteAll();
	}

	@Override
	public void update(User user) {
		userDao.update(user);
	}
}
