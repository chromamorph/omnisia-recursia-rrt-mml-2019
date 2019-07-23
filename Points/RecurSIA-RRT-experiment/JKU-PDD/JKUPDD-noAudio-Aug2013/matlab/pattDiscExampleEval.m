% Copyright Tom Collins 13/8/2013

% Calculating evaluation metrics for example pattern discovery algorithm
% output.

% The following paths will need correcting for a specific file system: the
% location of the JKU Patterns Development Database.
%dataRoot = fullfile('~', 'Data', 'Music', 'JKU',...
%  'JKUPDD-noAudio-Mar2013', 'groundTruth');
dataRoot = fullfile('..', 'groundTruth');
% The location of the function pattAllOccAll2struct and this script
% (pattDiscExampleEval).
projectRoot = fullfile('.');
% The location of the functions estPrecRecMat, occPrecRecMat, threeLayerF1,
% precisionRecallTrans, and scoreMatrix. These can be distributed across
% different folders,...
coreRoot = fullfile('.');
% ...as long as each folder is added here.
addpath(fullfile(coreRoot, 'analysis'),...
  fullfile(coreRoot, 'filter'),...
  fullfile(coreRoot, 'formatUtils'),...
  fullfile(coreRoot, 'patternDiscovery'),...
  fullfile(coreRoot, 'patternMatching'),...
  fullfile(coreRoot, 'rating'),...
  fullfile(coreRoot, 'thirdParty', 'matlabCentral', 'count'),...
  fullfile(coreRoot, 'thirdParty', 'matlabCentral', 'peakfinder'));
% Add the functions specific to this project.
addpath(fullfile(projectRoot))
% Location to save results.
metricResultsPath = fullfile(projectRoot, 'results');

