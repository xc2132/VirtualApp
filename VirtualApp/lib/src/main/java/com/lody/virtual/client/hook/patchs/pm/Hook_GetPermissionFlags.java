package com.lody.virtual.client.hook.patchs.pm;

import java.lang.reflect.Method;

import com.lody.virtual.client.hook.base.Hook;

import android.annotation.TargetApi;
import android.os.Build;

/**
 * @author Lody
 *
 */
@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
/* package */ class Hook_GetPermissionFlags extends Hook<PackageManagerPatch> {
	/**
	 * 这个构造器必须有,用于依赖注入.
	 *
	 * @param patchObject
	 *            注入对象
	 */
	public Hook_GetPermissionFlags(PackageManagerPatch patchObject) {
		super(patchObject);
	}

	@Override
	public String getName() {
		return "getPermissionFlags";
	}

	@Override
	public Object onHook(Object who, Method method, Object... args) throws Throwable {
		// TODO
		return method.invoke(who, args);
	}

}
