package com.lody.virtual.client.hook.patchs.restriction;

import com.lody.virtual.client.hook.base.Patch;
import com.lody.virtual.client.hook.base.PatchObject;
import com.lody.virtual.client.hook.binders.HookRestrictionBinder;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.IRestrictionsManager;
import android.os.Build;
import android.os.ServiceManager;

/**
 * @author Lody
 */
@TargetApi(Build.VERSION_CODES.LOLLIPOP)
@Patch({Hook_NotifyPermissionResponse.class, Hook_RequestPermission.class, Hook_GetApplicationRestrictions.class,})
public class RestrictionPatch extends PatchObject<IRestrictionsManager, HookRestrictionBinder> {

	@Override
	protected HookRestrictionBinder initHookObject() {
		return new HookRestrictionBinder();
	}

	@Override
	public void inject() throws Throwable {
		getHookObject().injectService(Context.RESTRICTIONS_SERVICE);

	}

	@Override
	public boolean isEnvBad() {
		return ServiceManager.getService(Context.RESTRICTIONS_SERVICE) != getHookObject();
	}

}