% Define the names of the algorithms that are to be evaluated.
algOut = {fullfile(projectRoot, 'pattDiscOut', 'COSIACTTEC'),...
fullfile(projectRoot, 'pattDiscOut', 'COSIACTTEC-RRT'),...
fullfile(projectRoot, 'pattDiscOut', 'COSIACTTECBB'),...
fullfile(projectRoot, 'pattDiscOut', 'COSIACTTECBB-RRT'),...
fullfile(projectRoot, 'pattDiscOut', 'COSIACTTECSegment'),...
fullfile(projectRoot, 'pattDiscOut', 'COSIACTTECSegment-RRT'),...
fullfile(projectRoot, 'pattDiscOut', 'COSIARCTTEC'),...
fullfile(projectRoot, 'pattDiscOut', 'COSIARCTTEC-RRT'),...
fullfile(projectRoot, 'pattDiscOut', 'COSIARCTTECBB'),...
fullfile(projectRoot, 'pattDiscOut', 'COSIARCTTECBB-RRT'),...
fullfile(projectRoot, 'pattDiscOut', 'COSIARCTTECSegment'),...
fullfile(projectRoot, 'pattDiscOut', 'COSIARCTTECSegment-RRT'),...
fullfile(projectRoot, 'pattDiscOut', 'COSIARTEC'),...
fullfile(projectRoot, 'pattDiscOut', 'COSIARTEC-RRT'),...
fullfile(projectRoot, 'pattDiscOut', 'COSIARTECBB'),...
fullfile(projectRoot, 'pattDiscOut', 'COSIARTECBB-RRT'),...
fullfile(projectRoot, 'pattDiscOut', 'COSIARTECSegment'),...
fullfile(projectRoot, 'pattDiscOut', 'COSIARTECSegment-RRT'),...
fullfile(projectRoot, 'pattDiscOut', 'COSIATEC'),...
fullfile(projectRoot, 'pattDiscOut', 'COSIATEC-RRT'),...
fullfile(projectRoot, 'pattDiscOut', 'COSIATECBB'),...
fullfile(projectRoot, 'pattDiscOut', 'COSIATECBB-RRT'),...
fullfile(projectRoot, 'pattDiscOut', 'COSIATECSegment'),...
fullfile(projectRoot, 'pattDiscOut', 'COSIATECSegment-RRT'),...
fullfile(projectRoot, 'pattDiscOut', 'Forth'),...
fullfile(projectRoot, 'pattDiscOut', 'Forth-RRT'),...
fullfile(projectRoot, 'pattDiscOut', 'ForthBB'),...
fullfile(projectRoot, 'pattDiscOut', 'ForthBB-RRT'),...
fullfile(projectRoot, 'pattDiscOut', 'ForthCT'),...
fullfile(projectRoot, 'pattDiscOut', 'ForthCT-RRT'),...
fullfile(projectRoot, 'pattDiscOut', 'ForthCTBB'),...
fullfile(projectRoot, 'pattDiscOut', 'ForthCTBB-RRT'),...
fullfile(projectRoot, 'pattDiscOut', 'ForthCTSegment'),...
fullfile(projectRoot, 'pattDiscOut', 'ForthCTSegment-RRT'),...
fullfile(projectRoot, 'pattDiscOut', 'ForthR'),...
fullfile(projectRoot, 'pattDiscOut', 'ForthR-RRT'),...
fullfile(projectRoot, 'pattDiscOut', 'ForthRBB'),...
fullfile(projectRoot, 'pattDiscOut', 'ForthRBB-RRT'),...
fullfile(projectRoot, 'pattDiscOut', 'ForthRCT'),...
fullfile(projectRoot, 'pattDiscOut', 'ForthRCT-RRT'),...
fullfile(projectRoot, 'pattDiscOut', 'ForthRCTBB'),...
fullfile(projectRoot, 'pattDiscOut', 'ForthRCTBB-RRT'),...
fullfile(projectRoot, 'pattDiscOut', 'ForthRCTSegment'),...
fullfile(projectRoot, 'pattDiscOut', 'ForthRCTSegment-RRT'),...
fullfile(projectRoot, 'pattDiscOut', 'ForthRSegment'),...
fullfile(projectRoot, 'pattDiscOut', 'ForthRSegment-RRT'),...
fullfile(projectRoot, 'pattDiscOut', 'ForthSegment'),...
fullfile(projectRoot, 'pattDiscOut', 'ForthSegment-RRT'),...
fullfile(projectRoot, 'pattDiscOut', 'ReCOSIACTTEC'),...
fullfile(projectRoot, 'pattDiscOut', 'ReCOSIACTTEC-RRT'),...
fullfile(projectRoot, 'pattDiscOut', 'ReCOSIACTTECBB'),...
fullfile(projectRoot, 'pattDiscOut', 'ReCOSIACTTECBB-RRT'),...
fullfile(projectRoot, 'pattDiscOut', 'ReCOSIACTTECSegment'),...
fullfile(projectRoot, 'pattDiscOut', 'ReCOSIACTTECSegment-RRT'),...
fullfile(projectRoot, 'pattDiscOut', 'ReCOSIARCTTEC'),...
fullfile(projectRoot, 'pattDiscOut', 'ReCOSIARCTTEC-RRT'),...
fullfile(projectRoot, 'pattDiscOut', 'ReCOSIARCTTECBB'),...
fullfile(projectRoot, 'pattDiscOut', 'ReCOSIARCTTECBB-RRT'),...
fullfile(projectRoot, 'pattDiscOut', 'ReCOSIARCTTECSegment'),...
fullfile(projectRoot, 'pattDiscOut', 'ReCOSIARCTTECSegment-RRT'),...
fullfile(projectRoot, 'pattDiscOut', 'ReCOSIARTEC'),...
fullfile(projectRoot, 'pattDiscOut', 'ReCOSIARTEC-RRT'),...
fullfile(projectRoot, 'pattDiscOut', 'ReCOSIARTECBB'),...
fullfile(projectRoot, 'pattDiscOut', 'ReCOSIARTECBB-RRT'),...
fullfile(projectRoot, 'pattDiscOut', 'ReCOSIARTECSegment'),...
fullfile(projectRoot, 'pattDiscOut', 'ReCOSIARTECSegment-RRT'),...
fullfile(projectRoot, 'pattDiscOut', 'ReCOSIATEC'),...
fullfile(projectRoot, 'pattDiscOut', 'ReCOSIATEC-RRT'),...
fullfile(projectRoot, 'pattDiscOut', 'ReCOSIATECBB'),...
fullfile(projectRoot, 'pattDiscOut', 'ReCOSIATECBB-RRT'),...
fullfile(projectRoot, 'pattDiscOut', 'ReCOSIATECSegment'),...
fullfile(projectRoot, 'pattDiscOut', 'ReCOSIATECSegment-RRT'),...
fullfile(projectRoot, 'pattDiscOut', 'ReForth'),...
fullfile(projectRoot, 'pattDiscOut', 'ReForth-RRT'),...
fullfile(projectRoot, 'pattDiscOut', 'ReForthBB'),...
fullfile(projectRoot, 'pattDiscOut', 'ReForthBB-RRT'),...
fullfile(projectRoot, 'pattDiscOut', 'ReForthCT'),...
fullfile(projectRoot, 'pattDiscOut', 'ReForthCT-RRT'),...
fullfile(projectRoot, 'pattDiscOut', 'ReForthCTBB'),...
fullfile(projectRoot, 'pattDiscOut', 'ReForthCTBB-RRT'),...
fullfile(projectRoot, 'pattDiscOut', 'ReForthCTSegment'),...
fullfile(projectRoot, 'pattDiscOut', 'ReForthCTSegment-RRT'),...
fullfile(projectRoot, 'pattDiscOut', 'ReForthR'),...
fullfile(projectRoot, 'pattDiscOut', 'ReForthR-RRT'),...
fullfile(projectRoot, 'pattDiscOut', 'ReForthRBB'),...
fullfile(projectRoot, 'pattDiscOut', 'ReForthRBB-RRT'),...
fullfile(projectRoot, 'pattDiscOut', 'ReForthRCT'),...
fullfile(projectRoot, 'pattDiscOut', 'ReForthRCT-RRT'),...
fullfile(projectRoot, 'pattDiscOut', 'ReForthRCTBB'),...
fullfile(projectRoot, 'pattDiscOut', 'ReForthRCTBB-RRT'),...
fullfile(projectRoot, 'pattDiscOut', 'ReForthRCTSegment'),...
fullfile(projectRoot, 'pattDiscOut', 'ReForthRCTSegment-RRT'),...
fullfile(projectRoot, 'pattDiscOut', 'ReForthRSegment'),...
fullfile(projectRoot, 'pattDiscOut', 'ReForthRSegment-RRT'),...
fullfile(projectRoot, 'pattDiscOut', 'ReForthSegment'),...
fullfile(projectRoot, 'pattDiscOut', 'ReForthSegment-RRT'),...
fullfile(projectRoot, 'pattDiscOut', 'ReSIACTTECCompress'),...
fullfile(projectRoot, 'pattDiscOut', 'ReSIACTTECCompress-RRT'),...
fullfile(projectRoot, 'pattDiscOut', 'ReSIACTTECCompressBB'),...
fullfile(projectRoot, 'pattDiscOut', 'ReSIACTTECCompressBB-RRT'),...
fullfile(projectRoot, 'pattDiscOut', 'ReSIACTTECCompressSegment'),...
fullfile(projectRoot, 'pattDiscOut', 'ReSIACTTECCompressSegment-RRT'),...
fullfile(projectRoot, 'pattDiscOut', 'ReSIARCTTECCompress'),...
fullfile(projectRoot, 'pattDiscOut', 'ReSIARCTTECCompress-RRT'),...
fullfile(projectRoot, 'pattDiscOut', 'ReSIARCTTECCompressBB'),...
fullfile(projectRoot, 'pattDiscOut', 'ReSIARCTTECCompressBB-RRT'),...
fullfile(projectRoot, 'pattDiscOut', 'ReSIARCTTECCompressSegment'),...
fullfile(projectRoot, 'pattDiscOut', 'ReSIARCTTECCompressSegment-RRT'),...
fullfile(projectRoot, 'pattDiscOut', 'ReSIARTECCompress'),...
fullfile(projectRoot, 'pattDiscOut', 'ReSIARTECCompress-RRT'),...
fullfile(projectRoot, 'pattDiscOut', 'ReSIARTECCompressBB'),...
fullfile(projectRoot, 'pattDiscOut', 'ReSIARTECCompressBB-RRT'),...
fullfile(projectRoot, 'pattDiscOut', 'ReSIARTECCompressSegment'),...
fullfile(projectRoot, 'pattDiscOut', 'ReSIARTECCompressSegment-RRT'),...
fullfile(projectRoot, 'pattDiscOut', 'ReSIATECCompress'),...
fullfile(projectRoot, 'pattDiscOut', 'ReSIATECCompress-RRT'),...
fullfile(projectRoot, 'pattDiscOut', 'ReSIATECCompressBB'),...
fullfile(projectRoot, 'pattDiscOut', 'ReSIATECCompressBB-RRT'),...
fullfile(projectRoot, 'pattDiscOut', 'ReSIATECCompressSegment'),...
fullfile(projectRoot, 'pattDiscOut', 'ReSIATECCompressSegment-RRT'),...
fullfile(projectRoot, 'pattDiscOut', 'SIACTTECCompress'),...
fullfile(projectRoot, 'pattDiscOut', 'SIACTTECCompress-RRT'),...
fullfile(projectRoot, 'pattDiscOut', 'SIACTTECCompressBB'),...
fullfile(projectRoot, 'pattDiscOut', 'SIACTTECCompressBB-RRT'),...
fullfile(projectRoot, 'pattDiscOut', 'SIACTTECCompressSegment'),...
fullfile(projectRoot, 'pattDiscOut', 'SIACTTECCompressSegment-RRT'),...
fullfile(projectRoot, 'pattDiscOut', 'SIARCTTECCompress'),...
fullfile(projectRoot, 'pattDiscOut', 'SIARCTTECCompress-RRT'),...
fullfile(projectRoot, 'pattDiscOut', 'SIARCTTECCompressBB'),...
fullfile(projectRoot, 'pattDiscOut', 'SIARCTTECCompressBB-RRT'),...
fullfile(projectRoot, 'pattDiscOut', 'SIARCTTECCompressSegment'),...
fullfile(projectRoot, 'pattDiscOut', 'SIARCTTECCompressSegment-RRT'),...
fullfile(projectRoot, 'pattDiscOut', 'SIARTECCompress'),...
fullfile(projectRoot, 'pattDiscOut', 'SIARTECCompress-RRT'),...
fullfile(projectRoot, 'pattDiscOut', 'SIARTECCompressBB'),...
fullfile(projectRoot, 'pattDiscOut', 'SIARTECCompressBB-RRT'),...
fullfile(projectRoot, 'pattDiscOut', 'SIARTECCompressSegment'),...
fullfile(projectRoot, 'pattDiscOut', 'SIARTECCompressSegment-RRT'),...
fullfile(projectRoot, 'pattDiscOut', 'SIATECCompress'),...
fullfile(projectRoot, 'pattDiscOut', 'SIATECCompress-RRT'),...
fullfile(projectRoot, 'pattDiscOut', 'SIATECCompressBB'),...
fullfile(projectRoot, 'pattDiscOut', 'SIATECCompressBB-RRT'),...
fullfile(projectRoot, 'pattDiscOut', 'SIATECCompressSegment'),...
fullfile(projectRoot, 'pattDiscOut', 'SIATECCompressSegment-RRT'),...
};

