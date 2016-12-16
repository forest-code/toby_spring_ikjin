package springbook.user.domain;


public interface UserLevelUpgradePolicy {
	boolean canUpgradeLevel(User user);
	User upgradeLevel(User user);
}
