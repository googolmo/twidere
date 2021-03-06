/*
 *				Twidere - Twitter client for Android
 * 
 * Copyright (C) 2012 Mariotaku Lee <mariotaku.lee@gmail.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.mariotaku.twidere.loader;

import static org.mariotaku.twidere.util.Utils.getTwitterInstance;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;

import org.mariotaku.twidere.Constants;
import org.mariotaku.twidere.model.ParcelableStatus;

import twitter4j.Twitter;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.content.AsyncTaskLoader;

public abstract class ParcelableStatusesLoader extends AsyncTaskLoader<List<ParcelableStatus>> implements Constants {

	private final Twitter mTwitter;
	private final long mAccountId;
	private final List<ParcelableStatus> mData;
	private final SharedPreferences mPreferences;
	private boolean mForceSSLConnection;

	public ParcelableStatusesLoader(Context context, long account_id, List<ParcelableStatus> data) {
		super(context);
		mPreferences = context.getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
		mForceSSLConnection = mPreferences.getBoolean(PREFERENCE_KEY_FORCE_SSL_CONNECTION, false);
		mTwitter = getTwitterInstance(context, account_id, true);
		mAccountId = account_id;
		mData = data != null ? data : new ArrayList<ParcelableStatus>();
	}

	public boolean containsStatus(long status_id) {
		for (final ParcelableStatus status : mData) {
			if (status.status_id == status_id) return true;
		}
		return false;
	}

	public synchronized boolean deleteStatus(long status_id) {
		try {
			final ArrayList<ParcelableStatus> data_to_remove = new ArrayList<ParcelableStatus>();
			for (final ParcelableStatus status : mData) {
				if (status.status_id == status_id) {
					data_to_remove.add(status);
				}
			}
			return mData.removeAll(data_to_remove);
		} catch (final ConcurrentModificationException e) {
			// This shouldn't happen.
		}
		return false;
	}

	public long getAccountId() {
		return mAccountId;
	}

	public List<ParcelableStatus> getData() {
		return mData;
	}

	public Twitter getTwitter() {
		return mTwitter;
	}

	public boolean isForceSSLConnection() {
		return mForceSSLConnection;
	}

	@Override
	public abstract List<ParcelableStatus> loadInBackground();

	@Override
	public void onStartLoading() {
		forceLoad();
	}

	public void reloadConnectivitySettings() {
		mForceSSLConnection = mPreferences.getBoolean(PREFERENCE_KEY_FORCE_SSL_CONNECTION, false);
	}

}