% Define the names of the pieces forming the ground truth.
pieces = {fullfile(dataRoot, 'bachBWV889Fg'),...
  fullfile(dataRoot, 'beethovenOp2No1Mvt3'),...
  fullfile(dataRoot, 'chopinOp24No4'),...
  fullfile(dataRoot, 'gibbonsSilverSwan1612'),...
  fullfile(dataRoot, 'mozartK282Mvt2')};
labels = {'bach_wtc2f20', 'beet_sonata01-3', 'chop_mazurka24-4',...
  'gbns_silverswan' 'mzrt_sonata04-2'};
polyAnn = {'bruhn' 'barlowAndMorgensternRevised' 'sectionalRepetitions'...
  'schoenberg' 'tomCollins'};
monoAnn = {'bruhn' 'barlowAndMorgenstern' 'barlowAndMorgensternRevised'...
  'sectionalRepetitions' 'schoenberg' 'tomCollins'};
compIDs = {'bach_', 'beet_', 'chop_', 'gbns_', 'mzrt_'};

% Specify version of the task and some parameters for the evaluation
% metrics.
taskVer = 'polyphonic';
projectionIdx = [1 2];
similarThresh = [.75 .5];
similarFunc = 'cardinality score';


% Load runtimes and fifth return times (FRT) as nalgOut x npiece matrices.
% The matrices are defined here, and the times are loaded from text files
% below.
nalgOut = size(algOut, 2);
npiece = size(pieces, 2);
RT = zeros(nalgOut, npiece);
FRT = zeros(nalgOut, npiece);

