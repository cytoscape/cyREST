package org.cytoscape.rest;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import javax.ws.rs.Path;
import org.cytoscape.rest.internal.CyRESTConstants;
import org.junit.Ignore;
import org.junit.Test;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

public class SwaggerCoverageTest {

	enum PrintMode {
		STRICT,
		UNCOVERED,
		BASIC,
		ALL
	}
	
	@Test 
	public void strict() {
		swaggerCoverage(PrintMode.STRICT);
	}
	
	@Ignore @Test
	public void uncovered() {
		swaggerCoverage(PrintMode.UNCOVERED);
	}

	@Ignore @Test
	public void basic() {
		swaggerCoverage(PrintMode.BASIC);
	}

	@Ignore  @Test
	public void all() {
		swaggerCoverage(PrintMode.ALL);
	}

	public void swaggerCoverage(PrintMode mode)
	{
		int pathTotal = 0;
		int basicSwaggerTotal = 0;
		int strictSwaggerTotal = 0;

		for (Class<?> clazz : CyRESTConstants.coreResourceClasses)
		{
			Path rootPath = clazz.getAnnotation(Path.class);

			for (Method method : clazz.getMethods()){
				boolean isBasic = false;
				if (method.isAnnotationPresent(Path.class)) {
					String[] result = new String[]{clazz.toString(),method.getName(),"","","",""};
					pathTotal++;
					if (rootPath != null){
						result[2] = new String(rootPath.value());
					}
					result[2] += method.getAnnotation(Path.class).value();
					if (method.isAnnotationPresent(ApiOperation.class)) {
						basicSwaggerTotal++;
						isBasic=true;
						result[3] = "YES";
					}
					else {
						result[3] = "NO";
					}

					int params = 0;
					int apiParams = 0;
					for (Parameter parameter : method.getParameters())
					{
						params++;
						if (parameter.isAnnotationPresent(ApiParam.class)){
							apiParams++;
						}
					}
					if (isBasic && apiParams == params){
						strictSwaggerTotal++;
						result[4] = "YES";
					} else {
						result[4] = "NO";
					}

					result[5] = apiParams + "/"+ params;
					boolean print = false;
					if (mode == PrintMode.STRICT) {
						if (result[3].equals("YES") && result[4].equals("YES")) {
							print = true;
						}
					} else if (mode == PrintMode.UNCOVERED) {
						if (result[3].equals("NO") && result[4].equals("NO")) {
							print = true;
						}
					} else if (mode == PrintMode.BASIC) {
						if (result[3].equals("YES") && result[4].equals("NO")) {
							print = true;	
						}
					} else if (mode == PrintMode.ALL) {
						print = true;
					} 
					if (print) {
						System.out.println(result[0] + "\t"
								+ result[1] + "\t"
								+ result[2] + "\t"
								+ result[3] + "\t"
								+ result[4] + "\t"
								+ result[5]
								);
					}
				}
			}
		}



		System.out.println("Basic: \t" + basicSwaggerTotal + "/" + pathTotal);
		System.out.println("Strict:\t" + strictSwaggerTotal + "/" + pathTotal);
	}
}
