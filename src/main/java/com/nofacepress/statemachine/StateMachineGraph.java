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

public interface StateMachineGraph<S, E, C> {

	StateType<S, E, C> addState(S state);

	void addTransition(S fromState, S toState, E event);

	StateMachineGraph<S, E, C> dup(boolean includeListeners);

	S getInitialState();

	ListenerManager<S, E, C> getListenerManager();

	StateType<S, E, C> getStateInfo(S state);

	Collection<? extends StateType<S, E, C>> getStates();

	void setInitialState(S state);

}