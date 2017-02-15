package springbook.user.service;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

public class TransactionAdvice implements MethodInterceptor {
	PlatformTransactionManager transactionManager;
	
	public void setTransactionManager(PlatformTransactionManager transactionManager) {
		this.transactionManager = transactionManager;
	}
	
	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		TransactionStatus status = this.transactionManager.getTransaction(new DefaultTransactionDefinition());
		
		System.out.println("invoke");
		
		try {
			Object ret = invocation.proceed();
			this.transactionManager.commit(status);
			System.out.println("invoke try");
			return ret;
		} catch(RuntimeException e) {
			this.transactionManager.rollback(status);
			System.out.println("invoke catch");
			throw e;
		}
	}

}