% Load ground truths for each piece into the variable called GT.
if strcmp(taskVer, 'polyphonic')
  annotations = polyAnn;
else
  annotations = monoAnn;
end
% Variable to hold patterns for all pieces.
GT = cell(1, npiece);
for ipiece = 1:npiece
  % Variable to hold patterns for current piece.
  T = struct([]);
  pattj = 1; % Increment to create T.
  % Iterate over different annotations.
  currAnn = fullfile(pieces{ipiece}, taskVer, 'repeatedPatterns');
  contents = dir(currAnn);
  ncont = size(contents, 1);
  for icont = 1:ncont
    currAnnName = contents(icont).name;
    if ismember(currAnnName, annotations)
      % Include the patterns from this annotation.
      annCont = dir(fullfile(currAnn, currAnnName));
      pattn = size(annCont, 1);
      for patti = 1:pattn
        pattName = annCont(patti).name;
        % Check that it is a proper name, e.g., A, B, or some letters.
        if isletter(pattName(1)) &&...
            isempty(regexp(pattName, '.txt', 'once'))
          % Add the pattern.
          pattPath = fullfile(currAnn, currAnnName, pattName, 'csv');
          fname = dir(fullfile(pattPath, '*.csv'));
          fname = fname.name;
          P = csvread(fullfile(pattPath, fname));
          T(pattj).annotation = currAnnName;
          T(pattj).patternName = pattName;
          T(pattj).patternNotProjected = P;
          T(pattj).pattern = unique(P(:, projectionIdx), 'rows');
          % Add the occurrences.
          occPath = fullfile(currAnn, currAnnName, pattName,...
            'occurrences', 'csv');
          occCont = dir(occPath);
          occn = size(occCont, 1);
          occurrences = cell(1, occn);
          occj = 1; % Increment to populate occurrences.
          for occi = 1:occn
            currOcc = occCont(occi).name;
            if ~isempty(regexp(currOcc, 'occ', 'once')) &&...
                ~isempty(regexp(currOcc, '.csv', 'once'))
              occurrences{occj} = csvread(fullfile(occPath, currOcc));
              occj = occj + 1;
            end
          end
          occurrences = occurrences(1:occj - 1);
          T(pattj).occurrences = occurrences;
          pattj = pattj + 1;
        end
      end
    end
  end
  GT{ipiece} = struct;
  GT{ipiece}.k = size(projectionIdx, 2);
  GT{ipiece}.details = T;
