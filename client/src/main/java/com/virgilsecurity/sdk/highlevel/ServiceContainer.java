/*
 * Copyright (c) 2016, Virgil Security, Inc.
 *
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * * Neither the name of virgil nor the names of its
 *   contributors may be used to endorse or promote products derived from
 *   this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.virgilsecurity.sdk.highlevel;

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.virgilsecurity.sdk.client.exceptions.ServiceIsAlreadyRegistered;
import com.virgilsecurity.sdk.client.exceptions.ServiceNotRegisteredException;
import com.virgilsecurity.sdk.crypto.exception.VirgilException;

/**
 * Service container allows to register service by type and get service by type.
 * Some kind of IoC.
 *
 * @author Andrii Iakovenko
 *
 */
public class ServiceContainer {

	private Map<Class<?>, RegisteredObject> registeredObjects;

	/**
	 * Create a new instance of {@code ServiceContainer}
	 *
	 */
	public ServiceContainer() {
		registeredObjects = new ConcurrentHashMap<>();
	}

	public void registerSingleton(Class<?> resolvedType, Class<?> instanceType) {
		register(resolvedType, new RegisteredObject(instanceType));
	}

	public void registerInstance(Class<?> resolvedType, Object instance) {
		register(resolvedType, new RegisteredObject(instance.getClass(), instance));
	}

	@SuppressWarnings("unchecked")
	public <T> T resolve(final Class<T> type) {
		RegisteredObject registeredObject = registeredObjects.get(type);
		if (registeredObject == null) {
			throw new ServiceNotRegisteredException(
					String.format("The type %1$s has not been registered", type.getCanonicalName()));
		}

		return (T) getInstance(registeredObject);
	}

	private Object getInstance(RegisteredObject registeredObject) {
		if (registeredObject.getInstance() != null) {
			return registeredObject.getInstance();
		}

		try {
			Constructor<?>[] constructors = registeredObject.getType().getConstructors();
			Arrays.sort(constructors, new Comparator<Constructor<?>>() {

				@Override
				public int compare(Constructor<?> o1, Constructor<?> o2) {
					return o1.getParameterCount() - o2.getParameterCount();
				}
			});

			Constructor<?> constructor = constructors[0];
			Object instance = constructor.newInstance(resolveConstructorParameters(constructor));
			registeredObject.setInstance(instance);

			return instance;
		} catch (Exception e) {
			throw new VirgilException(e);
		}
	}

	private Object[] resolveConstructorParameters(Constructor<?> constructor) {
		Class<?>[] types = constructor.getParameterTypes();
		Object[] params = new Object[types.length];
		int i = 0;
		for (Class<?> type : types) {
			params[i++] = resolve(type);
		}
		return params;
	}

	public void remove(Type type) {
		registeredObjects.remove(type);
	}

	public void clear() {
		registeredObjects.clear();
	}

	private void register(Class<?> resolvedType, RegisteredObject registeredObject) {
		if (registeredObjects.containsKey(resolvedType)) {
			throw new ServiceIsAlreadyRegistered();
		}
		registeredObjects.put(resolvedType, registeredObject);
	}

	private class RegisteredObject {
		private Class<?> type;
		private Object instance;

		/**
		 * Create a new instance of {@code RegisteredObject}
		 *
		 * @param type
		 */
		public RegisteredObject(Class<?> type) {
			this.type = type;
		}

		/**
		 * Create a new instance of {@code RegisteredObject}
		 *
		 * @param type
		 * @param instance
		 */
		public RegisteredObject(Class<?> type, Object instance) {
			this.type = type;
			this.instance = instance;
		}

		/**
		 * @return the instance
		 */
		public Object getInstance() {
			return instance;
		}

		/**
		 * @param instance
		 *            the instance to set
		 */
		public void setInstance(Object instance) {
			this.instance = instance;
		}

		/**
		 * @return the type
		 */
		public Class<?> getType() {
			return type;
		}
	}

}
