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
package com.nofacepress.statemachine;

import java.util.Map;

import com.nofacepress.statemachine.listener.ListenerManager;

/**
 * Wrapper for each unique state. Contains listener information and transitions.
 * 
 * @param <S> The state class
 * @param <E> The event class
 * @param <C> The context class
 */
public interface StateType<S, E, C> {

	/**
	 * Returns the unique state id.
	 * 
	 * @return the state being wrapped
	 */
	S getId();

	/**
	 * Returns the listener manager for this state.
	 * 
	 * @return the listener manager
	 */
	ListenerManager<S, E, C> getListenerManager();

	/**
	 * Returns the target state for a given transition event.
	 * 
	 * @param event the event
	 * @return the state or null if not found
	 */
	StateType<S, E, C> getTransition(E event);

	/**
	 * Returns a read-only map of events and transition states relative to this
	 * state.
	 * 
	 * @return the map of transitions
	 */
	public Map<E, ? extends StateType<S, E, C>> getTransitions();

	/**
	 * Returns if an event could cause a state transition.
	 * 
	 * @param event the event
	 * @return true if the event has an associated transition
	 */
	boolean hasTransition(E event);

	/**
	 * Checks if there are are any possible transitions.
	 * 
	 * @return true if there are no more transitions possible.
	 */
	boolean isEnd();

}
