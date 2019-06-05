/*
 * Copyright 2002-2019 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.jms.config;

import org.junit.Before;
import org.junit.Test;

import org.springframework.beans.factory.support.StaticListableBeanFactory;

import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author Stephane Nicoll
 */
public class JmsListenerEndpointRegistrarTests {

	private final JmsListenerEndpointRegistrar registrar = new JmsListenerEndpointRegistrar();

	private final JmsListenerEndpointRegistry registry = new JmsListenerEndpointRegistry();

	private final JmsListenerContainerTestFactory containerFactory = new JmsListenerContainerTestFactory();


	@Before
	public void setup() {
		this.registrar.setEndpointRegistry(this.registry);
		this.registrar.setBeanFactory(new StaticListableBeanFactory());
	}


	@Test
	public void registerNullEndpoint() {
		assertThatIllegalArgumentException().isThrownBy(() ->
				this.registrar.registerEndpoint(null, this.containerFactory));
	}

	@Test
	public void registerNullEndpointId() {
		assertThatIllegalArgumentException().isThrownBy(() ->
				this.registrar.registerEndpoint(new SimpleJmsListenerEndpoint(), this.containerFactory));
	}

	@Test
	public void registerEmptyEndpointId() {
		SimpleJmsListenerEndpoint endpoint = new SimpleJmsListenerEndpoint();
		endpoint.setId("");

		assertThatIllegalArgumentException().isThrownBy(() ->
				this.registrar.registerEndpoint(endpoint, this.containerFactory));
	}

	@Test
	public void registerNullContainerFactoryIsAllowed() throws Exception {
		SimpleJmsListenerEndpoint endpoint = new SimpleJmsListenerEndpoint();
		endpoint.setId("some id");
		this.registrar.setContainerFactory(this.containerFactory);
		this.registrar.registerEndpoint(endpoint, null);
		this.registrar.afterPropertiesSet();
		assertNotNull("Container not created", this.registry.getListenerContainer("some id"));
		assertEquals(1, this.registry.getListenerContainers().size());
		assertEquals("some id", this.registry.getListenerContainerIds().iterator().next());
	}

	@Test
	public void registerNullContainerFactoryWithNoDefault() throws Exception {
		SimpleJmsListenerEndpoint endpoint = new SimpleJmsListenerEndpoint();
		endpoint.setId("some id");
		this.registrar.registerEndpoint(endpoint, null);

		assertThatIllegalStateException().isThrownBy(() ->
				this.registrar.afterPropertiesSet())
			.withMessageContaining(endpoint.toString());
	}

	@Test
	public void registerContainerWithoutFactory() throws Exception {
		SimpleJmsListenerEndpoint endpoint = new SimpleJmsListenerEndpoint();
		endpoint.setId("myEndpoint");
		this.registrar.setContainerFactory(this.containerFactory);
		this.registrar.registerEndpoint(endpoint);
		this.registrar.afterPropertiesSet();
		assertNotNull("Container not created", this.registry.getListenerContainer("myEndpoint"));
		assertEquals(1, this.registry.getListenerContainers().size());
		assertEquals("myEndpoint", this.registry.getListenerContainerIds().iterator().next());
	}

}