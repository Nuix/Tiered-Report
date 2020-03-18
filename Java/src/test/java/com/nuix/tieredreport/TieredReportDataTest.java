package com.nuix.tieredreport;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Test;

/**
 * Unit test for simple App.
 */
public class TieredReportDataTest {

	@Test
	public void shouldComputeRanges() {
		/* test data sets
			- first array element of data is the input to getRanges()
			- second array element of data is the expected output
		*/
		int[][][][] data = new int[][][][]{
			{{ {0}             }, { {0}                }},
			{{ {0, 1},         }, { {0, 1}             }},
			{{ {0, 1, 2},      }, { {0, 2}             }},
			{{ {0, 1, 2, 3},   }, { {0, 3}             }},
			{{ {0, 2, 3},      }, { {0}, {2, 3}        }},
			{{ {0, 2, 3, 4}    }, { {0}, {2, 4}        }},
			{{ {0, 2, 4},      }, { {0}, {2}, {4}      }},
			{{ {0, 2, 4, 6},   }, { {0}, {2}, {4}, {6} }},
			{{ {0, 2, 4, 5}    }, { {0}, {2}, {4,5}    }},
			{{ {0, 1, 4, 5}    }, { {0, 1}, {4,5}      }},
			{{ {0, 1, 4, 5, 6} }, { {0, 1}, {4,6}      }},
			{{ {0, 2, 4, 6}    }, { {0}, {2}, {4}, {6} }},
			{{ {0, 2, 3, 4, 6} }, { {0}, {2, 4}, {6}   }},
		};
		for(int i = 0; i < data.length; i += 1) {
			int[][][] test = data[i];
			int[][] input = test[0];
			int[][] expectedOutput = test[1];
			ArrayList<int[]> actualOutput = TieredReportData.getRanges(input[0]);
			assertEquals(String.format("Output lengths are not equal for test data[%d]", i), Integer.valueOf(expectedOutput.length), Integer.valueOf(actualOutput.size()));
			for(int j = 0; j < expectedOutput.length; j += 1) {
				int[] expectedRange = expectedOutput[j];
				int[] actualRange = actualOutput.get(j);
				assertEquals(String.format("Range lengths are not equal for test data[%d]", i),  Integer.valueOf(expectedRange.length),  Integer.valueOf(actualRange.length));
				for (int k = 0; k < expectedRange.length; k += 1) {
					int actualValue = actualRange[k];
					int expectedValue = expectedRange[k];
					assertEquals(String.format("Values are not equal for test data[%d]", i), Integer.valueOf(expectedValue), Integer.valueOf(actualValue));
				}
			}
		}
	}
}
