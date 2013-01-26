package com.yinyuetai.cdi.spring.inject;

import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.enterprise.event.Observes;
import javax.enterprise.inject.spi.*;
import java.util.Set;

/**
 * User: yipengfei
 * Date: 13-1-26
 * Time: ÏÂÎç1:40
 */
public class SpringExtension implements Extension {
	public static boolean INIT_BEANS=false;
	private static final String RESOURCE_NAME = "applicationContext.xml";
	private static final String CLASSLOADER_RESOURCE = "/" + RESOURCE_NAME;
	private static final String CLASSPATH_RMANNIBUCAU_SPRING_CDI_XML = "classpath:" + RESOURCE_NAME;

	protected void addSpringBeansToCdi(@Observes final AfterBeanDiscovery afterBeanDiscovery, final BeanManager beanManager) {
		if(INIT_BEANS){
			return;
		}
		INIT_BEANS=true;
		ConfigurableApplicationContext ctx = null;
		// get spring context --> needs @Produces
		final Set<Bean<?>> beans = beanManager.getBeans(ConfigurableApplicationContext.class);
		if (beans != null && beans.size() == 1) {
			final Bean<?> bean = beanManager.resolve(beans);
			ctx = (ConfigurableApplicationContext) beanManager.getReference(bean, ConfigurableApplicationContext.class, beanManager.createCreationalContext(null));
		}else if (Thread.currentThread().getContextClassLoader().getResource(CLASSLOADER_RESOURCE) != null) {
			ctx = new ClassPathXmlApplicationContext(CLASSPATH_RMANNIBUCAU_SPRING_CDI_XML);
		}
		if (ctx != null) {
			for (final String id : ctx.getBeanDefinitionNames()) {
				final Class<Object> clazz = (Class<Object>) ctx.getType(id);
				final AnnotatedType<Object> annotatedType = beanManager.createAnnotatedType(clazz);
				final InjectionTarget<Object> injectionTarget = beanManager.createInjectionTarget(annotatedType);
				final ConfigurableApplicationContext applicationContext = ctx;
				afterBeanDiscovery.addBean(new SpringBean<Object>(clazz, ctx, injectionTarget));
			}
		}
	}
}
