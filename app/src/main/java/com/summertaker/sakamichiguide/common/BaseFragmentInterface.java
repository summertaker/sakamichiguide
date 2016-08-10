package com.summertaker.sakamichiguide.common;

public interface BaseFragmentInterface {

	public void refresh(String articleId);
    public boolean canGoBack();
    public void goBack();
}
