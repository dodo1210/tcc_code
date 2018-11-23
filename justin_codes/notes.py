# CREATED: 11/9/15 3:57 PM by Justin Salamon <justin.salamon@nyu.edu>

import librosa
import vamp
import argparse
import os
import numpy as np
from midiutil.MidiFile import MIDIFile
from scipy.signal import medfilt
import jams

def intervalo(infile,notes):
    arq = open(infile+'.txt','r')
    str = arq.readlines()
    
    tudo = []
    for oi in str:
        nota = float(oi)
        tudo.append(nota)
    arq.close()

    arq = open(infile+'2.txt','r')
    arqi = open(infile+'3.txt','w')
    stri = arq.readlines()

    anterior = 0.0
    cont = 0
    notas = []

    y, sr = librosa.load(infile)

    # 3. Run the default beat tracker
    tempo, beat_frames = librosa.beat.beat_track(y=y, sr=sr)

    print('Estimated tempo: {:.2f} beats per minute'.format(tempo))

    millisegundos = 60000/tempo
    if millisegundos > 84:
        for oi in stri:
            nota = float(oi)
            if nota-anterior>millisegundos/10000:
                print(tudo[cont])
                notas.append(tudo[cont])
            anterior = nota
            cont=cont+1
        arq.close()
        arqi.close()
    else:
        for oi in stri:
            nota = float(oi)
            if nota-anterior>(millisegundos/2)/10000:
                print(tudo[cont])
                notas.append(tudo[cont])
            anterior = nota
            cont=cont+1
        arq.close()
        arqi.close()

    cont = 0
    arq = open('Adele_Hello.txt','a')
    for n in notas:
        if len(notas) == cont+1:
            break
        '''if n-notas[cont+1]==0:
            arq.write("unison\n")
        if n-notas[cont+1]==1 or n-notas[cont+1]==-1:
            arq.write("segunda menor\n")
        if n-notas[cont+1]==2 or n-notas[cont+1]==-2:
            arq.write("segunda maior\n")'''
        if n-notas[cont+1]==3 or n-notas[cont+1]==-3:
            arq.write("terceira menor\n")
        if n-notas[cont+1]==4 or n-notas[cont+1]==-4:
            arq.write("terceira maior\n")
        '''if n-notas[cont+1]==5 or n-notas[cont+1]==-5:
            arq.write("quarta justa\n")
        if n-notas[cont+1]==6 or n-notas[cont+1]==-6:
            arq.write("quarta aumentada\n")
        if n-notas[cont+1]==7 or n-notas[cont+1]==-7:
            arq.write("quinta justa\n")
        if n-notas[cont+1]==8 or n-notas[cont+1]==-8:
            arq.write("quinta aumentada\n")
        if n-notas[cont+1]==9 or n-notas[cont+1]==-9:
            arq.write("sexta maior\n")
        if n-notas[cont+1]==10 or n-notas[cont+1]==-10:
            arq.write("sétima menor\n")
        if n-notas[cont+1]==11 or n-notas[cont+1]==-11:
            arq.write("sétima maior\n")'''
        cont = cont+1

    arq.close()



def audio_to_midi_melodia(infile, outfile, bpm, smooth=0.25, minduration=0.1,
                          savejams=False):

    # define analysis parameters
    fs = 44100
    hop = 128

    # load audio using librosa
    print("Loading audio...")
    data, sr = librosa.load(infile, sr=fs, mono=True)

    # extract melody using melodia vamp plugin
    print("Extracting melody f0 with MELODIA...")
    melody = vamp.collect(data, sr, "mtg-melodia:melodia",
                          parameters={"voicing": 0.2})

    # hop = melody['vector'][0]
    pitch = melody['vector'][1]

    # impute missing 0's to compensate for starting timestamp
    pitch = np.insert(pitch, 0, [0]*8)
    # debug
    # np.asarray(pitch).dump('f0.npy')
    # print(len(pitch))

    # convert f0 to midi notes
    print("Converting Hz to MIDI notes...")
    midi_pitch = hz2midi(pitch)

    # segment sequence into individual midi notes
    notes = midi_to_notes(midi_pitch, fs, hop, smooth, minduration, infile)
    print(notes)
    
def hz2midi(hz):
    # convert from Hz to midi note
    hz_nonneg = hz.copy()
    hz_nonneg[hz <= 0] = 1
    midi = 69 + 12*np.log2(hz_nonneg/440)
    midi[midi <= 0] = 0
    # round
    midi = np.round(midi)
    return midi

