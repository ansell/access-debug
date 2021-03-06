/*
 * Copyright (c) 2018, Peter Ansell
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * 
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.github.ansell.access;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;

import com.github.ansell.csv.stream.CSVStream;
import joptsimple.OptionException;

/**
 * Tests for {@link AccessMapper}.
 * 
 * @author Peter Ansell p_ansell@yahoo.com
 */
public class AccessMapperTest {

	@Rule
	public ExpectedException thrown = ExpectedException.none();

	@Rule
	public TemporaryFolder tempDir = new TemporaryFolder();

	private Path testMapping;

	private Path testMappingWithHidden;

	private Path testMappingMultipleRoutes;

	private Path testMappingMissingForeignKey;

	private Path testFile;

	private Path testOutput;

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		testMapping = tempDir.newFile("test-mapping.csv").toPath();
		Files.copy(this.getClass().getResourceAsStream("/com/github/ansell/access/test-mapping.csv"), testMapping,
				StandardCopyOption.REPLACE_EXISTING);

		testMappingWithHidden = tempDir.newFile("test-mapping-with-hidden.csv").toPath();
		Files.copy(this.getClass().getResourceAsStream("/com/github/ansell/access/test-mapping-with-hidden.csv"),
				testMappingWithHidden, StandardCopyOption.REPLACE_EXISTING);

		testMappingMultipleRoutes = tempDir.newFile("test-mapping-multiple-routes.csv").toPath();
		Files.copy(this.getClass().getResourceAsStream("/com/github/ansell/access/test-mapping-multiple-routes.csv"),
				testMappingMultipleRoutes, StandardCopyOption.REPLACE_EXISTING);

		testMappingMissingForeignKey = tempDir.newFile("test-mapping-missing-foreign-key.csv").toPath();
		Files.copy(
				this.getClass()
						.getResourceAsStream("/com/github/ansell/access/test-mapping-missing-foreign-key.csv"),
				testMappingMissingForeignKey, StandardCopyOption.REPLACE_EXISTING);

		testFile = tempDir.newFile("test-source.accdb").toPath();
		Files.copy(this.getClass().getResourceAsStream("/com/github/ansell/access/test-source.accdb"), testFile,
				StandardCopyOption.REPLACE_EXISTING);

