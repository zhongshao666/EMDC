package com.briup.environment.util;

public interface BackUP extends WossModuleInit,ConfigurationAWare {
	/**
	 * 在保存数据时，在原来文件的基础上进行追加。 
	 */
	public static final boolean STORE_APPEND=true;
	/**
	 * 在保存数据时，覆盖原来的文件
	 */
	public static final boolean STORE_OVERRIDE=false;
	/**
	 * 在读取数据同时，删除备份文件
	 */
	public static final boolean LOAD_REMOVE=true;
	/**
	 * 在读取数据同时，保留备份文件。 
	 */
	public static final boolean LOAD_UNREMOVE=false;
	/**
	 * 通过键名获取已经备份的数据
	 * @param key
	 * @param flag
	 * @return	
	 */
	Object load(String key, boolean flag);
	/**
	 * 通过键名来存储数据。
	 * @param key
	 * @param data
	 * @param flag
	 */
	void store(String key, Object data, boolean flag);
}
