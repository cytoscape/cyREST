package org.cytoscape.rest;

import static org.junit.Assert.fail;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.naming.spi.DirStateFactory.Result;
import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

import org.cytoscape.rest.internal.CyRESTConstants;
import org.junit.Test;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

public class SwaggerCoverageTest {

	
	
	private class SwaggerReport {
		final String className;
		final String methodName;
		final String path;
		final boolean hasApiOperationAnnotation;
		final boolean allParamsAnnotated;
		final boolean hasMessageBodyModel;
		final boolean hasResponseModel;
		
		public SwaggerReport(String className, String methodName, String path, boolean hasApiOperationAnnotation, boolean allParamsAnnotated, boolean messageBodyModel, boolean responseModel){
			this.className = className;
			this.methodName = methodName;
			this.path = path;
			this.hasApiOperationAnnotation = hasApiOperationAnnotation;
			this.allParamsAnnotated = allParamsAnnotated;
			this.hasMessageBodyModel = messageBodyModel;
			this.hasResponseModel = responseModel;
		}
		
		public String getName() {
			return className + "\t" + methodName + "\t" + path;
		}
		
		public String toString() {
			return getName() + "\t" + hasApiOperationAnnotation + "\t" + allParamsAnnotated + "\t" + hasMessageBodyModel + "\t" + hasResponseModel;
		}
	}
	
	@Test 
	public void strict() {
		swaggerCoverage();
	}

	public void swaggerCoverage()
	{
		List<SwaggerReport> reports = new ArrayList<SwaggerReport>();
		for (Class<?> clazz : CyRESTConstants.coreResourceClasses)
		{
			Path rootPath = clazz.getAnnotation(Path.class);
	
			for (Method method : clazz.getMethods()){
				String className = clazz.getClass().getName();
				String path = rootPath != null ? rootPath.value() : "";
				boolean hasApiOperationAnnotation;
				boolean hasMessageBodyModel;
				boolean hasResponseModel;
				
				if (method.isAnnotationPresent(GET.class) 
						|| method.isAnnotationPresent(PUT.class)
						|| method.isAnnotationPresent(POST.class)
						|| method.isAnnotationPresent(DELETE.class)) {
				
				
					hasMessageBodyModel = true;
					
					
					hasResponseModel = !(String.class.equals(method.getReturnType()) ||
							Result.class.equals(method.getReturnType()));
					Path annotationPath = method.getAnnotation(Path.class);
					if (annotationPath != null) {
						path += method.getAnnotation(Path.class).value();
					}
					hasApiOperationAnnotation = method.isAnnotationPresent(ApiOperation.class);
					if (hasApiOperationAnnotation) {
						ApiOperation apiOperation = method.getAnnotation(ApiOperation.class);
						if (!java.lang.Void.class.equals(apiOperation.response())) {
							hasResponseModel = true;
						} 
					}
					
					int params = 0;
					int apiParams = 0;
					boolean allParamsAnnotated;
					for (Parameter parameter : method.getParameters())
					{
						
						if (parameter.isAnnotationPresent(ApiParam.class)){
							apiParams++;
						}
						if (
								parameter.isAnnotationPresent(PathParam.class) 
								|| parameter.isAnnotationPresent(QueryParam.class)
								|| parameter.isAnnotationPresent(FormParam.class)
								) {
							params++;
						}
						else if (parameter.isAnnotationPresent(Context.class)) {
							
						}
						else {
							if (String.class.equals(parameter.getType()) 
									|| InputStream.class.equals(parameter.getType())
								) {
								hasMessageBodyModel = false;
							} 
							else if (Collection.class.isAssignableFrom(parameter.getType())) {
								hasMessageBodyModel = true;
							}
							else {
								// This is an unexpected body type.
								fail("Unrecognized body parameter type:" + parameter.getClass().getName());
							}
						}
					}
					allParamsAnnotated = apiParams == params;
					
					SwaggerReport swaggerReport = new SwaggerReport(clazz.getName(), method.toGenericString(), path, hasApiOperationAnnotation, allParamsAnnotated, hasMessageBodyModel,hasResponseModel);
					reports.add(swaggerReport);
				}
			}
		}
		System.out.println("Missing ApiOperation tag: ");
		for (SwaggerReport swaggerReport : reports) {
			if (!swaggerReport.hasApiOperationAnnotation){
				System.out.println(swaggerReport.getName());
			}
		}
		
		System.out.println("\nIncomplete ApiParam tags: ");
		for (SwaggerReport swaggerReport : reports) {
			if (!swaggerReport.allParamsAnnotated) {
				System.out.println(swaggerReport.getName());
			}
		}

		System.out.println("\nMissing MessageBody model: ");
		for (SwaggerReport swaggerReport : reports) {
			if (!swaggerReport.hasMessageBodyModel) {
				System.out.println(swaggerReport.getName());
			}
		}
		
		System.out.println("\nMissing Response model: ");
		for (SwaggerReport swaggerReport : reports) {
			if (!swaggerReport.hasResponseModel) {
				System.out.println(swaggerReport.getName());
			}
		}
		
		int strictTotal = 0;
		for (SwaggerReport swaggerReport : reports) {
			if (swaggerReport.hasApiOperationAnnotation && swaggerReport.allParamsAnnotated && swaggerReport.hasMessageBodyModel && swaggerReport.hasResponseModel) {
				strictTotal++;
			}
		}
		
		System.out.print("\nPerfect coverage: " + strictTotal + "/" + reports.size());
		System.out.format(" (%2.0f%%)", (double)strictTotal / reports.size() * 100);
		
	}
}
