package io.xjar;

/**
 * 数组工具
 *
 * @author Payne 646742615@qq.com
 * 2019/5/20 10:25
 */
public class XArray {

    /**
     * 数组是否为{@code null}或者是空数组
     *
     * @param array 数组
     * @return 如果是则返回{@code true} 否则返回{@code false}
     */
    public static boolean isEmpty(Object[] array) {
        return array == null || array.length == 0;
    }

}
