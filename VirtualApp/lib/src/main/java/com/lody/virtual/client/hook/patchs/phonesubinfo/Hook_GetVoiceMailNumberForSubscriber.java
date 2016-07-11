package com.lody.virtual.client.hook.patchs.phonesubinfo;

/**
 * @author Lody
 *
 */
/* package */ class Hook_GetVoiceMailNumberForSubscriber extends BaseHook_ReplacePkgName {
	/**
	 * 这个构造器必须有,用于依赖注入.
	 *
	 * @param patchObject
	 *            注入对象
	 */
	public Hook_GetVoiceMailNumberForSubscriber(PhoneSubInfoPatch patchObject) {
		super(patchObject);
	}

	@Override
	protected int getIndex() {
		return -2;
	}

	@Override
	public String getName() {
		return "getVoiceMailNumberForSubscriber";
	}
}
