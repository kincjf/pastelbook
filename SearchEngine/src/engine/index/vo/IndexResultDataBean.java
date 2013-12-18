package engine.index.vo;

public class IndexResultDataBean {

	/**
	 * @param args
	 */
	private int totalBookmarkSize;
	private int totalURLListSize;
	private int assignedIdxNum;
	/**
	 * @return the totalBookmarkSize
	 */
	public final int getTotalBookmarkSize() {
		return totalBookmarkSize;
	}
	/**
	 * @param totalBookmarkSize the totalBookmarkSize to set
	 */
	public final void setTotalBookmarkSize(int totalBookmarkSize) {
		this.totalBookmarkSize = totalBookmarkSize;
	}
	/**
	 * @return the totalURLListSize
	 */
	public final int getTotalURLListSize() {
		return totalURLListSize;
	}
	/**
	 * @param totalURLListSize the totalURLListSize to set
	 */
	public final void setTotalURLListSize(int totalURLListSize) {
		this.totalURLListSize = totalURLListSize;
	}
	/**
	 * @return the assignedIdxNum
	 */
	public final int getAssignedIdxNum() {
		return assignedIdxNum;
	}
	/**
	 * @param assignedIdxNum the assignedIdxNum to set
	 */
	public final void setAssignedIdxNum(int assignedIdxNum) {
		this.assignedIdxNum = assignedIdxNum;
	}

}