end

% Comparing the algorithms' output to the ground truths, and save to a
% results file.
fid = fopen(fullfile(metricResultsPath, 'results.txt'), 'w');
% Iterate over the algorithms.
for ialgOut = 1:nalgOut
  [~, algStub, ~] = fileparts(algOut{ialgOut});
  fprintf(fid, 'Algorithm %d, %s\n', ialgOut, algStub);
  fprintf('Algorithm %d\n', ialgOut);
  fprintf(fid, ['n_P, n_Q, P_est, R_est, F1_est, '...
    'P_occ(c=.75), R_occ(c=.75), F_1occ(c=.75), '...
    'P_3, R_3, TLF_1, '...
    'runtime, FRT, FFTP_est, FFP, '...
    'P_occ(c=.5), R_occ(c=.5), F_1occ(c=.5), '...
    'P, R, F_1\n']);
  % Find the results for this algorithm.
  outCont = dir(fullfile(algOut{ialgOut}, '*.txt'));
  nOut = size(outCont, 1);
  % Iterate over the pieces.
  for ipiece = 1:npiece
    
    % Find relevant member of outCont for algorithm output.
    rel_idx = [];
    iOut = 1;
    while iOut <= nOut
      if ~isempty(regexp(outCont(iOut).name, labels{ipiece}, 'once')) &&...
        isempty(regexp(outCont(iOut).name, 'runtime', 'once'))
        rel_idx = iOut;
        iOut = nOut;
      end
      iOut = iOut + 1;
    end
    % Find relevant member of outCont for runtime.
    run_idx = [];
    iOut = 1;
    while iOut <= nOut
      if ~isempty(regexp(outCont(iOut).name, labels{ipiece}, 'once')) &&...
        ~isempty(regexp(outCont(iOut).name, 'runtime', 'once'))
        run_idx = iOut;
        iOut = nOut;
      end
      iOut = iOut + 1;
    end
    if ~isempty(rel_idx)
      fprintf(fid, '%s\n', labels{ipiece});
      fprintf('Piece %d\n', ipiece);
      % Load the results.
      Q = pattAllOccAll2struct(fullfile(algOut{ialgOut},...
        outCont(rel_idx).name));
      % Load the runtime and fifth return time if available.
      if ~isempty(run_idx)
        fid_run = fopen(fullfile(algOut{ialgOut}, outCont(run_idx).name));
        A = textscan(fid_run, '%s %f');
        fclose(fid_run);
        RT(ialgOut, ipiece) = A{2}(1);
        FRT(ialgOut, ipiece) = A{2}(2);
      end
      % Give the number of ground truth patterns and number of algorithm-
      % output patterns.
      fprintf(fid, '%d, %d, ', size(GT{ipiece}.details, 2), size(Q, 2));
      
      % Calculate and print establishment precision and recall.
      [p_est, r_est, S] = estPrecRecMat(GT{ipiece}, Q, similarFunc);
      if p_est == 0 && r_est == 0
        f1_est = 0;
      else
        f1_est = 2*p_est*r_est/(p_est + r_est);
      end
      fprintf(fid, '%6.5f, %6.5f, %6.5f, ', p_est, r_est, f1_est);
      
      % Calculate and print occurrence precision and recall for c = .75.
      [p_occ, r_occ, ~, ~] = occPrecRecMat(GT{ipiece}, Q,...
        similarThresh(1), similarFunc);
      if p_occ == 0 && r_occ == 0
        f1_occ = 0;
      else
        f1_occ = 2*p_occ*r_occ/(p_occ + r_occ);
      end
      fprintf(fid, '%6.5f, %6.5f, %6.5f, ', p_occ, r_occ, f1_occ);
      
      % Calculate and print three-layer precision and recall.
      [p3, r3, TLF] = threeLayerF1({GT{ipiece}.details(:).occurrences},...
        {Q.occurrences});
      fprintf(fid, '%6.5f, %6.5f, %6.5f, ', p3, r3, TLF);
      
      % Include runtime and FRT, and calculate FFTP_est and FFP. If there
      % are fewer than five patterns returned, use the variable idx5 to
      % avoid this causing an error.
      idx5 = min(5, size(Q, 2));
      [~, FFTP_est, ~] = estPrecRecMat(GT{ipiece}, Q(1:idx5), similarFunc);
      Q1st5 = {Q.occurrences};
      Q1st5 = Q1st5(1:idx5);
      [FFP, ~, ~] = threeLayerF1({GT{ipiece}.details(:).occurrences},...
        Q1st5);
      fprintf(fid, '%6.5f, %6.5f, %6.5f, %6.5f, ',...
        RT(ialgOut, ipiece), FRT(ialgOut, ipiece), FFTP_est, FFP);
      
      % Calculate and print occurrence precision and recall for c = .5.
      [p_occ, r_occ, ~, ~] = occPrecRecMat(GT{ipiece}, Q,...
        similarThresh(2), similarFunc);
      if p_occ == 0 && r_occ == 0
        f1_occ = 0;
      else
        f1_occ = 2*p_occ*r_occ/(p_occ + r_occ);
      end
      fprintf(fid, '%6.5f, %6.5f, %6.5f, ', p_occ, r_occ, f1_occ);
      
      % Calculate standard precision, recall, and F1.
      [p r ~] = precisionRecallTrans(Q, GT{ipiece});
      if p == 0 && r == 0
        f1 = 0;
      else
        f1 = 2*p*r/(p + r);
      end
      fprintf(fid, '%6.5f, %6.5f, %6.5f\n\n', p, r, f1);
      
    end
  end
end
