package tests;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import application.Dummy;

class TestDummy {

	@Test
	void test() {
		Dummy d = new Dummy();
		assertEquals(d.add(1,2), 3);
	}

}
