package com.lody.virtual.service;

import java.util.List;

import com.lody.virtual.helper.proto.VComponentInfo;
import com.lody.virtual.helper.utils.XLog;

import android.app.IActivityManager;
import android.content.pm.ProviderInfo;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;

/**
 * @author Lody
 *
 */
public class VContentServiceImpl extends IContentManager.Stub {

	private static final String TAG = VContentServiceImpl.class.getSimpleName();

	private static final VContentServiceImpl sService = new VContentServiceImpl();

	private final ProviderList mProviderList = new ProviderList();

	public static VContentServiceImpl getService() {
		return sService;
	}

	@Override
	public IActivityManager.ContentProviderHolder getContentProvider(String auth) {
		if (TextUtils.isEmpty(auth)) {
			return null;
		}
		ProviderInfo providerInfo = VPackageServiceImpl.getService().resolveContentProvider(auth, 0);
		if (providerInfo == null) {
			XLog.d(TAG, "Unable to find Provider who named %s.", auth);
			return null;
		}
		IActivityManager.ContentProviderHolder holder = mProviderList.getHolder(auth);
		if (holder != null) {
			return holder;
		}
		try {
			XLog.d(TAG, "Installing %s...", providerInfo.authority);
			VProcessServiceImpl.getService().installComponent(VComponentInfo.wrap(providerInfo));
			IActivityManager.ContentProviderHolder getResult = mProviderList.getHolder(auth);
			if (getResult == null) {
				XLog.w(TAG, "Unable to getContentProvider : " + auth);
			}
			return getResult;
		} catch (Throwable e) {
			e.printStackTrace();
		}

		return null;
	}

	@Override
	public void publishContentProviders(List<IActivityManager.ContentProviderHolder> holderList) {

		if (holderList == null || holderList.isEmpty()) {
			return;
		}
		for (IActivityManager.ContentProviderHolder holder : holderList) {

			ProviderInfo providerInfo = holder.info;

			if (holder.provider == null || providerInfo == null || providerInfo.authority == null) {
				continue;
			}

			final String auth = providerInfo.authority;
			IBinder pb = holder.provider.asBinder();
			if (!linkProviderDied(auth, pb)) {
				XLog.e(TAG, "Link Provider(%s) died failed.", auth);
			}

			synchronized (mProviderList) {
				String auths[] = auth.split(";");
				for (String oneAuth : auths) {
					mProviderList.putHolder(oneAuth, holder);
				}
			}
		}
	}

	private boolean linkProviderDied(final String auth, IBinder binder) {
		if (binder == null) {
			return false;
		}
		try {
			binder.linkToDeath(new IBinder.DeathRecipient() {
				@Override
				public void binderDied() {
					synchronized (mProviderList) {
						mProviderList.removeAuth(auth);
					}
				}
			}, 0);
			return true;
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return false;
	}

}
