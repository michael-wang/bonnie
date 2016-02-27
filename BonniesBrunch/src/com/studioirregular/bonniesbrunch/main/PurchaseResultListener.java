package com.studioirregular.bonniesbrunch.main;

import com.studioirregular.bonniesbrunch.Game.StoredPurchaseState;

public interface PurchaseResultListener {
	public void onSentFullVersionPurchaseRequest();
	public void onFullVersionPurchaseStateChanged(StoredPurchaseState fullVersionPurchaseState);
}
