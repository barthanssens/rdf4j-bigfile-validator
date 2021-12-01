/*
 * Copyright (c) 2021, FPS BOSA DG DT
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package be.bosa.dt.rbv;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;

import org.eclipse.rdf4j.rio.RDFFormat;
import org.eclipse.rdf4j.rio.RDFParser;
import org.eclipse.rdf4j.rio.Rio;
import org.eclipse.rdf4j.rio.RioSetting;
import org.eclipse.rdf4j.rio.helpers.BasicParserSettings;
import org.eclipse.rdf4j.rio.helpers.NTriplesParserSettings;

/**
 *
 * @author Bart Hanssens
 */
public class Main {
	private final static Logger LOG = Logger.getGlobal();

	private final static RioSetting[] NON_FATAL_ERRORS = new RioSetting[] {
			BasicParserSettings.FAIL_ON_UNKNOWN_DATATYPES,
			BasicParserSettings.FAIL_ON_UNKNOWN_DATATYPES,
			BasicParserSettings.FAIL_ON_UNKNOWN_LANGUAGES,			
			BasicParserSettings.VERIFY_DATATYPE_VALUES,
			BasicParserSettings.VERIFY_LANGUAGE_TAGS,
			BasicParserSettings.VERIFY_RELATIVE_URIS,
			BasicParserSettings.VERIFY_URI_SYNTAX,
			NTriplesParserSettings.FAIL_ON_INVALID_LINES
	};
/*
	private static PipedReader[] prs;
	private static PipedWriter[] pws;
	private static ExecutorService executor;

	private static RDFParser createValidator(int counter) throws IOException {
		prs[counter] = new PipedReader();
		pws[counter] = new PipedWriter();
		prs[counter].connect(pws[counter]);

		RDFParser parser = Rio.createParser(RDFFormat.NTRIPLES);
		for (RioSetting setting: NON_FATAL_ERRORS) {
			parser.getParserConfig().addNonFatalError(setting).set(setting, true);
		}
		return parser;
	}

	private static void createThreads(int cores) {
		for (int i = 0; i < cores; i++) {
			int core = i;
			executor.submit(() -> {
				try {
					RDFParser parser = createValidator(core);
					parser.parse(prs[core]);
				} catch (IOException  ioe) {
					LOG.severe(ioe.getMessage());
				} catch (RDFParseException rpe) {
					long nr = rpe.getLineNumber();
					long line = (nr - 1) * cores + core + nr;
					LOG.log(Level.WARNING, "Line: {0} {1}", new Object[]{line, rpe.getMessage()});
				} catch (RDFHandlerException re) {
					LOG.warning(re.getMessage());
				}
			});
		}
	}
*/

	private static void validate(String inFile, int cores) throws IOException {
		long nr = 0;
	
		try(BZip2CompressorInputStream bcis = new BZip2CompressorInputStream(
												new BufferedInputStream(new FileInputStream(inFile)));
			BufferedReader r = new BufferedReader(new InputStreamReader(bcis))) {

			RDFParser parser = Rio.createParser(RDFFormat.NTRIPLES);
			for (RioSetting setting: NON_FATAL_ERRORS) {
				parser.getParserConfig().addNonFatalError(setting).set(setting, true);
			}
			parser.parse(bcis);
	/*	return parser;
			Iterator<String> iter = r.lines().iterator();
			while (iter.hasNext()) {
				pws[(int)(nr % cores)].write(iter.next());
				nr++;
				if (nr % 100_000 == 0) {
					LOG.log(Level.INFO, "Read {0} lines", nr);
				}
			} */
		}
		LOG.log(Level.INFO, "Read {0} lines", nr);
	}
	
	public static void main(String[] args) throws IOException {
		if (args.length < 2) {
			System.err.println("Usage: cores compressed-nt-input-file ");
		}

		int cores = Integer.valueOf(args[0]);

	/*	prs = new PipedReader[cores];
		pws = new PipedWriter[cores];

		executor = Executors.newFixedThreadPool(cores);

		createThreads(cores); */
		validate(args[1], cores);

/*		executor.shutdown();
	
		for (int i = 0; i < cores; i++) {
			pws[i].close();
			prs[i].close();
		} */
	} 
}
