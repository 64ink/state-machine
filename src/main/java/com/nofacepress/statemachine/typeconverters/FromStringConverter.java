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
package com.nofacepress.statemachine.typeconverters;

/**
 * Trivial interface for conversion to something "from" a String.
 * 
 * @param <S> the class being converted
 */
public interface FromStringConverter<S> {

	/**
	 * Converts from a String to something.
	 * 
	 * @param text input text
	 * @return output thing
	 */
	S convertFromString(String text);

}
