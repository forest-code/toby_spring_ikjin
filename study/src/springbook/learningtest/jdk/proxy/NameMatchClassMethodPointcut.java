package springbook.learningtest.jdk.proxy;

import org.springframework.aop.ClassFilter;
import org.springframework.aop.support.NameMatchMethodPointcut;
import org.springframework.util.PatternMatchUtils;

public class NameMatchClassMethodPointcut extends NameMatchMethodPointcut {
	public void setMappedClassName(String mappedClassName) {
		System.out.println("mappedclassname:"+mappedClassName);
		this.setClassFilter(new SimpleClassFilter(mappedClassName));
	}
	
	@Override
	public void setMappedName(String mappedName) {
		System.out.println("mappedname:"+mappedName);
		super.setMappedName(mappedName);
	}
	
	static class SimpleClassFilter implements ClassFilter {
		String mappedName;
		
		private SimpleClassFilter(String mappedName) {
			System.out.println("classfilter:"+mappedName);
			this.mappedName = mappedName;
		}
		
		public boolean matches(Class<?> clazz) {
			System.out.println("matches:"+clazz.getSimpleName());
			System.out.println(PatternMatchUtils.simpleMatch(mappedName,  clazz.getSimpleName()));
			return PatternMatchUtils.simpleMatch(mappedName,  clazz.getSimpleName());
		}
	}
}
