package cn.jingling.lib.filters;

abstract public class Filter {
	protected void statisticEvent() {
		String label = this.getClass().getSimpleName();
	}
}
