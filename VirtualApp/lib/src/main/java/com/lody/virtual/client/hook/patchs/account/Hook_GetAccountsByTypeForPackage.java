package com.lody.virtual.client.hook.patchs.account;

import com.lody.virtual.client.hook.base.Hook;
import com.lody.virtual.client.hook.utils.HookUtils;

import java.lang.reflect.Method;

/**
 * @author Lody
 */

public class Hook_GetAccountsByTypeForPackage extends Hook<AccountManagerPatch> {
    /**
     * 这个构造器必须有,用于依赖注入.
     *
     * @param patchObject 注入对象
     */
    public Hook_GetAccountsByTypeForPackage(AccountManagerPatch patchObject) {
        super(patchObject);
    }

    @Override
    public String getName() {
        return "getAccountsByTypeForPackage";
    }

    @Override
    public Object onHook(Object who, Method method, Object... args) throws Throwable {
        HookUtils.replaceFirstAppPkg(args);
        HookUtils.replaceLastAppPkg(args);
        return method.invoke(who, args);
    }
}
