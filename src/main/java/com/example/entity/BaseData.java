package com.example.entity;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.function.Consumer;

/*
 * Created with IntelliJ IDEA
 *
 * @author 郭宏洋
 * @version 1.0.0
 * @DateTime 2023/8/29 13:54
 * <p>
 * 对象转换小工具
 */

/**
 * 该接口用于将实体类转换为VO类
 */
public interface BaseData {

	/**
	 * 将该对象转换为指定类的对象，并对转换后的对象应用Consumer操作
	 *
	 * @param clazz    转换后的对象的类型
	 * @param consumer 对转换后的对象应用的操作
	 * @return 转换后的对象
	 */
	default <V> V asViewObject(Class<V> clazz , Consumer<V> consumer) {
		V v = this.asViewObject(clazz);
		consumer.accept(v);
		return v;
	}

	/**
	 * 将该对象转换为指定类的对象
	 *
	 * @param clazz 转换后的对象的类型
	 * @return 转换后的对象
	 */
	default <V> V asViewObject(Class<V> clazz) {
		try {
			Field[] declaredFields = clazz.getDeclaredFields();
			Constructor<V> constructor = clazz.getConstructor();
			V v = constructor.newInstance();
			for (Field declaredField : declaredFields) {
				convert(declaredField , v);
			}
			return v;
		} catch ( ReflectiveOperationException exception ) {
			throw new RuntimeException(exception.getMessage());
		}
	}

	/**
	 * 将该对象的字段值设置到目标对象的对应字段中
	 *
	 * @param field 目标对象的字段
	 * @param vo    目标对象
	 */
	private void convert(Field field , Object vo) {
		try {
			Field source = this.getClass().getDeclaredField(field.getName());
			field.setAccessible(true);
			source.setAccessible(true);
			field.set(vo , source.get(this));
		} catch ( IllegalAccessException | NoSuchFieldException ignored ) {
		}
	}
}
