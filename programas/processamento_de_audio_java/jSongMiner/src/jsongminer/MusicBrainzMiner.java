/*
 * MusicBrainzMiner.java
 * Version 1.1
 *
 * Last modified on September 25, 2011.
 * University of Waikato and McGill University
 */

package jsongminer;

import com.slychief.javamusicbrainz.entities.*;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

/**
 * Objects of this class are used to extract information from the Music Brainz
 * API with respect to a given song. Upon instantiation, an object of this class
 * is given identifying information about a song, and a Music Brainz Track
 * object is stored. Further method calls to an object of this MusicBrainzMiner
 * class can be used to extract and access further information related to the
 * song associated with this Track object. Each MusicBrainzMiner object is
 * associated with one and only one song.
 *
 * <p>Metadata is extracted from the MusicBrainz API, which is described at
 * http://musicbrainz.org/doc/XML_Web_Service/Version_2. The queries are
 * formatted and the results interpereted using the JavaMusicbrainz.jar library,
 * which is available at http://javamusicbrainz.sourceforge.net/index.html.
 *
 * @author Cory McKay
 */
public class MusicBrainzMiner
{
	/* FIELDS *****************************************************************/


	/**
	 * The code used to indicate in MusicMetaData objects that a piece of data
	 * is from Music Brainz.
	 */
	private String music_brainz_source_identifier_code;

	/**
	 * The maximum number of items of any given field to include in reports.
	 */
	private static int max_to_report;

	/**
	 * The Music Brainz Track that is being analyzed.
	 */
	private Track track;


	/* CONSTRUCTOR ************************************************************/


	/**
	 * Initialize this object by setting up its fields. This includes accessing
	 * a Music Brainz Track object from the Music Brainz API based on the
	 * provided song title and artist name metadata.
	 *
	 * @param song_title		The title of the song. May not be null.
	 * @param artist_name		The "artist" of the song (typically refers
	 *							to performer/band, although it is sometimes
	 *							used to refer to composer, especially for
	 *							classical music). This may be null if this
	 *							information is unknown.
	 * @throws Exception		An informative exception is thrown if invalid
	 *							parameters are provided or if a match could not
	 *							be found for the specified song title and
	 *							artist.
	 */
	public MusicBrainzMiner(String song_title, String artist_name)
			throws Exception
	{
		// Set basic fields
		music_brainz_source_identifier_code = "Music Brainz API";
		max_to_report = 10;

		// Retrieve Music Brainz Track information
		track = null;
		setTrack(song_title, artist_name, false);
	}


	/* PUBLIC METHODS *********************************************************/


