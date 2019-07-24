package com.xier.lora.base.service;

import java.util.Map;

import javax.servlet.ServletContext;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ApplicationObjectSupport;
import org.springframework.web.context.ServletContextAware;


/**
 * spring上下文
 * <p>
 * 直接操作tomcat容器中的实例
 * </p>
 * @author lvhui5 2017年11月22日 下午4:15:50
 * @version V1.0
 */
//@Service("springContext")
public class SpringContext extends ApplicationObjectSupport implements InitializingBean, ServletContextAware {


	@SuppressWarnings("unused")
	private static final String PLACE_HOLDER_0 = "{0}";


	private static ApplicationContext webApplicationContext;
	private static ServletContext servletContext;


	private static void setServletContext2(ServletContext servletContext){
		SpringContext.servletContext = servletContext;
	}
	private static void setApplicationContext2(ApplicationContext applicationContext){
		SpringContext.webApplicationContext = applicationContext;
	}
	
	@Override
	public void setServletContext(ServletContext servletContext) {
		SpringContext.setServletContext2(servletContext);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		setApplicationContext2(super.getApplicationContext()); // 默认取到的root容器还是dispatchServlet容器取决于SpringContext在什么地方加载，Listener中为root，servlet中为后者。
		// SpringContext.rootApplicationContext = (ApplicationContext)
		// servletContext.getAttribute("org.springframework.web.context.WebApplicationContext.ROOT");
		System.out.println("SpringContext.webApplicationContext【" + webApplicationContext.getDisplayName() + "】初始化完成");
		// System.out.println("SpringContext.rootApplicationContext【" +
		// rootApplicationContext.getDisplayName() + "】初始化完成");
		// Assert.notNull(webApplicationContext, "webApplicationContext不能为空!");
		// Assert.notNull(rootApplicationContext,
		// "rootApplicationContext不能为空!");
		//Assert.notNull(servletContext, "servletContext不能为空!");
	}

	/**
	 * 根据名称获取Bean
	 * 
	 * @param beanName
	 * @return
	 */
	public static Object getBean(String beanName) {
		try {
			return webApplicationContext.getBean(beanName);
		} catch (Exception e) {
			throw new RuntimeException("获取bean[" + beanName + "]失败", e);
		}
	}

	/**
	 * 根据名称，类型获取Bean
	 * 
	 * @param beanName
	 * @param requiredType
	 * @return
	 */
	public static <T> T getBean(String beanName, Class<T> requiredType) {
		try {
			return webApplicationContext.getBean(beanName, requiredType);
		} catch (Exception e) {
			throw new RuntimeException("获取bean[" + beanName + "]失败,指定type[" + requiredType + "]", e);
		}
	}

	/**
	 * 根据类型获取Bean Map
	 * 
	 * @param type
	 * @return
	 */
	public static <T> Map<String, T> getBeanMapOfType(Class<T> type) {
		try {
			return webApplicationContext.getBeansOfType(type);
		} catch (Exception e) {
			throw new RuntimeException("获取bean失败,指定type[" + type + "]", e);
		}
	}




	/**
	 * 获取Servlet上下文对象
	 * 
	 * @return
	 */
	public static ServletContext getServletContext() {
		return servletContext;
	}


}
