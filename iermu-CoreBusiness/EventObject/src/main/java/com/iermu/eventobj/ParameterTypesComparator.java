package com.iermu.eventobj;

import java.sql.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Map;

/**
 * 参数类型比较器
 * @author xy
 *
 */
public class ParameterTypesComparator {

	/**
	 * 比较参数个数
	 * 比较参数Class类型
	 * @param args	比较对象
	 * @param clzs	目标Class类型
	 * @return
	 */
	public static int compare(Object[] args, Class<?>[] clzs) {
		if(args == null || clzs == null) {
			return -1;
		}

		int compare = args.length - clzs.length;
		if(compare == 0 ) {
			for(int i=0; i<args.length; i++) {
				Object obj = args[i];
				int compareClz = 0;
				if(obj != null) {
					//TODO 暂不处理
					if(obj instanceof Collection) {
						compareClz = 0;
					} else if(obj instanceof Map) {
						compareClz = 0;
					} else if(obj instanceof Array) {
						compareClz = 0;
					} else {
						compareClz = compareClz(clzs, obj.getClass());
					}
				}
				if(compareClz < 0 && compareClz == i) {
					return -1;
				} else {
					compare = compareClz;
				}
			}
		}
		return compare;
	}

	private static int compareClz(Class<?>[] clzs, Class<?> clz) {
		return Arrays.binarySearch(clzs, clz, new Comparator<Class<?>>() {
			@Override
			public int compare(Class<?> arg0, Class<?> arg1) {
				if (arg0.isArray() && arg1.isArray()) {
					return 0;
				} else if(arg0 instanceof Object) {
                    return 0;
                }
				return arg0.getName().equals(arg1.getName()) ? 0 : -1;
			}
		});
	}
}