	/**
	 * Extract all available metadata on Music Braizn for the Track stored in
	 * this object. This excludes metadata about the artist, however, which
	 * can be obtained using the getArtistMetaData method.
	 *
	 * @param store_fails	If this is true, then for each individual piece of
	 *						metadata that cannot be extracted from the Music
	 *						Brainz API an indication is added to the returned
	 *						MusicMetaData array highlighting the failure. If
	 *						this is false then fields that cannot be extracted
	 *						are simply ignored.
	 * @return				An array holding references to all of the extracted
	 *						track metadata. Null is returned if no data could be
	 *						found.
	 */
	public MusicMetaData[] getSongMetaData(boolean store_fails)
	{
		// The metadata extracted to date by this method
		Vector<MusicMetaData> results = new Vector<MusicMetaData>();

		// Extract the song title
		try {results.add(new MusicMetaData(music_brainz_source_identifier_code, "Song Title", track.getTitle()));}
		catch (Exception e) {if (store_fails) results.add(new MusicMetaData(music_brainz_source_identifier_code, "Song Title", "Could not extract this information: " + e.getMessage()));}

		// Extract the Music Brainz Song ID
		try {results.add(new MusicMetaData(music_brainz_source_identifier_code, "Music Brainz Song ID", track.getId()));}
		catch (Exception e) {if (store_fails) results.add(new MusicMetaData(music_brainz_source_identifier_code, "Music Brainz Song ID", "Could not extract this information: " + e.getMessage()));}

		// Extract the artist name
		try {results.add(new MusicMetaData(music_brainz_source_identifier_code, "Artist Name", track.getArtist().getName()));}
		catch (Exception e) {if (store_fails) results.add(new MusicMetaData(music_brainz_source_identifier_code, "Artist Name", "Could not extract this information: " + e.getMessage()));}

		// Extract the Artist Music Brainz ID
		try {results.add(new MusicMetaData(music_brainz_source_identifier_code, "Music Brainz Artist ID", track.getArtist().getId()));}
		catch (Exception e) {if (store_fails) results.add(new MusicMetaData(music_brainz_source_identifier_code, "Music Brainz Artist ID", "Could not extract this information: " + e.getMessage()));}

		// Extract the album title
		try {results.add(new MusicMetaData(music_brainz_source_identifier_code, "Album Title", track.getReleases().getReleases().get(0).getTitle()));}
		catch (Exception e) {if (store_fails) results.add(new MusicMetaData(music_brainz_source_identifier_code, "Album Title", "Could not extract this information: " + e.getMessage()));}

		// Extract the album Music Brainz ID
		try {results.add(new MusicMetaData(music_brainz_source_identifier_code, "Music Brainz Album ID", track.getReleases().getReleases().get(0).getId()));}
		catch (Exception e) {if (store_fails) results.add(new MusicMetaData(music_brainz_source_identifier_code, "Music Brainz Album ID", "Could not extract this information: " + e.getMessage()));}

		// Extract the song release 
		try
		{
			List<Release> list = track.getReleases().getReleases();
			int this_max = list.size();
			if (max_to_report < this_max) this_max = max_to_report;
			for (int i = 0; i < this_max; i++)
				results.add(new MusicMetaData(music_brainz_source_identifier_code, "Song Release (Release " + (i + 1) + ")", list.get(i).getTitle()));
		}
		catch (Exception e) {if (store_fails) results.add(new MusicMetaData(music_brainz_source_identifier_code, "Song Release", "Could not extract this information: " + e.getMessage()));}

		// Extract the song duration
		try {results.add(new MusicMetaData(music_brainz_source_identifier_code, "Duration (seconds)", (new Integer(track.getDuration() / 1000)).toString()));}
		catch (Exception e) {if (store_fails) results.add(new MusicMetaData(music_brainz_source_identifier_code, "Duration (seconds)", "Could not extract this information: " + e.getMessage()));}

		// Return the extracted metadata
		if (results.isEmpty()) return null;
		else return results.toArray(new MusicMetaData[results.size()]);
	}


