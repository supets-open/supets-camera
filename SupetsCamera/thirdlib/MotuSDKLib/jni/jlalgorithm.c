/*
 * =====================================================================================
 *
 *       Filename:  jlalgorithm.c
 *
 *    Description:  鍖呭惈澶氳竟褰㈠～鍏呯畻娉� *
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
#include <stdio.h>
#include <stdlib.h>
#include <memory.h>
#include <math.h>
#include "jlalgorithm.h"

void InsertEdge(Edge *list, Edge *edge)
{
	Edge *p, *q=list;
	p=q->next;
	while(p)
	{
		if(edge->x < p->x || edge->x+edge->dx < p->x+p->dx)
			p = 0;
		else
		{
			q = p;
			p = p->next;
		}
	}
	edge->next = q->next;
	q->next = edge;
}

void BuildEdgeList(int cnt, int *pts, Edge *edges[], int nScanMin)
{
	Edge *edge;
	int *p1, *p2;
	int i;
	p1 = pts + 2*(cnt-1);
	for(i=0; i<cnt; i++)
	{
		p2 = pts + 2*i;
		if(p1[1] != p2[1])
		{
			edge = (Edge*)malloc(sizeof(Edge));
			edge->dx = -(float)(p2[0]-p1[0])/(p2[1]-p1[1]);
			if(p1[1]>p2[1])
			{
				edge->x = (float)(p1[0]);
				edge->ymin = p2[1];
				InsertEdge(edges[p1[1]-nScanMin],edge);
			}
			else
			{
				edge->x = (float)(p2[0]);
				edge->ymin = p1[1];
				InsertEdge(edges[p2[1]-nScanMin],edge);
			}
		}
		p1 = p2;
	}
}


void BuildActiveList(int scan, Edge *active, Edge *edges[], int nScanMin)
{
	Edge *p, *q;
	p = edges[scan-nScanMin]->next;
	while(p)
	{
		q = p->next;
		InsertEdge(active,p);
		p = q;
	}
}


void FillScan(int* line, int width, Edge *active)
{
	int i;
	Edge *p1, *p2;
	p1 = active->next;

	/* 用来处理一行中没有交点的情况，用来应对顶点的情况^ */
	if(p1 == 0)
	{
		for(i=0; i<width; ++i)
		{
			line[i] &= (0x00ffffff);
		}
		return;
	}

	while(p1)
	{
		p2 = p1->next;

		for(i=0; i<width; ++i)
		{
			if(i<p1->x || i>p2->x)
				line[i] &= (0x00ffffff);
			else
				line[i] |= (0xff000000);
		}
		p1=p2->next;
	}
}

void DeleteAfter(Edge *q)
{
	Edge *p=q->next;
	q->next=p->next;
	free(p);
}

void UpdateActiveList(int scan, Edge *active)
{
	Edge *q=active,*p=active->next;
	while(p)
	{
		if(scan <= p->ymin)
		{
			p=p->next;
			DeleteAfter(q);
		}
		else
		{
			p->x = p->x+p->dx;
			q = p;
			p = p->next;
		}
	}
}

void setVisibleArea(int* img, int w, int h, int *pts, int cnt)
{
	Edge *active;
	int* line;
	int i,scan,scanmin=1000,scanmax=0;

	if(cnt <= 0 || pts == 0)
	{
		return;
	}

	for(i=0;i<cnt;i++)
	{
		if(scanmax < pts[2*i+1]) scanmax=pts[2*i+1];
		if(scanmin > pts[2*i+1]) scanmin=pts[2*i+1];
	}
	pEdge *edges = (pEdge *)malloc((scanmax-scanmin+1)*sizeof(pEdge));
	for(scan=scanmin;scan<=scanmax;scan++)
	{
		edges[scan-scanmin]=(Edge *)malloc(sizeof(Edge));
		edges[scan-scanmin]->next=NULL;
	}

	BuildEdgeList(cnt,pts,edges,scanmin);
	active = (Edge *)malloc(sizeof(Edge));
	active->next = 0;
	line = img + w*scanmax;
	for(scan=scanmax;scan>=scanmin;scan--)
	{
		BuildActiveList(scan,active,edges,scanmin);
		FillScan(line, w, active);
		UpdateActiveList(scan-1,active);
		line -= w;
	}

	free(active);
	for(scan=scanmin;scan <=scanmax;scan++)
	{
		free(edges[scan-scanmin]);
	}
	free(edges);
}

