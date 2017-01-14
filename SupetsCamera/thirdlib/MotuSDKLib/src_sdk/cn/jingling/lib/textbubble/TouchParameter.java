package cn.jingling.lib.textbubble;

public class TouchParameter {

	public int pointerCnt;
	public MyPoint FirstPointer;
	public MyPoint secondPointer;

	public Boolean isUp;

	public TouchParameter(int pC, MyPoint p1, MyPoint p2, Boolean up) {
		pointerCnt = pC;
		isUp = up;

		FirstPointer = new MyPoint(p1);
		secondPointer = new MyPoint(p2);
	}

	public void setIsUp(Boolean up) {
		isUp = up;
	}

	public int getPointerCnt() {
		return pointerCnt;
	}

	public void setPointerCnt(int pointerCnt) {
		this.pointerCnt = pointerCnt;
	}

	public MyPoint getFirstPointer() {
		return FirstPointer;
	}

	public void setFirstPointer(MyPoint firstPointer) {
		FirstPointer = firstPointer;
	}

	public MyPoint getSecondPointer() {
		return secondPointer;
	}

	public void setSecondPointer(MyPoint secondPointer) {
		this.secondPointer = secondPointer;
	}

	public Boolean getIsUp() {
		return isUp;
	}

}