		testOutput = tempDir.newFolder("test-output").toPath();
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		// Allow time for travis to recover after each test, it tends towards
		// wrecking the joint otherwise
		Thread.sleep(1000);
		// Also suggest a GC occur before returning
		System.gc();
		// Then sleep again to give the GC a chance to succeed before Travis
		// kills it
		Thread.sleep(1000);
	}

	/**
	 * Test method for
	 * {@link com.github.ansell.csv.access.AccessMapper#main(java.lang.String[])}
	 * .
	 */
	@Test
	public final void testMainNoArgs() throws Exception {
		thrown.expect(OptionException.class);
		AccessMapper.main();
	}

	/**
	 * Test method for
	 * {@link com.github.ansell.csv.access.AccessMapper#main(java.lang.String[])}
	 * .
	 */
	@Test
	public final void testMainHelp() throws Exception {
		AccessMapper.main("--help");
	}

	/**
	 * Test method for
	 * {@link com.github.ansell.csv.access.AccessMapper#main(java.lang.String[])}
	 * .
	 */
	@Test
	public final void testMain() throws Exception {
		AccessMapper.main("--input", testFile.toAbsolutePath().toString(), "--mapping",
				testMapping.toAbsolutePath().toString(), "--output", testOutput.toAbsolutePath().toString());

		List<String> headers = new ArrayList<>();
		List<List<String>> lines = new ArrayList<>();
		try (BufferedReader reader = Files.newBufferedReader(testOutput.resolve("Mapped-Single-tblData.csv"));) {
			CSVStream.parse(reader, h -> headers.addAll(h), (h, l) -> l, l -> lines.add(l));
		}
		assertEquals(30, headers.size());
		assertEquals(85519, lines.size());
		lines.sort(Comparator.comparing(l -> l.get(0)));

		lines.get(0).forEach(k -> System.out.print("\"" + k + "\", "));

		assertEquals(Arrays.asList("1", "1", "Murray Monitoring", "2006-06-29T00:00:00", "812", "27101014", "27",
				"Insecta", "2710", "Diptera", "271010", "sf. Tanypodinae", "Genus", "Paramerina", "20", "1.0",
				"Percentage of sample surveyed (0-1) was 1.0", "20.0", "812 - Murtho", "Murray", "Murtho", "Murray",
				"34.0684", "140.8111", "-34.0684", "140.8111", "POINT(34.0684 140.8111)", "SN",
				"Murray sweep net (SM512)", "Caught in trap no. 2"), lines.get(0));
	}

	/**
	 * Test method for
	 * {@link com.github.ansell.csv.access.AccessMapper#main(java.lang.String[])}
	 * .
	 */
	@Test
	public final void testMainWithHidden() throws Exception {
		AccessMapper.main("--input", testFile.toAbsolutePath().toString(), "--mapping",
				testMappingWithHidden.toAbsolutePath().toString(), "--output", testOutput.toAbsolutePath().toString());

		List<String> headers = new ArrayList<>();
		List<List<String>> lines = new ArrayList<>();
		try (BufferedReader reader = Files.newBufferedReader(testOutput.resolve("Mapped-Single-tblData.csv"));) {
			CSVStream.parse(reader, h -> headers.addAll(h), (h, l) -> l, l -> lines.add(l));
		}
		assertEquals(28, headers.size());
		assertEquals(85519, lines.size());
		lines.sort(Comparator.comparing(l -> l.get(0)));

		lines.get(0).forEach(k -> System.out.print("\"" + k + "\", "));

		assertEquals(Arrays.asList("1", "1"
				// , "Murray Monitoring"
				, "2006-06-29T00:00:00", "812", "27101014", "27", "Insecta", "2710", "Diptera", "271010",
				"sf. Tanypodinae", "Genus", "Paramerina", "20", "1.0", "Percentage of sample surveyed (0-1) was 1.0",
				"20.0", "812 - Murtho", "Murray", "Murtho", "Murray", "34.0684", "140.8111", "-34.0684", "140.8111",
				"SN", "Murray sweep net (SM512)", "Caught in trap no. 2"), lines.get(0));

		// Trace showing that the headers were not changed correctly
		// java.lang.AssertionError: expected:
		// <[1, 1, 2006-06-29T00:00:00, 812, 27101014, 27, Insecta, 2710,
		// Diptera, 271010, sf. Tanypodinae, Genus, Paramerina, 20, 1.0,
		// Percentage of sample surveyed (0-1) was 1.0, 20.0, 812 - Murtho,
		// Murray, Murtho, Murray, 34.0684, 140.8111, -34.0684, 140.8111, SN,
		// Murray sweep net (SM512), Caught in trap no. 2]>
		// but was:
		// <[1, 1, 2006-06-29T00:00:00, 812, 27101014, 27, Insecta, 2710,
		// Diptera, 271010, sf. Tanypodinae, Genus, Paramerina, 20, 1.0,
		// Percentage of sample surveyed (0-1) was 1.0, 20.0, 812 - Murtho,
		// Murray, Murtho, Murray, 34.0684, 140.8111, -34.0684, 140.8111, SN,
		// Murray sweep net (SM512), Caught in trap no. 2, ]>

	}

	/**
	 * Test method for
	 * {@link com.github.ansell.csv.access.AccessMapper#main(java.lang.String[])}
	 * .
	 */
	@Test
	public final void testMainMultipleRoutes() throws Exception {
		thrown.expect(RuntimeException.class);
		thrown.expectMessage("Cannot map to a destination table from multiple Access mappings: ");
		AccessMapper.main("--input", testFile.toAbsolutePath().toString(), "--mapping",
				testMappingMultipleRoutes.toAbsolutePath().toString(), "--output",
				testOutput.toAbsolutePath().toString());
	}

	/**
	 * Test method for
	 * {@link com.github.ansell.csv.access.AccessMapper#main(java.lang.String[])}
	 * .
	 */
	@Test
	public final void testMainMissingForeignKey() throws Exception {
		AccessMapper.main("--input", testFile.toAbsolutePath().toString(), "--mapping",
				testMappingMissingForeignKey.toAbsolutePath().toString(), "--output",
				testOutput.toAbsolutePath().toString());

		List<String> headers = new ArrayList<>();
		List<List<String>> lines = new ArrayList<>();
		try (BufferedReader reader = Files.newBufferedReader(testOutput.resolve("Mapped-Single-tblData.csv"));) {
			CSVStream.parse(reader, h -> headers.addAll(h), (h, l) -> l, l -> lines.add(l));
		}
		assertEquals(29, headers.size());
		assertEquals(85519, lines.size());
		lines.sort(Comparator.comparing(l -> l.get(0)));

		lines.get(0).forEach(k -> System.out.print("\"" + k + "\", "));

		assertEquals(Arrays.asList("1", "1", "Murray Monitoring", "2006-06-29T00:00:00", "812", "27101014", "27",
				"Insecta", "2710", "Diptera", "271010", "sf. Tanypodinae", "Genus", "Paramerina", "20", "1.0",
				"Percentage of sample surveyed (0-1) was 1.0", "20.0", "812 - Murtho", "Murray", "Murtho", "Murray",
				"34.0684", "140.8111", "-34.0684", "140.8111", "SN", "Murray sweep net (SM512)",
				"Caught in trap no. 2"), lines.get(0));

		// Trace showing that the headers were not changed correctly
		// java.lang.AssertionError: expected:
		// <[1, 1, 2006-06-29T00:00:00, 812, 27101014, 27, Insecta, 2710,
		// Diptera, 271010, sf. Tanypodinae, Genus, Paramerina, 20, 1.0,
		// Percentage of sample surveyed (0-1) was 1.0, 20.0, 812 - Murtho,
		// Murray, Murtho, Murray, 34.0684, 140.8111, -34.0684, 140.8111, SN,
		// Murray sweep net (SM512), Caught in trap no. 2]>
		// but was:
		// <[1, 1, 2006-06-29T00:00:00, 812, 27101014, 27, Insecta, 2710,
		// Diptera, 271010, sf. Tanypodinae, Genus, Paramerina, 20, 1.0,
		// Percentage of sample surveyed (0-1) was 1.0, 20.0, 812 - Murtho,
		// Murray, Murtho, Murray, 34.0684, 140.8111, -34.0684, 140.8111, SN,
		// Murray sweep net (SM512), Caught in trap no. 2, ]>

	}

	/**
	 * Test method for
	 * {@link com.github.ansell.csv.access.AccessMapper#main(java.lang.String[])}
	 * .
	 */
	@Test
	public final void testMainDebug() throws Exception {
		AccessMapper.main("--input", testFile.toAbsolutePath().toString(), "--mapping",
				testMapping.toAbsolutePath().toString(), "--output", testOutput.toAbsolutePath().toString(), "--debug",
				Boolean.TRUE.toString(), "--add-table-prefix", Boolean.FALSE.toString());

		List<String> headers = new ArrayList<>();
		List<List<String>> lines = new ArrayList<>();
		try (BufferedReader reader = Files.newBufferedReader(testOutput.resolve("Mapped-Single-tblData.csv"));) {
			CSVStream.parse(reader, h -> headers.addAll(h), (h, l) -> l, l -> lines.add(l));
		}
		assertEquals(30, headers.size());
		assertEquals(85519, lines.size());
	}

	/**
	 * Test method for
	 * {@link com.github.ansell.csv.access.AccessMapper#main(java.lang.String[])}
	 * .
	 */
	@Test
	public final void testMainEmpty() throws Exception {
		Path testFile = tempDir.newFile("test-empty.accdb").toPath();
		Files.copy(this.getClass().getResourceAsStream("/com/github/ansell/access/test-empty.accdb"), testFile,
				StandardCopyOption.REPLACE_EXISTING);

		thrown.expect(IOException.class);
		thrown.expectMessage("Empty database file");
		AccessMapper.main("--input", testFile.toAbsolutePath().toString(), "--mapping",
				testMapping.toAbsolutePath().toString(), "--output", testOutput.toAbsolutePath().toString());
	}

	/**
	 * Test method for
	 * {@link com.github.ansell.csv.access.AccessMapper#main(java.lang.String[])}
	 * .
	 */
	@Test
	public final void testMainMissingInput() throws Exception {
		Path testEmptyDir = tempDir.newFolder("test-missing-accdb").toPath();

		thrown.expect(IOException.class);
		thrown.expectMessage("Could not find input");
		AccessMapper.main("--input", testEmptyDir.resolve("does-not-exist.accdb").toAbsolutePath().toString(),
				"--mapping", testMapping.toAbsolutePath().toString(), "--output",
				testOutput.toAbsolutePath().toString());
	}

	/**
	 * Test method for
	 * {@link com.github.ansell.csv.access.AccessMapper#main(java.lang.String[])}
	 * .
	 */
	@Test
	public final void testMainMissingMapping() throws Exception {
		Path testEmptyDir = tempDir.newFolder("test-missing-mapping").toPath();

		thrown.expect(IOException.class);
		thrown.expectMessage("Could not find mapping");
		AccessMapper.main("--input", testFile.toAbsolutePath().toString(), "--mapping",
				testEmptyDir.resolve("mapping-does-not-exist.csv").toAbsolutePath().toString(), "--output",
				testOutput.toAbsolutePath().toString());
	}

}