	/**
	 * Extract all available on Music Brainz metadata for the artist associated
	 * with the Track stored in this object.
	 *
	 * @param store_fails	If this is true, then for each individual piece of
	 *						metadata that cannot be extracted from the Music
	 *						Brainz API an indication is added to the returned
	 *						MusicMetaData array highlighting the failure. If
	 *						this is false then fields that cannot be extracted
	 *						are simply ignored.
	 * @return				An array holding references to all of the extracted
	 *						artist metadata. Null is returned if no data could
	 *						be found.
	 */
	public MusicMetaData[] getArtistMetaData(boolean store_fails)
	{
		// The metadata extracted to date by this method
		Vector<MusicMetaData> results = new Vector<MusicMetaData>();

		// Prepare a Music Brainz Artist object
		Artist artist = track.getArtist();

		// Extract the artist name
		try {results.add(new MusicMetaData(music_brainz_source_identifier_code, "Artist Name", artist.getName()));}
		catch (Exception e) {if (store_fails) results.add(new MusicMetaData(music_brainz_source_identifier_code, "Artist Name", "Could not extract this information: " + e.getMessage()));}

		// Extract the Artist Music Brainz ID
		try {results.add(new MusicMetaData(music_brainz_source_identifier_code, "Music Brainz Artist ID", artist.getId()));}
		catch (Exception e) {if (store_fails) results.add(new MusicMetaData(music_brainz_source_identifier_code, "Music Brainz Artist ID", "Could not extract this information: " + e.getMessage()));}

		// Extract the artist's years active
		try {results.add(new MusicMetaData(music_brainz_source_identifier_code, "Years Active", (artist.getBegin() + " to " + artist.getEnd())));}
		catch (Exception e) {if (store_fails) results.add(new MusicMetaData(music_brainz_source_identifier_code, "Years Active", "Could not extract this information: " + e.getMessage()));}

		// Extract the artist's lifespan
		try {results.add(new MusicMetaData(music_brainz_source_identifier_code, "Lifespan", (artist.getLifespan().getBegin() + " to " + artist.getLifespan().getEnd())));}
		catch (Exception e) {if (store_fails) results.add(new MusicMetaData(music_brainz_source_identifier_code, "Lifespan", "Could not extract this information: " + e.getMessage()));}

		// Extract the artist aliases
		try
		{
			List<Alias> list = artist.getAliaslist().getAliases();
			int this_max = list.size();
			if (max_to_report < this_max) this_max = max_to_report;
			for (int i = 0; i < this_max; i++)
				results.add(new MusicMetaData(music_brainz_source_identifier_code, "Artist Alias (Alias " + (i + 1) + ")", list.get(i).getName()));
		}
		catch (Exception e) {if (store_fails) results.add(new MusicMetaData(music_brainz_source_identifier_code, "Artist Alias", "Could not extract this information: " + e.getMessage()));}

		// Extract the top albums
		try
		{
			List<Release> list = artist.getReleases();
			int this_max = list.size();
			if (max_to_report < this_max) this_max = max_to_report;
			for (int i = 0; i < this_max; i++)
				results.add(new MusicMetaData(music_brainz_source_identifier_code, "Top Albums (Album " + (i + 1) + ")", list.get(i).getTitle()));
		}
		catch (Exception e) {if (store_fails) results.add(new MusicMetaData(music_brainz_source_identifier_code, "Top Albums", "Could not extract this information: " + e.getMessage()));}

		// Return the extracted metadata
		if (results.isEmpty()) return null;
		else return results.toArray(new MusicMetaData[results.size()]);
	}


