package com.sameaccord.plateful.platform;

import java.util.ServiceLoader;

import com.sameaccord.plateful.platform.services.IPlatformHelper;

/**
 * Finds the current loader's implementation of the platform hooks. Each loader module lists its
 * implementation in META-INF/services/com.sameaccord.plateful.platform.services.IPlatformHelper.
 */
public final class Services {
	public static final IPlatformHelper PLATFORM = load(IPlatformHelper.class);

	private Services() {
	}

	public static <T> T load(Class<T> type) {
		return ServiceLoader.load(type, Services.class.getClassLoader())
			.findFirst()
			.orElseThrow(() -> new IllegalStateException("No implementation found for " + type.getName()));
	}
}
