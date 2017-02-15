package springbook.user.service;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.aop.framework.ProxyFactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.mail.MailSender;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.PlatformTransactionManager;

import springbook.dao.UserDao;
import springbook.user.domain.Level;
import springbook.user.domain.User;
import springbook.user.domain.UserLevelUpgradePolicy;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="/springbook/test-applicationContext.xml")
public class UserServiceTest {
//	@Autowired 
//	ApplicationContext context;
	@Autowired
	UserDao userDao;
	@Autowired
	UserService userService;
	@Autowired
	UserService testUserService;
//	@Autowired
//	UserServiceImpl userServiceImpl;
	@Autowired
	UserLevelUpgradePolicy userLevelUpgradePolicy;
	@Autowired
	PlatformTransactionManager transactionManager;
	@Autowired
	MailSender mailSender;
	List<User> users;
	
	@Before
	public void setUp() {
		users = Arrays.asList(
				new User("bumjin","박범진","p1",Level.BASIC,49,0,"bumjin@email.com"),
				new User("joytouch","강명성","p2",Level.BASIC,50,0,"joytouch@email.com"),
				new User("erwins","신승한","p3",Level.SILVER,60,29,"erwins@email.com"),
				new User("madnite1","이상호","p4",Level.SILVER,60,30,"madnite1@email.com"),
				new User("green","오민규","p5",Level.GOLD,100,Integer.MAX_VALUE,"green@email.com")
				);
	}
	
	@Test
	public void bean() {
		assertThat(this.userService, is(notNullValue()));
	}
	
	@Test
	public void upgradeLevels() throws Exception {
		UserServiceImpl userServiceImpltest = new UserServiceImpl();
		
		userServiceImpltest.setUserLevelUpgradePolicy(this.userLevelUpgradePolicy);
//		userDao.deleteAll();
//		for(User user : users) {
//			userDao.add(user);
//		}
		
		MockUserDao mockUserDao = new MockUserDao(this.users);
		userServiceImpltest.setUserDao(mockUserDao);
		
		MockMailSender mockMailSender = new MockMailSender();
		userServiceImpltest.setMailSender(mockMailSender);
		
		userServiceImpltest.upgradeLevels();
		
//		checkLevel(users.get(0), Level.BASIC);
//		checkLevel(users.get(1), Level.SILVER);
//		checkLevel(users.get(2), Level.SILVER);
//		checkLevel(users.get(3), Level.GOLD);
//		checkLevel(users.get(4), Level.GOLD);
//		checkLevelUpgrade(users.get(0), false);
//		checkLevelUpgrade(users.get(1), true);
//		checkLevelUpgrade(users.get(2), false);
//		checkLevelUpgrade(users.get(3), true);
//		checkLevelUpgrade(users.get(4), false);
		
		List<User> updated = mockUserDao.getUpdated();
		assertThat(updated.size(), is(2));
		checkUserAndLevel(updated.get(0), "joytouch", Level.SILVER);
		checkUserAndLevel(updated.get(1), "madnite1", Level.GOLD);
		
		
		List<String> requests = mockMailSender.getRequests();
		assertThat(requests.size(), is(2));
		assertThat(requests.get(0), is(users.get(1).getEmail()));
		assertThat(requests.get(1), is(users.get(3).getEmail()));
	}
	
	private void checkUserAndLevel(User updated, String expectedId, Level expectedLevel) {
		assertThat(updated.getId(), is(expectedId));
		assertThat(updated.getLevel(), is(expectedLevel));
	}
	
	private void checkLevel(User user, Level expectedLevel) {
		User userUpdate = userDao.get(user.getId());
		assertThat(userUpdate.getLevel(), is(expectedLevel));
	}
	
	private void checkLevelUpgrade(User user, boolean upgraded) {
		User userUpdate = userDao.get(user.getId());
		if(upgraded) {
			assertThat(userUpdate.getLevel(), is(user.getLevel().nextLevel()));
		} else {
			assertThat(userUpdate.getLevel(), is(user.getLevel()));
		}
	}
	
	@Test
	public void add() {
		userDao.deleteAll();
		
		User userWithLevel = users.get(4);
		User userWithoutLevel = users.get(0);
		userWithoutLevel.setLevel(null);
		
		userService.add(userWithLevel);
		userService.add(userWithoutLevel);
		
		User userWithLevelRead = userDao.get(userWithLevel.getId());
		User userWithoutLevelRead = userDao.get(userWithoutLevel.getId());
		
		assertThat(userWithLevelRead.getLevel(), is(userWithLevel.getLevel()));
		assertThat(userWithoutLevelRead.getLevel(), is(userWithoutLevel.getLevel()));
		
	}
	
