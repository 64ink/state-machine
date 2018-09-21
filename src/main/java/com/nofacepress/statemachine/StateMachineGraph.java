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

import java.util.Collection;

import com.nofacepress.statemachine.listener.ListenerManager;

/**
 * Primary container that manages a state machine graph/model. An instance of
 * this is shared by StateMachineInstance's as a read-only reference.
 * 
 * In addition to the model, the listeners are also defined here. If different
 * instanced are supposed to have different listeners, use dup() to make copies.
 * 
 * @param <S> The state class
 * @param <E> The event class
 * @param <C> The context class
 */
public interface StateMachineGraph<S, E, C> {

	/**
	 * Adds a state to the graph. States that are defined with transitions are
	 * automatically created. If a state already exists, this method will do
	 * nothing.
	 * 
	 * @param state the state to add
	 * @return the StateType associated with it.
	 */
	StateType<S, E, C> addState(S state);

	/**
	 * Adds a transition to the graph. Undefined states are automatically added to
	 * the graph.
	 * 
	 * @param source the initial state
	 * @param target the target state
	 * @param event  the event causing the transition
	 */
	void addTransition(S source, S target, E event);

	/**
	 * Makes a duplicate copy of the graph.
	 * 
	 * @param includeListeners if true, listeners are included in the copy.
	 * @return the new graph
	 */
	StateMachineGraph<S, E, C> dup(boolean includeListeners);

	/**
	 * Returns the initial state that was configured. By default, the first state
	 * added is considered initial unless one is explicitly set.
	 * 
	 * @return the initial state
	 */
	S getInitialState();

	/**
	 * Returns the listener manager for adding listeners to be called during "all"
	 * state changes.
	 * 
	 * @return the listener manager
	 */
	ListenerManager<S, E, C> getListenerManager();

	/**
	 * Returns the StateType wrapper associated with a particular state.
	 * 
	 * @param state the state to lookup
	 * @return the state type wrapper or null if not found
	 */
	StateType<S, E, C> getStateType(S state);

	/**
	 * Returns a read-only list of all states.
	 * 
	 * @return a list of all states
	 */
	Collection<? extends StateType<S, E, C>> getStates();

	/**
	 * Sets the initial state, adding it if needed.
	 * 
	 * @param state the state to start with by default
	 */
	void setInitialState(S state);

}