	/**
	 * Extract all available on Music Brainz metadata for the album associated
	 * with the Track stored in this object. Note that only the first matching
	 * album is used.
	 *
	 * @param store_fails	If this is true, then for each individual piece of
	 *						metadata that cannot be extracted from the Music
	 *						Brainz API an indication is added to the returned
	 *						MusicMetaData array highlighting the failure. If
	 *						this is false then fields that cannot be extracted
	 *						are simply ignored.
	 * @return				An array holding references to all of the extracted
	 *						album metadata. Null is returned if no data could
	 *						be found.
	 */
	public MusicMetaData[] getAlbumMetaData(boolean store_fails)
	{
		// The metadata extracted to date by this method
		Vector<MusicMetaData> results = new Vector<MusicMetaData>();

		// Prepare a set of Music Brainz Release objects
		Release album = track.getReleases().getReleases().get(0);

		// Extract the album name
		try {results.add(new MusicMetaData(music_brainz_source_identifier_code, "Album Title", album.getTitle()));}
		catch (Exception e) {if (store_fails) results.add(new MusicMetaData(music_brainz_source_identifier_code, "Album Title", "Could not extract this information: " + e.getMessage()));}

		// Extract the album Music Brainz ID
		try {results.add(new MusicMetaData(music_brainz_source_identifier_code, "Music Brainz Album ID", album.getId()));}
		catch (Exception e) {if (store_fails) results.add(new MusicMetaData(music_brainz_source_identifier_code, "Album Album Brainz ID", "Could not extract this information: " + e.getMessage()));}

		// Extract the album Amazon ID
		try {results.add(new MusicMetaData(music_brainz_source_identifier_code, "Amazon Album ID", album.getAsin()));}
		catch (Exception e) {if (store_fails) results.add(new MusicMetaData(music_brainz_source_identifier_code, "Amazon Album ID", "Could not extract this information: " + e.getMessage()));}

		// Extract the release type
		try {results.add(new MusicMetaData(music_brainz_source_identifier_code, "Release Type", (album.getType())));}
		catch (Exception e) {if (store_fails) results.add(new MusicMetaData(music_brainz_source_identifier_code, "Release Type", "Could not extract this information: " + e.getMessage()));}

		// Extract the artist name
		try {results.add(new MusicMetaData(music_brainz_source_identifier_code, "Artist Name", album.getArtist().getName()));}
		catch (Exception e) {if (store_fails) results.add(new MusicMetaData(music_brainz_source_identifier_code, "Artist Name", "Could not extract this information: " + e.getMessage()));}

		// Extract the artist Music Brainz ID
		try {results.add(new MusicMetaData(music_brainz_source_identifier_code, "Music Brainz Artist ID", album.getArtist().getName()));}
		catch (Exception e) {if (store_fails) results.add(new MusicMetaData(music_brainz_source_identifier_code, "Music Brainz Artist ID", "Could not extract this information: " + e.getMessage()));}

		// Extract the number of tracks
		try {results.add(new MusicMetaData(music_brainz_source_identifier_code, "Number of Tracks", (new Integer (album.getTracks().size())).toString()));}
		catch (Exception e) {if (store_fails) results.add(new MusicMetaData(music_brainz_source_identifier_code, "Number of Tracks", "Could not extract this information: " + e.getMessage()));}

		// Extract the album track list
		try
		{
			List<Track> list = album.getTracks();
			int this_max = list.size();
			if (max_to_report < this_max) this_max = max_to_report;
			for (int i = 0; i < this_max; i++)
				results.add(new MusicMetaData(music_brainz_source_identifier_code, "Track List (Track " + (i + 1) + ")", list.get(i).getTitle()));
		}
		catch (Exception e) {if (store_fails) results.add(new MusicMetaData(music_brainz_source_identifier_code, "Track List", "Could not extract this information: " + e.getMessage()));}

		// Return the extracted metadata
		if (results.isEmpty()) return null;
		else return results.toArray(new MusicMetaData[results.size()]);
	}


