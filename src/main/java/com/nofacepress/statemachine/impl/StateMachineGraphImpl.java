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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map.Entry;

import com.nofacepress.statemachine.StateMachineGraph;
import com.nofacepress.statemachine.StateType;
import com.nofacepress.statemachine.listener.ListenerManager;

public class StateMachineGraphImpl<S, E, C> implements StateMachineGraph<S, E, C> {
	private final HashMap<S, StateTypeImpl<S, E, C>> states = new HashMap<S, StateTypeImpl<S, E, C>>();
	private Collection<StateTypeImpl<S, E, C>> statesRO = null;
	private ListenerManager<S, E, C> listenerManager = new ListenerManager<S, E, C>();
	private S initialState = null;

	@Override
	public StateType<S, E, C> addState(S state) {
		return getOrCreateState(state);
	}

	@Override
	public void addTransition(S sourceState, S targetState, E event) {
		StateTypeImpl<S, E, C> source = getOrCreateState(sourceState);
		StateTypeImpl<S, E, C> target = getOrCreateState(targetState);
		source.addTransition(target, event);
	}

	@Override
	public StateMachineGraph<S, E, C> dup(boolean includeListeners) {

		StateMachineGraphImpl<S, E, C> copy = new StateMachineGraphImpl<S, E, C>();
		copy.initialState = initialState;
		if (includeListeners) {
			copy.listenerManager.copyListeners(listenerManager);
		}
		for (Entry<S, StateTypeImpl<S, E, C>> entry : states.entrySet()) {
			StateTypeImpl<S, E, C> myState = entry.getValue();
			StateTypeImpl<S, E, C> copyState = copy.getOrCreateState(myState.getId());
			copy.states.put(entry.getKey(), copyState);
			for (Entry<E, ? extends StateTypeImpl<S, E, C>> sEntry : myState.getTransitions().entrySet()) {
				StateTypeImpl<S, E, C> copyTarget = copy.getOrCreateState(sEntry.getValue().getId());
				copyState.addTransition(copyTarget, sEntry.getKey());
			}
			if (includeListeners) {
				copyState.getListenerManager().copyListeners(copyState.getListenerManager());
			}
		}
		return this;

	}

	@Override
	public S getInitialState() {
		return initialState;
	}

	@Override
	public ListenerManager<S, E, C> getListenerManager() {
		return listenerManager;
	}

	private StateTypeImpl<S, E, C> getOrCreateState(S state) {
		StateTypeImpl<S, E, C> info = states.get(state);
		if (info == null) {
			info = new StateTypeImpl<S, E, C>(state);
			states.put(state, info);
			statesRO = null;
			if (initialState == null) {
				initialState = state;
			}
		}
		return info;
	}

	@Override
	public StateType<S, E, C> getStateType(S state) {
		return states.get(state);
	}

	@Override
	public Collection<? extends StateType<S, E, C>> getStates() {
		if (statesRO == null) {
			statesRO = Collections.unmodifiableCollection(states.values());
		}
		return statesRO;
	}

	@Override
	public void setInitialState(S state) {
		getOrCreateState(state);
		initialState = state;
	}

}
