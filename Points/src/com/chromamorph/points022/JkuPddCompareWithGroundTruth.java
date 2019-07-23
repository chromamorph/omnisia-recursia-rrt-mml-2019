package com.chromamorph.points022;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.TreeSet;

import com.chromamorph.notes.Notes;
import com.chromamorph.pitch.Pitch;

/**
 * 
 * @author David Meredith
 * @date 28 June 2013
 * 
 * For each piece in the JKU PDD, show each ground truth pattern 
 * together with the most similar MTP TEC in the encoding of the
 * piece generated by COSIATEC or SIATECCompress.
 * 
 */

public class JkuPddCompareWithGroundTruth {

	/*
	 * Only change the following folder path name!
	 */
//	private static String computedPatternFolderPathName = "/Users/dave/Documents/Work/Research/Data/JKU-PDD/2014-06-23-Gissel-JKU/tests/algo8LMsc322layermono";
	private static String algorithmName = "COSIATEC";
	private static String computedPatternFolderPathName = "/Users/dave/Documents/Work/Research/Data/JKU-PDD/JNMR2014/"+algorithmName;
	private static String monoPolyFolderName = "polyphonic";
	private static String computedPatternFileSuffix = (algorithmName.contains("COSIA")?".COSIATEC":".SIATECCompress");
//	private static String computedPatternFileSuffix = ".SIATECCompress";
	
	private static int pieceIndex = 0;
	private static String groundTruthFolderPathName = "/Users/dave/Documents/Work/Research/Data/JKU-PDD/JKUPDD-noAudio-Aug2013/groundTruth/";
	private static String[] pieceFolderNames = {"bachBWV889Fg","beethovenOp2No1Mvt3","chopinOp24No4","gibbonsSilverSwan1612","mozartK282Mvt2"};
	private static String[] pieceFileNames = {"wtc2f20","sonata01-3","mazurka24-4","silverswan","sonata04-2"};
//	private static String[] composerFileNamePrefixes = {"bach_","beet_","chop_","gbns_","mzrt_"};
	private static String inputFileNameWithoutSuffix = groundTruthFolderPathName + pieceFolderNames[pieceIndex]+"/"+monoPolyFolderName+"/lisp/"+pieceFileNames[pieceIndex];
	private static String inputFileName = inputFileNameWithoutSuffix+".opnd";
	private static String lispInputFileName = inputFileNameWithoutSuffix+".txt";

	/*
	 * Following to be used with Gissel's output
	 */
//	private static String computedPatternFilePathName = computedPatternFolderPathName+"/"+composerFileNamePrefixes[pieceIndex]+pieceFileNames[pieceIndex]+".txt";

	/*
	 * Following to be used with JNMR 2014 output if algorithm is COSIATEC
	 */
	private static String computedPatternFilePathName = computedPatternFolderPathName+"/"+pieceFileNames[pieceIndex]+computedPatternFileSuffix;

	private static boolean diatonicPitch = true;
	private static boolean mirexFormat = true;


	//	private static String rootFolderName = "/Users/dave/Documents/Work/Research/workspace/Points/data/JKUPDD-noAudio-Jul2013-2013-07-10-cos18/groundTruth/"; 
	private static ArrayList<String> opndAndOpcFileNames = null;
	private static ArrayList<String> inputFileNames = new ArrayList<String>();
	private static ArrayList<String> patternFileNames = new ArrayList<String>();
	private static ArrayList<String> occurrenceFileNames = new ArrayList<String>();
	private static TreeSet<String> workNames = new TreeSet<String>();
	private static PointSet DATASET = null;

