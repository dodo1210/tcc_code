function d = mirdesign(orig,argin,option,postoption,specif,type)

if nargin == 0
    d.method = {};
    d.argin = '';
    d.option = {};
    d.postoption = {};
    d.specif = struct;
    d.type = '';
    d.frame = {};
    d.segment = {};
    d.chunkdecomposed = 0;
    d.size = {};
    d.file = '';
    d.channel = [];
    d.scale = [];
    d.sampling = 0;
    d.length = 0;
    d.resampling = 0;
    d.chunksizefactor = 0;
    d.nochunk = 0;
    d.overlap = 0;
    d.separate = 0;
    d.presilence = 0;
    d.postsilence = 0;
    d.chunk = [];
    d.eval = 0;
    d.interchunk = [];
    d.acrosschunks = [];
    d.ready = 0;
    d.struct = [];
    d.stored = [];
    d.index = NaN;
    d.tmpof = [];
elseif isa(orig,'mirdesign')
    d.method = orig.method;
    d.argin = orig.argin;
    d.option = orig.option;
    d.postoption = orig.postoption;
    d.specif = orig.specif;
    d.type = orig.type;
    d.frame = orig.frame;
    d.segment = orig.segment;
    d.chunkdecomposed = orig.chunkdecomposed;
    d.size = orig.size;
    d.file = orig.file;
    d.channel = orig.channel;
    d.scale = orig.scale;
    d.sampling = orig.sampling;
    d.length = orig.length;
    d.resampling = orig.resampling;
    d.chunksizefactor = orig.chunksizefactor;
    d.nochunk = orig.nochunk;
    d.overlap = orig.overlap;
    d.separate = orig.separate;
    d.presilence = orig.presilence;
    d.postsilence = orig.postsilence;
    d.chunk = orig.chunk;
    d.eval = orig.eval;
    d.interchunk = orig.interchunk;
    d.acrosschunks = orig.acrosschunks;
    d.ready = orig.ready;
    d.struct = orig.struct;
    d.stored = orig.stored;
    d.index = orig.index;
    d.tmpof = orig.tmpof;
else
    d.method = orig;
    d.argin = argin;
    d.option = option;
    d.postoption = postoption;
    d.specif = specif;
    d.type = type;
    if ischar(argin)
        d.frame = {};
        d.segment = {};
        d.chunkdecomposed = 0;
        d.size = {};
        d.file = '';
        d.channel = [];
        d.scale = [];
        d.sampling = 0;
        d.length = 0;
        d.resampling = 0;
        d.chunksizefactor = 1;
        d.nochunk = 0;
        d.overlap = 0;
        d.separate = 0;
        d.presilence = 0;
        d.postsilence = 0;
    else
        if iscell(argin)
            argin = argin{1};
        end
        if (strcmp(func2str(orig),'mirspectrum') && d.option.alongbands) ...
            || (isfield(specif,'nochunk') && specif.nochunk)
            d.frame = [];
            if isfield(d.specif,'eachchunk')
                d.specif = rmfield(d.specif,'eachchunk');
                d.specif = rmfield(d.specif,'combinechunk');
            end
        else
            d.frame = argin.frame;
            if not(isempty(d.frame))
                if isfield(d.specif,'extensive') 
                    d.frame.dontchunk = 1;
                    % Some frame-decomposed extractor should not be evaluated
                    % chunk after chunk because the whole result is needed for
                    % subsequent computations.
                elseif isfield(d.specif,'chunkframebefore')
                    d.frame.chunkbefore = 1;
                end
            end
        end
        d.segment = argin.segment;
        d.chunkdecomposed = argin.chunkdecomposed;
        d.size = argin.size;
        d.file = argin.file;
        d.channel = argin.channel;
        d.scale = argin.scale;
        d.sampling = argin.sampling;
        d.length = argin.length;
        d.resampling = argin.resampling;
        d.chunksizefactor = argin.chunksizefactor;
        if (isfield(specif,'nochunk') && specif.nochunk) 
            d.nochunk = 1; % was previously 2
        elseif not(isempty(argin.stored))
            % a temporary variable will be already computed.
            d.nochunk = 2; % Flag to indicate that no chunk should be 
                           % performed. Temporary variables cannot for the 
                           % moment be dispatched to dependent variables 
                           % chunk by chunk, but only once the whole 
                           % variable has been computed.
        else
            d.nochunk = argin.nochunk;
        end
        d.chunksizefactor = argin.chunksizefactor;
        d.overlap = argin.overlap;
        d.separate = argin.separate;
        d.presilence = argin.presilence;
        d.postsilence = argin.postsilence;
    end
    d.chunk = [];
    d.eval = 0;
    d.interchunk = [];   % Data that can be passed between successive chunks during the main process.
    d.acrosschunks = []; % Data that can be accumulated among chunks during the beforechunk process.
    d.ready = 0;
    d.struct = [];
    d.stored = [];
    d.index = NaN;
    d.tmpof = [];
end
d = class(d,'mirdesign');