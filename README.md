# Ŀ��

��CDIӦ�����ܹ�ʹ��Spring bean

# �÷�

���ַ�ʽʵ��������չ

## дһ��ConfigurableApplicationContext Producer

    public class ConfigurableApplicationContextProducer {
    	@Produces
    	public ConfigurableApplicationContext start() {
    		return new ClassPathXmlApplicationContext("classpath:applicationContext.xml");
    	}

    	public void close(@Disposes final ConfigurableApplicationContext ctx) {
    		ctx.close();
    	}
    }


## ʹ�� classpath applicationContext.xml �����ļ�

�� classpath ���� applicationContext.xml �����ļ������ַ�ʽ�ǳ���㡣

## ��CDI Bean��ע��Spring bean

    @Inject
    @Spring
    protected OAuth2Service oauth2Service;

