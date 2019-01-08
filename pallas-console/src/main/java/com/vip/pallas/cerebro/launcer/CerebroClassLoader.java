/**
 * Copyright 2019 vip.com.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * </p>
 */

package com.vip.pallas.cerebro.launcer;

import java.net.URL;
import java.net.URLClassLoader;

public class CerebroClassLoader extends URLClassLoader {

	public CerebroClassLoader(URL[] urls) {
		super(urls, null);
	}

	@Override
	protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
		if (name == null) {
			return null;
		}
		synchronized (getClassLoadingLock(name)) {
			Class<?> findClass = findLoadedClass(name);

			if (findClass == null) {
				findClass = super.loadClass(name, resolve);
			}
			return findClass;
		}
	}
}