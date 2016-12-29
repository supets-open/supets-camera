package com.supets.commons.model;

/**
 * 描述ListView滑动位置的一个类
 */
public class ListViewPosition {

    /**
     * ListView当前第一个可见的item
     */
    public int position;

    /**
     * Item距离顶部的偏移
     */
    public int top;

    /**
     * 构造函数
     *
     * @param position
     * @param top
     */
    public ListViewPosition(int position, int top) {
        this.position = position;
        this.top = top;
    }
}
