package com.lib.pulltorefreshview.pullableview;

public interface Pullable
{

    /**
     * 判断是否可以下拉, 如果不需要下拉功能可以直接return false
     * @return true: 可以下拉, 否则返回false
     */
	boolean canPullDown();

    /**
     * 判断是否可以上拉, 如果不需要上拉功能可以直接return false
     * @return true: 可以上拉, 否则返回false
     */
	boolean canPullUp();
}
