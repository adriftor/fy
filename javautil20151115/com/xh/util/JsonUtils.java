package com.xh.util;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * json处理类
 * 
 * @author adriftor
 *
 */
class JsonUtils {

	public static final Log log = LogFactory.getLog(JsonUtils.class);

	// cache ObjectMapper
	private static ObjectMapper objectMapper;

	/**
	 * 序列化Object，Object可以是POJO，也可以是Collection或数组 java对象变为json字符串
	 * 
	 * @param obj
	 *            可以是任何对象
	 * @return
	 * @throws RuntimeException
	 */
	public static String toJson(Object obj) throws RuntimeException {
		try {
			ObjectMapper jsonMapper = getMapper();
			return jsonMapper.writeValueAsString(obj);
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 反序列化POJO或简单Collection如List<String>
	 * 
	 * @param jsonString
	 * @param clazz
	 * @return
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	public static <T> T fromJson(String jsonString, Class<T> clazz) {
		if (CommonUtil.isEmpty(jsonString)) {
			return null;
		}
		ObjectMapper mapper = getMapper();
		try {
			return mapper.readValue(jsonString, clazz);
		}
		catch (Exception e) {
			throw new RuntimeException("JsonMapper fromJson method throw exception.", e);
		}
	}

	/**
	 * 反序列化复杂Collection如List<Bean>
	 * 
	 * @param jsonString
	 * @param collectionClass
	 *            集合类型
	 * @param elementClass
	 *            集合元素类型
	 * @return
	 * @throws JsonParseException
	 * @throws JsonMappingException
	 * @throws IOException
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T> T fromJson(String jsonString, Class<? extends Collection> collectionClass, Class<?> elementClass) {
		if (CommonUtil.isEmpty(jsonString)) {
			return null;
		}
		ObjectMapper mapper = getMapper();
		JavaType javaType = mapper.getTypeFactory().constructCollectionType(collectionClass, elementClass);
		try {
			return (T) mapper.readValue(jsonString, javaType);
		}
		catch (Exception e) {
			throw new RuntimeException("JsonMapper fromJson method throw exception.", e);
		}
	}

	/**
	 * 获取json Mapper, 当需要特殊配置时可以直接获取mapper进行配置
	 * 
	 * @return
	 */
	public static ObjectMapper getMapper() {
		if (objectMapper == null) {
			objectMapper = new ObjectMapper();
			// AnnotationIntrospector secondary = new
			// JacksonAnnotationIntrospector();
			// AnnotationIntrospector introspector = new
			// AnnotationIntrospectorPair(secondary, secondary);
			// objectMapper.setAnnotationIntrospector(introspector);
			initCondig(objectMapper);
		}

		return objectMapper;
	}

	/**
	 * 配置转换行为
	 * 
	 * @param mapper
	 */
	private static void initCondig(ObjectMapper mapper) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// 默认的日期格式
		mapper.setDateFormat(df);
		mapper.setSerializationInclusion(Include.NON_NULL);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		// mapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
		// mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
	}
}
