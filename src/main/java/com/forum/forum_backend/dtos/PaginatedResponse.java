package com.forum.forum_backend.dtos;

import java.util.List;

public class PaginatedResponse<T> {

	private int count;
	private List<T> results;

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

}