	/**
	 * Extracts all available song and, if appropriate, artist and album
	 * metadata from the Music Brainz API for the Track stored in this object.
	 * Adds the extracted metadata to the Vector parameters of this method.
	 *
	 * <p>Song metadata is always extracted. Artist and album metadata are only
	 * extracted if they have not already been extracted for the given artist
	 * or album (respectively) during the processing of another song, as
	 * indicated by the presence of the artist's name (in lower case) in the
	 * artists_already_extracted parameter, or the album's title (in lower case)
	 * in the albums_already_extracted parameter, respectively. Note that
	 * this method does <b>not</b> update either of these hash maps in any way,
	 * as it is left to the calling object to do this.
	 *
	 * @param song_metadata					A Vector of metadata already
	 *										extracted for this song. May not
	 *										be null, but may be empty.
	 * @param artist_metadata				A Vector of metadata already
	 *										extracted for the artist associated
	 *										with this song. May not be null, but
	 *										may be empty.
	 * @param album_metadata				A Vector of metadata already
	 *										extracted for the album associated
	 *										with this song. May not be null, but
	 *										may be empty.
	 * @param artists_already_extracted		A hash map whose keys correspond to
	 *										artist names (converted to lower
	 *										case), and whose values correspond
	 *										to vectors of corresponding artist
	 *										metadata. The contents are based on
	 *										the processing of previous songs.
	 *										This parameter may be null, in which
	 *										case it is ignored	and artist
	 *										extraction happens automatically.
	 *										Also, the values may be null, in
	 *										order to save space and processing
	 *										for the calling object, but this has
	 *										no effect on this method, as only
	 *										the keys are used.
	 * @param albums_already_extracted		A hash map whose keys correspond to
	 *										album titles (converted to lower
	 *										case), and whose values correspond
	 *										to vectors of corresponding album
	 *										metadata. The contents are based on
	 *										the processing of previous songs.
	 *										This parameter may be null, in which
	 *										case it is ignored	and album
	 *										extraction happens automatically.
	 *										Also, the values may be null, in
	 *										order to save space and processing
	 *										for the calling object, but this has
	 *										no effect on this method, as only
	 *										the keys are used.
	 * @param store_fails					If this is true, then for each
	 *										individual piece of metadata that
	 *										cannot be extracted from the Last.FM
	 *										API an indication is added to
	 *										the stored MusicMetaData
	 *										highlighting the failure. If this is
	 *										false then fields that cannot
	 *										be extracted are simply ignored.
	 *										Note that this parameter has no
	 *										effect on song identification
	 *										specifically.
	 * @throws Exception					An exception is thrown if invalid
	 *										parameters are passed.
	 * @return								True is returned if a change was
	 *										made to song_metadata,
	 *										artist_metadata or both.
	 */
	public boolean getAllAvailableNewMetaData( Vector<MusicMetaData> song_metadata,
			Vector<MusicMetaData> artist_metadata,
			Vector<MusicMetaData> album_metadata,
			HashMap< String, Vector<MusicMetaData> > artists_already_extracted,
			HashMap< String, Vector<MusicMetaData> > albums_already_extracted,
			boolean store_fails )
			throws Exception
	{
		// Verify input parameters
		if (song_metadata == null || artist_metadata == null || album_metadata == null)
			throw new Exception ("Song, artist and album vectors must be provided (although they may be empty).");

		// Whether or not a change has been made
		boolean some_data_successfully_extracted = false;

		// Extract song metadata
		MusicMetaData[] new_song_metadata = getSongMetaData(store_fails);

		// Add all song metadata extracted so far to the provided Vector
		if (new_song_metadata != null)
		{
			for (int i = 0; i < new_song_metadata.length; i++)
				song_metadata.add(new_song_metadata[i]);
			some_data_successfully_extracted = true;
		}

		// Test if should extract artist metadata
		boolean extract_artist_data = true;
		if (artists_already_extracted != null)
		{
			String artist_name = track.getArtist().getName();
			if (artist_name == null)
			{
				extract_artist_data = false;
			}
			else
			{
				artist_name = artist_name.toLowerCase();
				if (artists_already_extracted.containsKey(artist_name))
					extract_artist_data = false;
			}
		}

		// Extract artist metadata and store it, if appropriate
		if (extract_artist_data)
		{
			// Extract artist metadat for the specified song
			MusicMetaData[] new_artist_data = getArtistMetaData(store_fails);

			if (new_artist_data != null)
			{
				// Add artist metadata to the artist_metadata Vector
				for (int i = 0; i < new_artist_data.length; i++)
					artist_metadata.add(new_artist_data[i]);

				// Note that a change has been made
				some_data_successfully_extracted = true;
			}
		}

		// Test if should extract album metadata
		boolean extract_album_data = true;
		if (albums_already_extracted != null)
		{
			String album_name = track.getReleases().getReleases().get(0).getTitle();
			if (album_name == null)
			{
				extract_album_data = false;
			}
			else
			{
				album_name = album_name.toLowerCase();
				if (albums_already_extracted.containsKey(album_name))
					extract_album_data = false;
			}
		}

		// Extract album metadata and store it, if appropriate
		if (extract_album_data)
		{
			// Extract artist metadat for the specified song
			MusicMetaData[] new_album_data = getAlbumMetaData(store_fails);

			if (new_album_data != null)
			{
				// Add album metadata to the album_metadata Vector
				for (int i = 0; i < new_album_data.length; i++)
					album_metadata.add(new_album_data[i]);

				// Note that a change has been made
				some_data_successfully_extracted = true;
			}
		}

		// Return whether some data was successfully extracted
		return some_data_successfully_extracted;
	}


