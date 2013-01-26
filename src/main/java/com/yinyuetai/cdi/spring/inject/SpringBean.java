package com.yinyuetai.cdi.spring.inject;

import com.caucho.config.inject.OwnerCreationalContext;
import com.yinyuetai.cdi.spring.SpringLiteral;
import org.springframework.beans.factory.ListableBeanFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.context.spi.CreationalContext;
import javax.enterprise.inject.spi.Annotated;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.enterprise.inject.spi.InjectionTarget;
import javax.inject.Named;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * User: yipengfei
 * Date: 13-1-26
 * Time: ÏÂÎç4:27
 */
public class SpringBean<T> implements Bean<T> {
	private static final Logger log = Logger.getLogger(SpringBean.class.getName());
	private Class<T> clazz;
	private String _name;

	public SpringBean(Class<T> clazz, ListableBeanFactory applicationContext, InjectionTarget<T> injectionTarget) {
		this.clazz = clazz;
		this.applicationContext = applicationContext;
		this.injectionTarget = injectionTarget;
	}

	private ListableBeanFactory applicationContext;
	private InjectionTarget<T> injectionTarget;

	private Set<Type> types(final Class<?> clazz) {
		final Set<Type> classes = new HashSet<Type>();
		Class<?> current = clazz;
		while (current != null && !Object.class.equals(current)) {
			classes.add(current);
			current = current.getSuperclass();
		}
		return classes;
	}

	@Override
	public Set<Type> getTypes() {
		return types(clazz);
	}

	@Override
	public Set<Annotation> getQualifiers() {
		return new HashSet<Annotation>() {{
			add(SpringLiteral.SPRING);
		}};
	}

	@Override
	public Class<? extends Annotation> getScope() {
		return ApplicationScoped.class;
	}

	protected String getNamedValue(Annotation ann) {
		try {
			if (ann instanceof Named) {
				return ((Named) ann).value();
			}

			Method method = ann.getClass().getMethod("value");
			method.setAccessible(true);

			return (String) method.invoke(ann);
		} catch (NoSuchMethodException e) {
			// ioc/0m04
			log.log(Level.FINE, e.toString(), e);
			return "";
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Introspects the qualifier annotations
	 */
	protected void introspectName(Annotated annotated) {
		if (_name != null)
			return;

		Annotation ann = annotated.getAnnotation(Named.class);

		if (ann != null) {
			String value = getNamedValue(ann);

			if (value == null)
				value = "";

			_name = value;
		}
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public Set<Class<? extends Annotation>> getStereotypes() {
		return Collections.emptySet();
	}

	@Override
	public Class<?> getBeanClass() {
		return clazz;
	}

	@Override
	public boolean isAlternative() {
		return false;
	}

	@Override
	public boolean isNullable() {
		return false;
	}

	@Override
	public Set<InjectionPoint> getInjectionPoints() {
		return injectionTarget.getInjectionPoints();
	}

	@Override
	public T create(CreationalContext<T> creationalContext) {
		T instance = null;
		Named anno = null;
		Class parentClazz = ((OwnerCreationalContext) creationalContext).getParentValue().getClass();
		Field[] fields = parentClazz.getDeclaredFields();
		for (Field field : fields) {
			anno = field.getAnnotation(Named.class);
			if (anno != null) {
				break;
			}
		}
		if (anno != null) {
			String beanName = anno.value();
			if (beanName == null || "".equals(beanName))
				instance = applicationContext.getBean(clazz);
			else
				instance = applicationContext.getBean(beanName, clazz);
		} else {
			instance = applicationContext.getBean(clazz);
		}
		if (instance == null) {
			new RuntimeException("Not found spring bean: " + clazz);
		}
		injectionTarget.inject(instance, creationalContext);
		return instance;
	}

	@Override
	public void destroy(T instance, CreationalContext<T> creationalContext) {
		injectionTarget.preDestroy(instance);
		injectionTarget.dispose(instance);
		creationalContext.release();
	}
}