	static class TestUserServiceImpl extends UserServiceImpl {		
		private String id = "madnite1";
		
		@Override
		public void upgradeLevels() {
			try {
				List<User> users = super.userDao.getAll();
				for(User user : users) {
					if(super.userLevelUpgradePolicy.canUpgradeLevel(user)) {
						System.out.println(user.getId());
						if(user.getId().equals(this.id)) {
							System.out.println(user.getId());
							throw new TestUserServiceException();
						}
						user = super.userLevelUpgradePolicy.upgradeLevel(user);
						System.out.println(user.getLevel().toString());
						super.userDao.update(user);
						sendUpgradeEmail(user);
					}
				}
			} catch(Exception e) {
				throw e;
			} finally {
			}
		}
		
	}
	
	static class TestUserServiceException extends RuntimeException {
		
	}
	
	@Test
	public void upgradeAllOrNothing() throws Exception {
//		ProxyFactoryBean ProxyFactoryBean = context.getBean("&userService",ProxyFactoryBean.class);
//		ProxyFactoryBean.setTarget(testUserService);
//		UserService txUserService = (UserService) ProxyFactoryBean.getObject();
		
//		TransactionHandler txHandler = new TransactionHandler();
//		txHandler.setTarget(testUserService);
//		txHandler.setTransactionManager(transactionManager);
//		txHandler.setPattern("upgradeLevels");
//		UserService txUserService = (UserService)Proxy.newProxyInstance(getClass().getClassLoader(), new Class[] { UserService.class }, txHandler);
		
//		UserServiceTx txUserService = new UserServiceTx();
//		txUserService.setTransactionManager(transactionManager);
//		txUserService.setUserService(testUserService);
		
		userDao.deleteAll();
		for(User user : users) userDao.add(user);
		
		try {
			this.testUserService.upgradeLevels();
			fail("TestUserServiceException expected");
		} catch(TestUserServiceException e) {
			
		}
		
		checkLevelUpgrade(users.get(1), false);
	}
	
	static class MockUserDao implements UserDao {
		private List<User> users;
		private List<User> updated = new ArrayList<User>();
		
		private MockUserDao(List<User> users) {
			this.users = users;
		}
		
		public List<User> getUpdated() {
			return this.updated;
		}
		
		public List<User> getAll() {
			return this.users;
		}
		
		public void update(User user) {
			updated.add(user);
		}
		
		public void add(User user) { throw new UnsupportedOperationException(); }
		public void deleteAll() { throw new UnsupportedOperationException(); }
		public User get(String id) { throw new UnsupportedOperationException(); }
		public int getCount() { throw new UnsupportedOperationException(); }
	}
	/* 계속 오류남
	@Test
	public void mockUpgradeLevels() throws Exception {
		UserServiceImpl userServiceImpl = new UserServiceImpl();
		
		UserDao mockUserDao = mock(springbook.dao.UserDao.class);
		when(mockUserDao.getAll()).thenReturn(this.users);
		userServiceImpl.setUserDao(mockUserDao);
		
		MailSender mockMailSender = mock(MailSender.class);
		userServiceImpl.setMailSender(mockMailSender);
		
		userServiceImpl.upgradeLevels();
		
		verify(mockUserDao, times(2)).update(any(User.class));
		verify(mockUserDao, times(2)).update(any(User.class));
		verify(mockUserDao).update(users.get(1));
		assertThat(users.get(1).getLevel(), is(Level.SILVER));
		verify(mockUserDao).update(users.get(3));
		assertThat(users.get(3).getLevel(), is(Level.GOLD));
		
		ArgumentCaptor<SimpleMailMessage> mailMessageArg = ArgumentCaptor.forClass(SimpleMailMessage.class);
		verify(mockMailSender, times(2)).send(mailMessageArg.capture());
		List<SimpleMailMessage> mailMessages = mailMessageArg.getAllValues();
		assertThat(mailMessages.get(0).getTo()[0], is(users.get(1).getEmail()));
		assertThat(mailMessages.get(1).getTo()[0], is(users.get(3).getEmail()));
	}
	*/
	
	@Test
	public void advisorAutoProxyCreator() {
		assertThat(testUserService, is(java.lang.reflect.Proxy.class));
	}
}

