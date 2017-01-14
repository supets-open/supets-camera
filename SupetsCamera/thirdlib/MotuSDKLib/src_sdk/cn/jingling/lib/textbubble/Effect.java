package cn.jingling.lib.textbubble;

public abstract class Effect {

	public abstract boolean onOk();

	public abstract boolean onCancel();

	public abstract void perform();
	
	// 拦截返回键，需要拦截返回键的Effect重新该方法根据情况返回true或false(可见GlobalAutoBeautyNewEffect)
	public boolean onCallBack(){
		return false;
	}
}