def save_midi(outfile, notes, tempo):

    track = 0
    time = 0
    midifile = MIDIFile(1)

    # Add track name and tempo.
    midifile.addTrackName(track, time, "MIDI TRACK")
    midifile.addTempo(track, time, tempo)

    channel = 0
    volume = 100

    for note in notes:
        onset = note[0] * (tempo/60.)
        duration = note[1] * (tempo/60.)
        # duration = 1
        pitch = note[2]
        midifile.addNote(track, channel, pitch, onset, duration, volume)

    # And write it to disk.
    binfile = open(outfile, 'wb')
    midifile.writeFile(binfile)
    binfile.close()

    return notes


def midi_to_notes(midi, fs, hop, smooth, minduration, infile):

    # smooth midi pitch sequence first
    if (smooth > 0):
        filter_duration = smooth  # in seconds
        filter_size = int(filter_duration * fs / float(hop))
        if filter_size % 2 == 0:
            filter_size += 1
        midi_filt = medfilt(midi, filter_size)
    else:
        midi_filt = midi
    # print(len(midi),len(midi_filt))

    notes = []
    p_prev = 0
    duration = 0
    onset = 0
    atual = 0
    anterior = 0
    for n, p in enumerate(midi_filt):
        if p == p_prev:
            duration += 1
        else:
            # treat 0 as silence
            if p_prev > 0:
                # add note
                duration_sec = duration * hop / float(fs)
                # only add notes that are long enough
                if duration_sec >= minduration:
                    onset_sec = onset * hop / float(fs)
                    atual = duration_sec
                    #onset_sec = local onde será colocado a nota
                    #duration = comprimento da nota
                    #p_prev = mediana de cada janela, na biblioteca midiUTIL é a nota
                    notes.append((onset_sec, duration_sec, p_prev))

            # start new note
            '''arq = open(infile+'.txt','a')
            if p_prev!=0:
                arq.write(str(p_prev)+"\n")
            arq.close()
            
            arq = open(infile+'2.txt','a')
            if p_prev!=0:
                arq.write(str(atual)+"\n")
            arq.close()'''
            onset = n
            duration = 1
            p_prev = p


    # add last note
    if p_prev > 0:
        # add note
        duration_sec = duration * hop / float(fs)
        onset_sec = onset * hop / float(fs)
        '''notes.append((onset_sec, duration_sec, p_prev))
        arq = open(infile+'.txt','a')
        if(p!=0):
            arq.write(str(p_prev)+"\n")
        arq.close()    '''
    #intervalo(infile,notes)
    return notes

def save_jams(jamsfile, notes, track_duration, orig_filename):

    # Construct a new JAMS object and annotation records
    jam = jams.JAMS()

    # Store the track duration
    jam.file_metadata.duration = track_duration
    jam.file_metadata.title = orig_filename

    midi_an = jams.Annotation(namespace='pitch_midi',
                              duration=track_duration)
    midi_an.annotation_metadata = \
        jams.AnnotationMetadata(
            data_source='audio_to_midi_melodia.py v%s' % __init__.__version__,
            annotation_tools='audio_to_midi_melodia.py (https://github.com/'
                             'justinsalamon/audio_to_midi_melodia)')

    # Add midi notes to the annotation record.
    for n in notes:
        midi_an.append(time=n[0], duration=n[1], value=n[2], confidence=0)

    # Store the new annotation in the jam
    jam.annotations.append(midi_an)

    # Save to disk
    jam.save(jamsfile)

if __name__ == "__main__":

    parser = argparse.ArgumentParser()
    parser.add_argument("infile", help="Path to input audio file.")
    parser.add_argument("outfile", help="Path for saving output MIDI file.")
    parser.add_argument("bpm", type=int, help="Tempo of the track in BPM.")
    parser.add_argument("--smooth", type=float, default=0.25,
                        help="Smooth the pitch sequence with a median filter "
                             "of the provided duration (in seconds).")
    parser.add_argument("--minduration", type=float, default=0.1,
                        help="Minimum allowed duration for note (in seconds). "
                             "Shorter notes will be removed.")
    parser.add_argument("--jams", action="store_const", const=True,
                        default=False, help="Also save output in JAMS format.")

    args = parser.parse_args()

    notes = audio_to_midi_melodia(args.infile, args.outfile, args.bpm,
                          smooth=args.smooth, minduration=args.minduration,
                          savejams=args.jams)

