package springbook.user.domain;


public class UserLevelUpgradePolicyA implements UserLevelUpgradePolicy {
	public final int MIN_LOGCOUNT_FOR_SILVER = 50;
	public final int MIN_RECOMMEND_FOR_GOLD = 30;
	
	@Override
	public boolean canUpgradeLevel(User user) {
		Level currentLevel = user.getLevel();
		switch(currentLevel) {
			case BASIC: return (user.getLogin() >= MIN_LOGCOUNT_FOR_SILVER);
			case SILVER: return (user.getRecommend() >= MIN_RECOMMEND_FOR_GOLD);
			case GOLD: return false;
			default: throw new IllegalArgumentException("Unknown Level: " + currentLevel);
		}
	}
	
	@Override
	public User upgradeLevel(User user) {
		user.upgradeLevel();
		return user;
	}
	
	
}
