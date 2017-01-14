package cn.jingling.lib.textbubble;


public class PointsCaculation 
{
	/*
	 * 计算两点间的距离
	 */
	public static double caculateTwoPointsDis(double x1,double y1,double x2,double y2)
	{
		return caculateTwoPointsDis((int)x1,(int)y1,(int)x2,(int)y2);
	}
	
	public static double caculateTwoPointsDis(int x1,int y1,int x2,int y2)
	{
		return Math.sqrt((x1-x2)*(x1-x2)+(y1-y2)*(y1-y2));
	}

	public static double caculateTwoPointsDis(PwMotion mEvent)
	{
		return caculateTwoPointsDis(mEvent.getX(0),mEvent.getY(0),mEvent.getX(1),mEvent.getY(1));
	}
	
	/*
	 * 计算两点的夹角
	 */
	public static double caculateTwoPointsAngle(int x1,int y1,int x2,int y2)
	{
		return (Math.atan2(y2-y1, x2-x1))*(180/Math.PI);
	}
	
	public static double caculateTwoPointsAngle(double x1,double y1,double x2,double y2)
	{
		return caculateTwoPointsAngle((int)x1,(int)y1,(int)x2,(int)y2);
	}
	
	public static double caculateTwoPointsAngle(PwMotion mEvent)
	{
		return caculateTwoPointsAngle(mEvent.getX(0),mEvent.getY(0),mEvent.getX(1),mEvent.getY(1));
	}
}