	public static void main(String[] args) {
		pieceIndex = 0;
		Scanner scanner = new Scanner(System.in);
		while(pieceIndex < pieceFolderNames.length) {
			run();
			scanner.nextLine();
			pieceIndex++;
		}
		scanner.close();
	}
	public static void run() {

		inputFileNameWithoutSuffix = groundTruthFolderPathName + pieceFolderNames[pieceIndex]+"/"+monoPolyFolderName+"/lisp/"+pieceFileNames[pieceIndex];
		inputFileName = inputFileNameWithoutSuffix+".opnd";
		lispInputFileName = inputFileNameWithoutSuffix+".txt";
		
		/*
		 * Following to be used with Gissel's output
		 */
//		computedPatternFilePathName = computedPatternFolderPathName+"/"+composerFileNamePrefixes[pieceIndex]+pieceFileNames[pieceIndex]+".txt";

		/*
		 * Following to be used with JNMR 2014 output
		 */
		computedPatternFilePathName = computedPatternFolderPathName+"/"+pieceFileNames[pieceIndex]+computedPatternFileSuffix;

		//Find OPND and OPC files
		opndAndOpcFileNames = getFileNames(groundTruthFolderPathName, "lisp", ".opnd", ".opc");

		//		System.out.println("\nOPND and OPC files:");
		//		for(String s : opndAndOpcFileNames) System.out.println(s);


		for(String fileName : opndAndOpcFileNames) {
			if (!fileName.contains("repeatedPatterns"))
				inputFileNames.add(fileName);
			else if(!fileName.contains("occurrences"))
				patternFileNames.add(fileName);
			else
				occurrenceFileNames.add(fileName);
		}

		//		System.out.println("\nInput file names:");
		//		for(String s : inputFileNames) System.out.println(s);
		//		System.out.println("\nPattern file names:");
		//		for(String s : patternFileNames) System.out.println(s);
		//		System.out.println("\nOccurrence file names:");
		//		for(String s : occurrenceFileNames) System.out.println(s);

		//Find the work names for the input files to be used in the titles of the displayed point sets.
		for(String fileName : inputFileNames) {
			//			System.out.println(fileName);
			int startIndex = fileName.indexOf("groundTruth") + "groundTruth/".length();
			int endIndex = fileName.indexOf("/monophonic");
			if (endIndex < 0) endIndex = fileName.indexOf("/polyphonic");
			String workName = fileName.substring(startIndex,endIndex);
			//			System.out.println(workName);
			workNames.add(workName);
		}

		//		System.out.println("\nWork names:");
		//		for(String s : workNames) System.out.println(s);

		/*
		 * For each input file and for diatonic or chromatic pitch,
		 * create an ArrayList of PointSetCollectionPairs. 
		 * 
		 * Each PointSetCollectionPair contains two sets of patterns: 
		 * a ground-truth pattern set and the most similar computed pattern set
		 *  
		 * Each PointSetCollectionPair is displayed.
		 */
		//		for(boolean diatonicPitch : new boolean[]{true,false}) {
		//			for(String inputFileName : inputFileNames) {

		//				String inputFileName = rootFolderName + "bachBWV889Fg/polyphonic/lisp/wtc2f20.opnd";
		//				String inputFileName = rootFolderName + "chopinOp24No4/polyphonic/lisp/mazurka24-4.opnd";
		//				String inputFileName = rootFolderName + "gibbonsSilverSwan1612/polyphonic/lisp/silverswan.opnd";
		//				String inputFileName = rootFolderName + "mozartK282Mvt2/polyphonic/lisp/sonata04-2.opnd";

		try {
			String workName = findWorkName(inputFileName);
			String monoPoly = findMonoPoly(inputFileName);
			DATASET = new PointSet(Notes.fromOPND(inputFileName),diatonicPitch); 
			String windowTitle = workName +": "+ monoPoly + ", "+ (diatonicPitch?"morphetic":"chromatic");
			ArrayList<PointSetCollectionPair> arrayListOfPointSetCollectionPairs = getArrayListOfPointSetCollectionPairs(inputFileName, workName, monoPoly, diatonicPitch);

			System.out.println("arrayListOfPointSetCollectionPairs for "+inputFileName);
			for (PointSetCollectionPair pscp : arrayListOfPointSetCollectionPairs) {
				System.out.println(pscp.pointSetCollection1);
				System.out.println(pscp.pointSetCollection2);
				System.out.println();
			}
			System.out.println("About to draw dataset");
			DATASET.draw(windowTitle,arrayListOfPointSetCollectionPairs);
		} 
		catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (NoMorpheticPitchException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		//			}
		//		}

	}

	public static ArrayList<String> getFileNames(String rootFolder, String folderName, String... suffixes) {
		ArrayList<String> outputList = new ArrayList<String>();
		File folder = new File(rootFolder);
		if (folder.isDirectory()) {
			String[] dirList = folder.list();
			for(String dirItemName : dirList) {
				String fullPath = rootFolder+"/"+dirItemName;
				File dirItem = new File(fullPath);
				if (dirItem.isDirectory())
					outputList.addAll(getFileNames(fullPath, folderName, suffixes));
				else if (rootFolder.endsWith(folderName) && hasSuffix(dirItemName,suffixes))
					outputList.add(fullPath);
			}
		}
		return outputList;
	}

	private static boolean hasSuffix(String fileName, String... suffixes) {
		for(String suffix : suffixes)
			if (fileName.endsWith(suffix))
				return true;
		return false;
	}

	private static String findWorkName(String inputFileName) throws IllegalArgumentException {
		for(String workName : workNames) {
			if (inputFileName.contains(workName))
				return workName;
		}
		throw new IllegalArgumentException("File name does not contain any known work name: "+ inputFileName);
	}

	private static class OPC {
		Long onset = null;
		Integer chromaticPitch = null;

		OPC(Long onset, Integer chromaticPitch) {
			this.onset = onset;
			this.chromaticPitch = chromaticPitch;
		}

		public String toString() {
			return "opc("+onset+","+chromaticPitch+")";
		}
	}

	private static ArrayList<OPC> loadOPCFile(String fileName) {
		ArrayList<OPC> opcList = new ArrayList<OPC>();

		try {
			BufferedReader br = new BufferedReader(new FileReader(fileName));
			StringBuilder sb = new StringBuilder();
			String l;
			while ((l = br.readLine()) != null)
				sb.append(l);
			String text = sb.toString();

			//			System.out.println("\nloadOPCFile read string from file "+fileName);
			//			System.out.println(text);

			String[] sa = text.split("[()]");

			//			for(String q : sa) System.out.println(q);

			for(String s : sa) {
				String x = s.trim();
				if (x != null && x.length() > 0) {
					String[] xa = x.split(" ");
					Long onset = Long.parseLong(xa[0]);
					Integer chromaticPitch = Integer.parseInt(xa[1]);
					opcList.add(new OPC(onset,chromaticPitch));
				}
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return opcList;
	}

	private static ArrayList<OPNDV> getOPNDList(String fileName) {
		ArrayList<OPNDV> opndList = new ArrayList<OPNDV>();

		try {
			BufferedReader br = new BufferedReader(new FileReader(fileName));
			StringBuilder sb = new StringBuilder();
			String l;
			while ((l = br.readLine()) != null)
				sb.append(l);

			//System.out.println("getOPNDList: read string from file "+fileName);
			//System.out.println(sb.toString());

			String text = sb.toString();
			String[] sa = text.split("[()]");

			//for(String q : sa) System.out.println(q);

			for(String s : sa) {
				String x = s.trim();
				if (x != null && x.length() > 0) {
					String[] xa = x.split(" ");
					OPNDV opnd = new OPNDV(
							Long.parseLong(xa[0]), //onset
							xa[1].trim(), //pitch name
							Long.parseLong(xa[2]), //duration
							Integer.parseInt(xa[3]) // voice
							);
					//					opnd.onset = Long.parseLong(xa[0]);
					//					opnd.pitchName = xa[1].trim();
					//					opnd.duration = Long.parseLong(xa[2]);
					//					opnd.voice = Integer.parseInt(xa[3]);
					opndList.add(opnd);
				}
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return opndList;
	}

	private static ArrayList<ArrayList<PointSet>> loadComputedPatternList(String inputFileName, boolean diatonicPitch) {
		//		System.out.println("Loading computed TEC List: " +inputFileName+" ("+ (diatonicPitch?"Diatonic":"Chromatic")+")");
		try {
			if (!mirexFormat) {
				ArrayList<TEC> tecList = new ArrayList<TEC>();
				BufferedReader br = new BufferedReader(new FileReader(inputFileName));
				for(String l = br.readLine(); l != null; l = br.readLine())
					if (l != null && l.trim().length() > 0)
						tecList.add(new TEC(l.trim()));
				br.close();

				//			Convert to ArrayList<ArrayList<PointSet>>

				ArrayList<ArrayList<PointSet>> patternList = new ArrayList<ArrayList<PointSet>>();
				for(TEC tec : tecList) {
					ArrayList<PointSet> occurrences = new ArrayList<PointSet>();
					for(Vector v : tec.getTranslators().getVectors())
						occurrences.add(tec.getPattern().translate(v));
					patternList.add(occurrences);
				}
				return patternList;
			} else { //Computed patterns are in a MIREX format file

				return MIREX2013Entries.readMIREXOutputFile(inputFileName, lispInputFileName);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static ArrayList<PointSetCollectionPair> getArrayListOfPointSetCollectionPairs(String inputFileName, String workName, String monoPoly, boolean diatonicPitch) {

		ArrayList<OPNDV> inputOPNDList = getOPNDList(inputFileName);

		//Load ground-truth pattern sets
		ArrayList<String> patternFileNamesForThisWork = new ArrayList<String>();
		for(String patternFileName : patternFileNames) {
			if (patternFileName.contains(workName) && patternFileName.contains(monoPoly))
				patternFileNamesForThisWork.add(patternFileName);
		}

		System.out.println("\nPattern file names for "+workName+" "+ monoPoly+ " "+diatonicPitch);
		for (String patternFileName : patternFileNamesForThisWork) System.out.println(patternFileName);

		//Make pattern set (point set collection) for each ground-truth pattern
		ArrayList<PatternOccurrenceListPair> groundTruthPatternSets = new ArrayList<PatternOccurrenceListPair>();
		System.out.println(workName+": patternFileNamesForThisWork length ="+patternFileNamesForThisWork.size());
		for(String patternFileName : patternFileNamesForThisWork) {
			PatternOccurrenceListPair groundTruthPatternSet = new PatternOccurrenceListPair();
			groundTruthPatternSet.patternFileName = patternFileName;
			try {
				groundTruthPatternSet.pattern = new PointSet(Notes.fromOPND(groundTruthPatternSet.patternFileName),diatonicPitch);
			} catch (NoMorpheticPitchException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			//Find the occurrence file names for this pattern
			for(String occurrenceFileName : occurrenceFileNames) {
				if (occurrenceFileName.startsWith(patternFileName.substring(0,patternFileName.indexOf("/lisp")))) {
					groundTruthPatternSet.occurrenceFileNames.add(occurrenceFileName);
				}
			}

			System.out.println("Occurrence file names for "+workName);
			for(String occFileName : groundTruthPatternSet.occurrenceFileNames) System.out.println(" "+occFileName);

			//Load the occurrences for this pattern
			/*
			 * Each occurrence file will be an opc file.
			 * We need to load the opc file into its own data structure.
			 * Then we need to find the points in the input file that correspond to the points in the opc file
			 * This set of input file points will form the occurrence to be added to the list of occurrences
			 * for this pattern.
			 */
			for(String occurrenceFileName : groundTruthPatternSet.occurrenceFileNames) {
				System.out.println("Loading OPC file for "+occurrenceFileName);
				ArrayList<OPC> opcList = loadOPCFile(occurrenceFileName);
				try {
					PointSet occurrencePointSet = getPointSetFromOPCList(opcList, inputOPNDList, diatonicPitch);
					System.out.println(opcList);
					System.out.println(occurrencePointSet);
					groundTruthPatternSet.occurrences.add(occurrencePointSet);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

//			DATASET.draw(groundTruthPatternSet.occurrences,"Ground-truth pattern for "+workName);			
			groundTruthPatternSets.add(groundTruthPatternSet);
		}

		for(PatternOccurrenceListPair polp : groundTruthPatternSets)
			System.out.println("\n"+polp);

		//Load computed pattern sets (TECs)

		//		System.out.println(inputFileName);
		//		/Users/dave/Documents/Work/Research/workspace/Points/data/JKUPDD-noAudio-Mar2013/groundTruth/bachBWV889Fg/monophonic/lisp/wtc2f20.opnd

		//		String folderName = inputFileName.substring(0,inputFileName.lastIndexOf("/"));
		//		String fileName = inputFileName.substring(inputFileName.lastIndexOf("/")+1,inputFileName.length()-5);

		//		System.out.println(cosiatecFileName);
		//		/Users/dave/Documents/Work/Research/workspace/Points/data/JKUPDD-noAudio-Mar2013/groundTruth/bachBWV889Fg/monophonic/lisp/wtc2f20-chrom.cos18
		//		/Users/dave/Documents/Work/Research/workspace/Points/data/JKUPDD-noAudio-Mar2013/groundTruth/bachBWV889Fg/monophonic/lisp/wtc2f20-diat.cos18

		ArrayList<ArrayList<PointSet>> computedPatternList = (loadComputedPatternList(computedPatternFilePathName, diatonicPitch));
//		for(ArrayList<PointSet> computedOccurrenceSet : computedPatternList)
//			DATASET.draw(computedOccurrenceSet,"Pattern for "+workName+" computed by "+algorithmName);			

		//For each ground-truth pattern set find the most similar TEC and add the pair
		//of pattern sets to arrayListOfPointSetCollectionPairs

		ArrayList<PointSetCollectionPair> arrayListOfPointSetCollectionPairs = new ArrayList<PointSetCollectionPair>();

		//		for(TEC tec : computedTECList) {
		//			PatternOccurrenceListPair mostSimilarGroundTruthPatternSet = findMostSimilarGroundTruthPatternSet(tec,groundTruthPatternSets);
		//			arrayListOfPointSetCollectionPairs.add(new PointSetCollectionPair(tec,mostSimilarGroundTruthPatternSet));
		//		}

		for(PatternOccurrenceListPair groundTruthPatternSet : groundTruthPatternSets)
			arrayListOfPointSetCollectionPairs.add(new PointSetCollectionPair(findMostSimilarTEC(groundTruthPatternSet,computedPatternList),groundTruthPatternSet));

		return arrayListOfPointSetCollectionPairs;
	}

	private static ArrayList<PointSet> findMostSimilarTEC(PatternOccurrenceListPair groundTruthPatternSet, ArrayList<ArrayList<PointSet>> computedPatternList) {
		ArrayList<PointSet> mostSimilarTEC = null;
		double max = 0.0;
		ArrayList<PointSet> groundTruthOccurrenceSet = groundTruthPatternSet.occurrences;
		for(ArrayList<PointSet> computedOccurrenceSet : computedPatternList) {
			double f1 = EvaluateMIREX2013.getF1(groundTruthOccurrenceSet,computedOccurrenceSet);
			if (f1 > max) {
				max = f1;
				mostSimilarTEC = computedOccurrenceSet;
			}
		}
		return mostSimilarTEC;
		
//		ArrayList<PointSet> mostSimilarPattern = null;
//		PointSet mostSimilarGroundTruthOccurrence = null;
//		PointSet mostSimilarComputedOccurrence = null;
//		double maxSimilarity = 0.0;
//
//		for(ArrayList<PointSet> pattern : computedPatternList) {
//			for(PointSet groundTruthOccurrence : groundTruthPatternSet.occurrences)
//				for(PointSet computedOccurrence : pattern) {
//					double similarity = getSimilarity(groundTruthOccurrence,computedOccurrence); 
//					if (mostSimilarPattern == null || similarity > maxSimilarity) {
//						mostSimilarPattern = pattern;
//						maxSimilarity = similarity;
//						mostSimilarGroundTruthOccurrence = groundTruthOccurrence;
//						mostSimilarComputedOccurrence = computedOccurrence;
//					}
//				}
//
//		}
//
//		System.out.println("\nmostSimilarGroundTruthPattern = "+mostSimilarGroundTruthOccurrence);
//		System.out.println("mostSimilarComputedPattern = "+mostSimilarComputedOccurrence);
//		System.out.println("Similarity = "+maxSimilarity);
//		return mostSimilarPattern;
	}

	/**
	 * 
	 * @param ps1
	 * @param ps2
	 * @return A double value between 0 and 1, calculated as follows:
	 * s = 2 * (|ps1 \cap ps2|)/(|ps1|+|ps2|)
	 */
//	private static double getSimilarity(PointSet ps1, PointSet ps2) {
//		double truePositives = ps1.intersection(ps2).size();
//		double trues = ps2.size();
//		double positives = ps1.size();
//		return 2.0 * truePositives / (trues + positives);
//	}

	//	private static PatternOccurrenceListPair findMostSimilarGroundTruthPatternSet(TEC tec, ArrayList<PatternOccurrenceListPair> groundTruthPatternSets) {
	//		PatternOccurrenceListPair mostSimilarGroundTruthPatternSet = null;
	//		Double maxSimilarity = null;
	//		for (PatternOccurrenceListPair groundTruthPatternSet : groundTruthPatternSets) {
	//			Double thisSimilarity = patternSetSimilarity(tec,groundTruthPatternSet);
	//			if (mostSimilarGroundTruthPatternSet == null || thisSimilarity > maxSimilarity) {
	//				maxSimilarity = thisSimilarity;
	//				mostSimilarGroundTruthPatternSet = groundTruthPatternSet;
	//			}
	//		}
	//		return mostSimilarGroundTruthPatternSet;
	//	}

	/**
	 * 
	 * @param tec
	 * @param groundTruthPatternSet
	 * @return
	 */
	//	private static Double patternSetSimilarity(TEC tec, PatternOccurrenceListPair groundTruthPatternSet) {
	//
	//		ArrayList<PointSet>	 computedPatternOccurrences = new ArrayList<PointSet>();
	//		TreeSet<Vector> translators = tec.getTranslators().getVectors(); 
	//		for(Vector v : translators)
	//			computedPatternOccurrences.add(tec.getPattern().translate(v));
	//
	//		ArrayList<PointSet> groundTruthOccurrences = new ArrayList<PointSet>();
	//		groundTruthOccurrences.add(groundTruthPatternSet.pattern);
	//		for(PointSet occurrence : groundTruthPatternSet.occurrences)
	//			groundTruthOccurrences.add(occurrence);
	//
	//		double maxSim = 0.0;
	//		for(PointSet groundTruthOccurrence : groundTruthOccurrences)
	//			for(PointSet computedPatternOccurrence : computedPatternOccurrences) {
	//				double sim = pointSetSimilarity(groundTruthOccurrence,computedPatternOccurrence);
	//				if (sim > maxSim)
	//					maxSim = sim;
	//			}
	//
	//
	//		return maxSim;
	//	}

	//	private static double pointSetSimilarity(PointSet ps1, PointSet ps2) {
	//		int maxSize = Math.max(ps1.size(), ps2.size());
	//		int intersectionSize = ps1.intersection(ps2).size();
	//		return (1.0 * maxSize / intersectionSize);
	//	}

	/**
	 * Makes a PointSet containing the Points in inputDataset that correspond to
	 * the opc points in opcList.
	 * 
	 * For each opc in opcList
	 *   find the corresponding opnd in inputOPNDList
	 *   find either the chromatic or morphetic pitch for this opnd, depending on diatonicPitch
	 *   find the point in inputDataset with the same onset as opc and the chromatic or morphetic pitch just calculated
	 *   put this point into the pointSet to be returned
	 * @throws Exception 
	 */
	private static PointSet getPointSetFromOPCList(ArrayList<OPC> opcList, ArrayList<OPNDV> inputOPNDList, boolean diatonicPitch) throws Exception {
		//		System.out.println("getPointSetFromOPCList called with \ndiatonicPitch = "+diatonicPitch+"\nopcList ="+opcList+"\ninputOPNDList = "+inputOPNDList);
		PointSet returnedPointSet = new PointSet();
		for(OPC opc : opcList) {
			//Find the opnd in inputOPNDList that corresponds to this opc
			//			System.out.println("opc is "+opc+" opc onset is "+opc.onset+" opc.chromaticPitch is "+opc.chromaticPitch);
			for(OPNDV opnd : inputOPNDList) {
				//				System.out.println(" "+opnd.onset+" "+opnd.pitchName);
				if (opnd.getOnset().equals(opc.onset)) {
					//					System.out.println("  Found same onset: "+opnd);
					Pitch opndPitch = new Pitch();
					opndPitch.setPitchName(opnd.getPitch().getPitchName());
					int opndChromaticPitch = opndPitch.getChromaticPitch();
					if (opc.chromaticPitch.equals(opndChromaticPitch)) { //Found corresponding OPND!
						//						System.out.println("  Found same chromatic pitch: "+opndChromaticPitch);
						Point newPoint = new Point(opc.onset,(diatonicPitch?opndPitch.getMorpheticPitch():opndChromaticPitch));
						//						System.out.println("  New point added: "+newPoint);
						returnedPointSet.add(newPoint);
						break;
					}				
				}
			}
		}
		return returnedPointSet;
	}

	private static String findMonoPoly(String inputFileName) {
		return (inputFileName.contains("monophonic")?"monophonic":"polyphonic");
	}
}

