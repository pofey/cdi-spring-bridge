package com.yinyuetai.cdi.spring;

import javax.enterprise.util.AnnotationLiteral;

/**
 * User: yipengfei
 * Date: 13-1-26
 * Time: обнГ5:30
 */
public class SpringLiteral extends AnnotationLiteral<Spring> implements Spring {
	public static Spring SPRING=new SpringLiteral();
}
