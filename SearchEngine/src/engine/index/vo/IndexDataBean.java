package engine.index.vo;

public class IndexDataBean {

	/**
	 * @param args
	 */
	
	private int idxNum;
	private String url;
	private String title;
	private String modifiedDate;
	private String registeredDate;
	private int hit;
	/**
	 * @return the idxNum
	 */
	public final int getIdxNum() {
		return idxNum;
	}
	/**
	 * @param idxNum the idxNum to set
	 */
	public final void setIdxNum(int idxNum) {
		this.idxNum = idxNum;
	}
	/**
	 * @return the url
	 */
	public final String getUrl() {
		return url;
	}
	/**
	 * @param url the url to set
	 */
	public final void setUrl(String url) {
		this.url = url;
	}
	/**
	 * @return the title
	 */
	public final String getTitle() {
		return title;
	}
	/**
	 * @param title the title to set
	 */
	public final void setTitle(String title) {
		this.title = title;
	}
	/**
	 * @return the hit
	 */
	public final int getHit() {
		return hit;
	}
	/**
	 * @param hit the hit to set
	 */
	public final void setHit(int hit) {
		this.hit = hit;
	}
	/**
	 * @return the modifiedDate
	 */
	public final String getModifiedDate() {
		return modifiedDate;
	}
	/**
	 * @param modifiedDate the modifiedDate to set
	 */
	public final void setModifiedDate(String modifiedDate) {
		this.modifiedDate = modifiedDate;
	}
	/**
	 * @return the registeredDate
	 */
	public final String getRegisteredDate() {
		return registeredDate;
	}
	/**
	 * @param registeredDate the registeredDate to set
	 */
	public final void setRegisteredDate(String registeredDate) {
		this.registeredDate = registeredDate;
	}
}
