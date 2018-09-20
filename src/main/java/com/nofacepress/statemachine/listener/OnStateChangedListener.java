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
package com.nofacepress.statemachine.listener;

import com.nofacepress.statemachine.StateMachineInstance;
import com.nofacepress.statemachine.StateType;

/**
 * Listens for notification that a state change is happening. It is not safe to
 * change the state from here. This is an ideal place to persist the state.
 * 
 * @param <S> The state class
 * @param <E> The event class
 * @param <C> The context class
 */
public interface OnStateChangedListener<S, E, C> {

	/**
	 * Called while a state change is happening. It is NOT safe to change the state
	 * form here. If a state one needs to fire a new event, use the
	 * AfterStateChangedListener. This is an excellent place to persist state data.
	 * 
	 * @param instance the instance of the state machine.
	 * @param from     the original state.
	 * @param to       the new state.
	 * @param event    the event that cause the transition.
	 */
	void onStateChanged(StateMachineInstance<S, E, C> instance, StateType<S, E, C> from, StateType<S, E, C> to,
			E event);

}
