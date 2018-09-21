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
package com.nofacepress.test.statemachine;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.xml.stream.XMLStreamException;

import org.junit.Test;

import com.nofacepress.statemachine.StateMachineGraph;
import com.nofacepress.statemachine.StateType;
import com.nofacepress.statemachine.scxml.SCXMLManager;

public class ScxmlTest {

	public String scxmlExample = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
			"<scxml xmlns=\"http://www.w3.org/2005/07/scxml\" version=\"1.0\" initial=\"STATE_2\">" +
			" <state id=\"STATE_1\">" +
			"  <transition event=\"EVENT_1\" target=\"STATE_2\"/>" +
			" </state>" +
			" <state id=\"STATE_2\">\r\n" +
			"  <transition event=\"EVENT_2\" target=\"STATE_3\"/>" +
			"  <transition event=\"EVENT_3\" target=\"STATE_1\"/>" +
			" </state>" +
			" <state id=\"STATE_3\">\r\n" +
			"  <transition event=\"EVENT_1\" target=\"STATE_1\"/>" +
			"  <transition event=\"EVENT_3\" target=\"STATE_4\"/>" +
			" </state>" +
			" <state id=\"STATE_4\">" +
			"  <transition event=\"EVENT_1\" target=\"STATE_1\"/>" +
			" </state>" +
			"</scxml>";

	@Test
	public void test_loadEnums() throws IOException, XMLStreamException {

		ByteArrayInputStream stream = new ByteArrayInputStream(scxmlExample.getBytes());

		StateMachineGraph<TestStates, TestEvents, String> graph = SCXMLManager.loadEnumGraph(stream,
				TestStates.class, TestEvents.class);

		validateEnumCase(graph);
	}

	@Test
	public void test_saveEnums() throws IOException, XMLStreamException {

		ByteArrayInputStream instream = new ByteArrayInputStream(scxmlExample.getBytes());
		StateMachineGraph<TestStates, TestEvents, String> graph = SCXMLManager.loadEnumGraph(instream,
				TestStates.class, TestEvents.class);

		ByteArrayOutputStream outstream = new ByteArrayOutputStream();
		SCXMLManager.saveEnumGraph(graph, outstream);

		instream = new ByteArrayInputStream(outstream.toByteArray());
		graph = SCXMLManager.loadEnumGraph(instream,
				TestStates.class, TestEvents.class);

		validateEnumCase(graph);
	}

	public void validateEnumCase(StateMachineGraph<TestStates, TestEvents, String> graph) {

		assertEquals(graph.getInitialState(), TestStates.STATE_2);

		assertEquals(graph.getStates().size(), 4);
		assertEquals(graph.getStateType(TestStates.STATE_1).getId(), TestStates.STATE_1);
		assertEquals(graph.getStateType(TestStates.STATE_2).getId(), TestStates.STATE_2);
		assertEquals(graph.getStateType(TestStates.STATE_3).getId(), TestStates.STATE_3);
		assertEquals(graph.getStateType(TestStates.STATE_4).getId(), TestStates.STATE_4);

		StateType<TestStates, TestEvents, String> state = graph.getStateType(TestStates.STATE_1);
		assertEquals(state.getTransitions().size(), 1);
		assertEquals(state.getTransition(TestEvents.EVENT_1).getId(), TestStates.STATE_2);

		state = graph.getStateType(TestStates.STATE_2);
		assertEquals(state.getTransitions().size(), 2);
		assertEquals(state.getTransition(TestEvents.EVENT_2).getId(), TestStates.STATE_3);
		assertEquals(state.getTransition(TestEvents.EVENT_3).getId(), TestStates.STATE_1);

		state = graph.getStateType(TestStates.STATE_3);
		assertEquals(state.getTransitions().size(), 2);
		assertEquals(state.getTransition(TestEvents.EVENT_1).getId(), TestStates.STATE_1);
		assertEquals(state.getTransition(TestEvents.EVENT_3).getId(), TestStates.STATE_4);

		state = graph.getStateType(TestStates.STATE_4);
		assertEquals(state.getTransitions().size(), 1);
		assertEquals(state.getTransition(TestEvents.EVENT_1).getId(), TestStates.STATE_1);

	}

	@Test
	public void test_loadStrings() throws IOException, XMLStreamException {

		ByteArrayInputStream stream = new ByteArrayInputStream(scxmlExample.getBytes());
		StateMachineGraph<String, String, String> graph = SCXMLManager.loadStringGraph(stream);

		validateStringCase(graph);

	}

	@Test
	public void test_saveStrings() throws IOException, XMLStreamException {

		ByteArrayInputStream instream = new ByteArrayInputStream(scxmlExample.getBytes());
		StateMachineGraph<String, String, String> graph = SCXMLManager.loadStringGraph(instream);

		ByteArrayOutputStream outstream = new ByteArrayOutputStream();
		SCXMLManager.saveStringGraph(graph, outstream);

		instream = new ByteArrayInputStream(outstream.toByteArray());
		graph = SCXMLManager.loadStringGraph(instream);

		validateStringCase(graph);

	}

	public void validateStringCase(StateMachineGraph<String, String, String> graph) {

		assertEquals(graph.getInitialState(), "STATE_2");

		assertEquals(graph.getStates().size(), 4);
		assertEquals(graph.getStateType("STATE_1").getId(), "STATE_1");
		assertEquals(graph.getStateType("STATE_2").getId(), "STATE_2");
		assertEquals(graph.getStateType("STATE_3").getId(), "STATE_3");
		assertEquals(graph.getStateType("STATE_4").getId(), "STATE_4");

		StateType<String, String, String> state = graph.getStateType("STATE_1");
		assertEquals(state.getTransitions().size(), 1);
		assertEquals(state.getTransition("EVENT_1").getId(), "STATE_2");

		state = graph.getStateType("STATE_2");
		assertEquals(state.getTransitions().size(), 2);
		assertEquals(state.getTransition("EVENT_2").getId(), "STATE_3");
		assertEquals(state.getTransition("EVENT_3").getId(), "STATE_1");

		state = graph.getStateType("STATE_3");
		assertEquals(state.getTransitions().size(), 2);
		assertEquals(state.getTransition("EVENT_1").getId(), "STATE_1");
		assertEquals(state.getTransition("EVENT_3").getId(), "STATE_4");

		state = graph.getStateType("STATE_4");
		assertEquals(state.getTransitions().size(), 1);
		assertEquals(state.getTransition("EVENT_1").getId(), "STATE_1");

	}

}
