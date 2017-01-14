/*
 * =====================================================================================
 *
 *       Filename:  jlalgorithm.h
 *
 *    Description:  包含多边形填充算法
 *
 *        Version:  1.0
 *        Created:  04/29/2011 08:35:29 PM
 *       Revision:  none
 *       Compiler:  gcc
 *
 *         Author:  Wu Jing (jing), wujing@jingling.cn
 *        Company:  jingling
 *
 * =====================================================================================
 */

#ifndef JLALGORITHM_H
#define JLALGORITHM_H

typedef struct tEdge
{
	int ymin;
	float x, dx;
	struct tEdge *next;
}Edge;

typedef Edge* pEdge;

extern void setVisibleArea(int* img, int w, int h, int *pts, int cnt);

#endif
