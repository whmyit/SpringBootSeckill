package com.dxhy.order.config;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.executor.resultset.DefaultResultSetHandler;
import org.apache.ibatis.executor.resultset.ResultSetHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ResultMap;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.type.ClobTypeHandler;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;

/**
 * mybatis拦截器
 *
 * @author ZSC-DXHY
 * @date 创建时间: 2020-08-14 15:21
 */
@Component
@Intercepts(@Signature(method = "handleResultSets", type = ResultSetHandler.class, args = { Statement.class }))
public class MybatisResultSetInterceptor implements Interceptor {
	
	private final String[] interceptContains = {};
	
	@Override
	public Object intercept(Invocation invocation) throws Throwable {
		List<Map<String, Object>> result = new ArrayList<>();
		
		boolean isIntercept = isIntercept(invocation);
		boolean isRetrunHashMapType = isReturnHashMapType(invocation);
		if (isIntercept || isRetrunHashMapType) {
			Statement statement = (Statement) invocation.getArgs()[0];
			ResultSet resultSet = statement.getResultSet();
			if (null != resultSet) {
				while (resultSet.next()) {
					Map<String, Object> rowData = new HashMap<>(10);
					
					int columnCount = resultSet.getMetaData().getColumnCount();
					for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
						String key = resultSet.getMetaData().getColumnLabel(columnIndex);
						Object value = resultSet.getObject(key);
						if (value instanceof Clob) {
							value = new ClobTypeHandler().getResult(resultSet, columnIndex);
						}
						//将return为hashMap类型的查询快的返回列名转换为驼峰结构
						if (isRetrunHashMapType) {
							if(key.contains("_")) {
								StringBuilder keyTmp = new StringBuilder();
								String[] keyWords = key.split("_");
								for (int i = 0; i < keyWords.length; i++) {
									if(i == 0) {
										keyTmp.append(keyWords[i].toLowerCase(Locale.ENGLISH));
									} else {
										keyTmp.append(StringUtils.capitalize(keyWords[i].toLowerCase(Locale.ENGLISH)));
									}
								}
								key = String.valueOf(keyTmp);
							} else {
								key = key.toLowerCase(Locale.ENGLISH);
							}
						}
						rowData.put(key, value);
					}
					result.add(rowData);
				}
				return result;
			} else {
				return invocation.proceed();
			}
		} else {
			return invocation.proceed();
		}
	}
	
	@Override
	public Object plugin(Object arg0) {
		return Plugin.wrap(arg0, this);
	}
	
	@Override
	public void setProperties(Properties arg0) {
	}
	
	private boolean isReturnHashMapType(Invocation invocation) {
		boolean result = false;
		DefaultResultSetHandler defaultResultSetHandler = (DefaultResultSetHandler) invocation.getTarget();
		
		Class<?> rsHandlerClass = defaultResultSetHandler.getClass();
		try {
			Field mappedStatementField = rsHandlerClass.getDeclaredField("mappedStatement");
			mappedStatementField.setAccessible(true);
			MappedStatement mappedStatement = (MappedStatement) mappedStatementField.get(defaultResultSetHandler);
			ResultMap resultMap = mappedStatement.getResultMaps().get(0);
			
			String sqlCommandType = mappedStatement.getSqlCommandType().name();
			if (sqlCommandType.toUpperCase().equals(SqlCommandType.SELECT.name())) {
				if (resultMap.getType().equals(HashMap.class)) {
					result = true;
				}
			}
		} catch (NoSuchFieldException | SecurityException | IllegalAccessException | IllegalArgumentException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	private boolean isIntercept(Invocation invocation) {
		boolean result = false;
		DefaultResultSetHandler defaultResultSetHandler = (DefaultResultSetHandler) invocation.getTarget();
		
		Class<?> rsHandlerClass = defaultResultSetHandler.getClass();
		try {
			Field boundSqlField = rsHandlerClass.getDeclaredField("boundSql");
			boundSqlField.setAccessible(true);
			BoundSql boundSql = (BoundSql) boundSqlField.get(defaultResultSetHandler);
			String sql = boundSql.getSql();
			
			Field mappedStatementField = rsHandlerClass.getDeclaredField("mappedStatement");
			mappedStatementField.setAccessible(true);
			MappedStatement mappedStatement = (MappedStatement) mappedStatementField.get(defaultResultSetHandler);
			ResultMap resultMap = mappedStatement.getResultMaps().get(0);
			
			String sqlCommandType = mappedStatement.getSqlCommandType().name();
			if (sqlCommandType.toUpperCase().equals(SqlCommandType.SELECT.name())) {
				for (String interceptContain : interceptContains) {
					if (sql.toUpperCase().indexOf(interceptContain) > 0
							&& (resultMap.getType().equals(new HashMap<String, Object>(10).getClass()))) {
						result = true;
						break;
					}
				}
			}
		} catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			e.printStackTrace();
		}
		
		return result;
	}
}
