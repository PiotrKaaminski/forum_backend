package com.forum.forum_backend.dtos;

import java.util.List;

public class PaginatedResponse<T> {

	private int count;
	private List<T> results;
	private int page;
	private int pagesAmount;
	private boolean isFirst;
	private boolean isLast;

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public List<T> getResults() {
		return results;
	}

	public void setResults(List<T> results) {
		this.results = results;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public int getPagesAmount() {
		return pagesAmount;
	}

	public void setPagesAmount(int pagesAmount) {
		this.pagesAmount = pagesAmount;
	}

	public boolean isFirst() {
		return isFirst;
	}

	public void setFirst(boolean first) {
		isFirst = first;
	}

	public boolean isLast() {
		return isLast;
	}

	public void setLast(boolean last) {
		isLast = last;
	}
}
