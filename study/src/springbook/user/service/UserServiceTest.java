package springbook.user.service;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import springbook.dao.UserDao;
import springbook.user.domain.Level;
import springbook.user.domain.User;
import springbook.user.domain.UserLevelUpgradePolicy;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations="/springbook/test-applicationContext.xml")
public class UserServiceTest {
	@Autowired
	UserDao userDao;
	@Autowired
	UserService userService;
	@Autowired
	UserLevelUpgradePolicy userLevelUpgradePolicy;
	List<User> users;
	
	@Before
	public void setUp() {
		users = Arrays.asList(
				new User("bumjin","박범진","p1",Level.BASIC,49,0),
				new User("joytouch","강명성","p2",Level.BASIC,50,0),
				new User("erwins","신승한","p3",Level.SILVER,60,29),
				new User("madnite1","이상호","p4",Level.SILVER,60,30),
				new User("green","오민규","p5",Level.GOLD,100,Integer.MAX_VALUE)
				);
	}
	
	@Test
	public void bean() {
		assertThat(this.userService, is(notNullValue()));
	}
	
	@Test
	public void upgradeLevels() {
		userDao.deleteAll();
		for(User user : users) {
			userDao.add(user);
		}
		
		userService.upgradeLevels();
		
//		checkLevel(users.get(0), Level.BASIC);
//		checkLevel(users.get(1), Level.SILVER);
//		checkLevel(users.get(2), Level.SILVER);
//		checkLevel(users.get(3), Level.GOLD);
//		checkLevel(users.get(4), Level.GOLD);
		checkLevelUpgrade(users.get(0), false);
		checkLevelUpgrade(users.get(1), true);
		checkLevelUpgrade(users.get(2), false);
		checkLevelUpgrade(users.get(3), true);
		checkLevelUpgrade(users.get(4), false);
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
}
