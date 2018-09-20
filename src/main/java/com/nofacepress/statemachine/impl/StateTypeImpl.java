/*
 * Copyright 2018 No Face Press, LLC
 *
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
 */
package com.nofacepress.statemachine.impl;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.nofacepress.statemachine.StateType;
import com.nofacepress.statemachine.listener.ListenerManager;

class StateTypeImpl<S, E, C> implements StateType<S, E, C> {

	private final S id;

	private Map<E, StateTypeImpl<S, E, C>> transitionMapRO = null;

	private final Map<E, StateTypeImpl<S, E, C>> transitionMap = new HashMap<E, StateTypeImpl<S, E, C>>();

	private ListenerManager<S, E, C> listenerManager = new ListenerManager<S, E, C>();

	StateTypeImpl(S state) {
		this.id = state;
	}

	void addTransition(StateTypeImpl<S, E, C> target, E event) {
		transitionMap.put(event, target);
		transitionMapRO = null;
	}

	@Override
	public S getId() {
		return id;
	}

	StateTypeImpl<S, E, C> getInternalTransition(E event) {
		return transitionMap.get(event);
	}

	@Override
	public ListenerManager<S, E, C> getListenerManager() {
		return listenerManager;
	}

	@Override
	public StateType<S, E, C> getTransition(E event) {
		return transitionMap.get(event);
	}

	@Override
	public Map<E, ? extends StateTypeImpl<S, E, C>> getTransitions() {
		if (transitionMapRO == null) {
			transitionMapRO = Collections.unmodifiableMap(transitionMap);
		}
		return transitionMapRO;
	}

	@Override
	public boolean hasTransition(E event) {
		return transitionMap.containsKey(event);
	}

	@Override
	public boolean isEnd() {
		return transitionMap.isEmpty();
	}

}
