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
package com.nofacepress.statemachine.lucidchart;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

import com.nofacepress.csv4180.CSVWriter;
import com.nofacepress.statemachine.StateMachineGraph;
import com.nofacepress.statemachine.plantxml.BaseManager;
import com.nofacepress.statemachine.typeconverters.ToStringConverter;

/**
 * Creates a LucidChart state chart based on information probed from a State
 * Machine. This was created to find errors when setting up the state machine.
 */
public class LucidChartManager extends BaseManager {

	/**
	 * Creates a LucidChart state chart
	 * 
	 * @param        <S> the state type
	 * @param        <E> the event type
	 * @param        <C> the context type
	 * @param graph  the StateMachineGraph instance to probe.
	 * @param title  the title to put on the chart, null is ok for no title.
	 * @param outputWriter the output to write to
	 * @param Sconv  the object for converting from a state to a String
	 * @param Econv  the object for converting from an event to a String
	 * @throws IOException on file I/O errors
	 */
	@Override
	public <S, E, C> void saveGraph(StateMachineGraph<S, E, C> graph, String title, Writer outputWriter,
			ToStringConverter<S> Sconv, ToStringConverter<E> Econv) throws IOException {

		List<StateInfo> lstates = analyzeStateMachine(graph, Sconv, Econv);

		@SuppressWarnings("resource")
		CSVWriter writer = new CSVWriter(outputWriter);

		// header row
		writer.writeField("Id");
		writer.writeField("Name");
		writer.writeField("Shape Library");
		writer.writeField("Page ID");
		writer.writeField("Contained B");
		writer.writeField("Line Source");
		writer.writeField("Line Destination");
		writer.writeField("Source Arrow");
		writer.writeField("Destination Arrow");
		writer.writeField("Text Area 1");
		writer.writeField("Text Area 2");
		writer.writeField("Text Area 3");
		writer.newLine();

		// header row
		writer.writeField("1");
		writer.writeField("Page");
		writer.writeField("");
		writer.writeField("");
		writer.writeField("");
		writer.writeField("");
		writer.writeField("");
		writer.writeField("");
		writer.writeField("");
		writer.writeField(title == null ? "State Chart" : title);
		writer.writeField("");
		writer.writeField("");
		writer.newLine();

		final int INDEX_OFFSET = 2;

		// write out the states
		for (StateInfo source : lstates) {
			String label = source.name;
			if (source.qualifier != null) {
				label = String.format("%s\n<%s>", source.name, source.qualifier.name());
			}

			writer.writeField("" + (source.index + INDEX_OFFSET));
			writer.writeField("State Name");
			writer.writeField("UML");
			writer.writeField("1");
			writer.writeField("");
			writer.writeField("");
			writer.writeField("");
			writer.writeField("");
			writer.writeField("");
			writer.writeField(label);
			writer.writeField("");
			writer.writeField("");
			writer.newLine();

		}

		int lineCounter = INDEX_OFFSET + lstates.size();
		// write out the transitions
		for (StateInfo source : lstates) {

			for (TransitionInfo t : source.transitions) {

				writer.writeField("" + (lineCounter++));
				writer.writeField("Line");
				writer.writeField("");
				writer.writeField("1");
				writer.writeField("");
				writer.writeField("" + (source.index + INDEX_OFFSET));
				writer.writeField("" + (t.target.index + INDEX_OFFSET));
				writer.writeField("None");
				writer.writeField("Arrow");
				writer.writeField(t.event);
				writer.writeField("");
				writer.writeField("");
				writer.newLine();

			}

		}
		writer.flush();
	}

}
