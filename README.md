# 目标

在CDI应用中能够使用Spring bean

# 用法

两种方式实现这种扩展

## 写一个ConfigurableApplicationContext Producer

    public class ConfigurableApplicationContextProducer {
    	@Produces
    	public ConfigurableApplicationContext start() {
    		return new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
    	}

    	public void close(@Disposes final ConfigurableApplicationContext ctx) {
    		ctx.close();
    	}
    }


## 使用 classpath applicationContext.xml 配置文件

在 classpath 创建 applicationContext.xml 配置文件，这种方式非常简便。

## 在CDI Bean中注入Spring bean

    @Inject
    @Spring
    protected OAuth2Service oauth2Service;