	/* PRIVATE METHODS ********************************************************/


	/**
	 * Retrieve a Music Brainz Track with the given song title and, optionally,
	 * artist name, and store it in this object's track field.
	 *
	 * @param song_title		The title of the song. May not be null.
	 * @param artist_name		The "artist" of the song (typically refers
	 *							to performer/band, although it is sometimes
	 *							used to refer to composer, especially for
	 *							classical music). This may be null if this
	 *							information is unknown.
	 * @param called_already	Whether or not this method has already been
	 *							called. Needed to preven an infinite loop.
	 * @throws Exception		An informative exception is thrown if invalid
	 *							parameters are provided or if a match could not
	 *							be found for the specified song title and
	 *							artist.
	 */
	private void setTrack( String song_title,
			String artist_name,
			boolean called_already )
			throws Exception
	{
		// Verify that a song title is specified
		if (song_title == null)
			throw new Exception("A song title must be specified in order to attempt to identify it.");
		if (song_title.equals(""))
			throw new Exception("A song title must be specified in order to attempt to identify it.");

		// Retrieve all tracks known to Music Brainz with similar titles to
		// song_title.
		Exception track_not_found_exception = new Exception("Could not find a Music Brainz track matching " + song_title);
		List<Track> matching_tracks = Track.findByTitle(song_title);
		if (matching_tracks == null ) throw track_not_found_exception;
		if (matching_tracks.size() == 0) throw track_not_found_exception;

		// Filter the similar tracks so that only exact mataches are kept
		int current_track = 0;
		while (current_track < matching_tracks.size())
		{
			if (!matching_tracks.get(current_track).getTitle().equals(song_title))
				matching_tracks.remove(current_track);
			else current_track++;
		}
		if (matching_tracks.size() == 0) throw track_not_found_exception;

		// Match the Track to an artist if an artist is specified
		if (artist_name != null)
		{
			// Retrieve all artists known by MusicBrainz with a similar name to
			// artist_name
			List<Artist> matching_artists = Artist.findByName(artist_name);

			// Find an artist that matches artist_name exactly and store it in
			// matched_artist Throw an exception if it cannot be found.
			Artist matched_artist = null;
			int current_artist = 0;
			Exception artist_not_found_exception = new Exception("Could not find a Music Brainz artist matching " + artist_name);
			if (matching_artists == null ) throw artist_not_found_exception;
			if (matching_artists.size() == 0) throw artist_not_found_exception;
			while (matched_artist == null && current_artist < matching_artists.size())
			{
				if (matching_artists.get(current_artist).getName().equals(artist_name))
					matched_artist = matching_artists.get(current_artist);
				current_artist++;
			}
			if (matched_artist == null) throw artist_not_found_exception;

			// Find the best track that has the same artist as matched_artist
			// and store it in track
			int this_track = 0;
			while (this_track < matching_tracks.size() && track == null)
			{
				if (matching_tracks.get(this_track).getArtist().getId().equals(matched_artist.getId()))
					track = matching_tracks.get(this_track);
				else this_track++;
			}
			if (track == null) throw new Exception("Could not find a Music Brainz track matching " + song_title + " by " + artist_name);
		}

		// If no artist is specified, then choose the best matching track
		else track = matching_tracks.get(0);
		if (track == null) throw new Exception("Could not find a Music Brainz track matching " + song_title);
	}
}