package org.restmediaserver.core.library

import java.io.File

import org.restmediaserver.core.files.mediafiles.MediaFile
import org.restmediaserver.core.library.SlickMediaLibrary.SongTuple
import slick.driver.H2Driver
import slick.driver.H2Driver.api._

import scala.concurrent.Future

/**
 * Created by Dan Pallas on 7/8/15.
 */
class SlickMediaLibrary(url: String, keepAliveConnection: Boolean, threads: Int, queue: Int)
  extends MediaLibrary{
  protected val db = Database.forURL(url = url,
    driver = H2Driver.getClass.getName,
    keepAliveConnection = keepAliveConnection,
    executor = AsyncExecutor("asyncExecutor", threads, queue))
  val songs = TableQuery[Songs]
  val dbSetup = DBIO.seq(songs.schema.create)
  db.run(dbSetup)

  override def getLibraryFolder(path: File): Future[Option[LibraryFolder]] = ???

  /** delete all contents in library */
  override def deleteAll(): Unit = ???

  override def putMediaFile(mediaFile: MediaFile): Future[Boolean] = ??? //{
//    mediaFile match {
//      case s: Song => put(s)
//      case _ => throw new IllegalStateException("SlickMediaLibrary.putMediaFile called with unhandled MediaFile");
//    }
//
//    def put(s: Song): Future[Boolean] =  ???
//  }

  override def removeLibraryFolder(path: String): Future[Int] = ???

  override def getSubDirs(parent: File): Future[Set[String]] = ???

  override def getSongCount(): Future[Int] = ???

  override def removeMediaFile(path: String): Future[Boolean] = ???

  override def close(): Unit = db.close()

  class Songs(tag: Tag) extends Table[SongTuple](tag, "dummy"){
    def path = column[String]("path")
    def fileType = column[String]("file_type")
    def modTime = column[Long]("mod_time")
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def bitRate = column[Long]("bit_rate")
    def channels = column[String]("channels")
    def encodingType = column[String]("encoding_type")
    def format = column[String]("format")
    def sampleRate = column[Int]("sample_rate")
    def trackLength = column[Int]("track_length")
    def vbr = column[Boolean]("vbr")
    def title = column[String]("title")
    def trackNumber = column[Option[Int]]("track_number")
    def trackTotal = column[Option[Int]]("track_total")
    def discNumber = column[Option[Int]]("disc_number")
    def discTotal = column[Option[Int]]("disc_total")
    def artist = column[String]("artist")
    def albumTitle = column[String]("album_title")
    def year = column[Option[Int]]("year")
    def genre = column[String]("genre")
    def comment = column[String]("comment")
    def composer = column[String]("composer")
    def originalArtist = column[String]("original_artist")
    def remixer = column[String]("remixer")
    def conductor = column[String]("conductor")
    def bpm = column[Option[Int]]("bmp")
    def grouping = column[String]("grouping")
    def isrc = column[String]("isrc")
    def recordLabel = column[String]("record_label")
    def encoder = column[String]("encoder")
    def lyracist = column[String]("lyracist")
    def lyrics = column[String]("lyrics")
    def albumArtist = column[String]("album_artist")
    def isCompilation = column[Boolean]("is_compilation")
    def hasArtwork = column[Boolean]("has_artwork")
    def * = 
      ((path, fileType, modTime, id.?, bitRate, channels, encodingType, format, sampleRate, trackLength),
      (vbr, title, trackNumber, trackTotal, discNumber, discTotal, artist, albumTitle, year, genre),
      (comment, composer, originalArtist, remixer, conductor, bpm, grouping, isrc, recordLabel, encoder),
      (lyracist, lyrics, albumArtist, isCompilation, hasArtwork)) <> (SongTuple.fromTuple, SongTuple.unapply)
    def pathIdx = index("path_idx", path, unique = true)
  }
}

object SlickMediaLibrary{

  case class SongTuple(path: String,
                               fileType: String,
                               modTime: Long,
                               id: Option[Int],
                               bitRate: Long,
                               channels: String,
                               encodingType: String,
                               format: String,
                               sampleRate: Int,
                               trackLength: Int,
                               vbr: Boolean,
                               title: String,
                               trackNumber: Option[Int],
                               trackTotal: Option[Int],
                               discNumber: Option[Int],
                               discTotal: Option[Int],
                               artist: String,
                               albumTitle: String,
                               year: Option[Int],
                               genre: String,
                               comment: String,
                               composer: String,
                               originalArtist: String,
                               remixer: String,
                               conductor: String,
                               bpm: Option[Int],
                               grouping: String,
                               isrc: String,
                               recordLabel: String,
                               encoder: String,
                               lyracist: String,
                               lyrics: String,
                               albumArtist: String,
                               isCompilation: Boolean,
                               hasArtwork: Boolean)
  object SongTuple {
    type SongTupled = ((String, String, Long, Option[Int], Long, String, String, String, Int, Int),
      (Boolean, String, Option[Int], Option[Int], Option[Int], Option[Int], String, String, Option[Int], String),
      (String, String, String, String, String, Option[Int], String, String, String, String),
      (String, String, String, Boolean, Boolean))

    def fromTuple(tuple: SongTupled): SongTuple = {
      val first = tuple._1
      val second = tuple._2
      val third = tuple._3
      val fourth = tuple._4
      SongTuple(first._1, first._2, first._3, first._4, first._5, first._6, first._7, first._8, first._9, first._10,
        second._1, second._2, second._3, second._4, second._5, second._6, second._7, second._8, second._9, second._10,
        third._1, third._2, third._3, third._4, third._5, third._6, third._7, third._8, third._9, third._10,
        fourth._1, fourth._2, fourth._3, fourth._4, fourth._5)
    }

    def unapply(x: SongTuple): Option[SongTupled] = {
      Some(
        (x.path, x.fileType, x.modTime, x.id, x.bitRate,
          x.channels, x.encodingType, x.format, x.sampleRate, x.trackLength),
        (x.vbr, x.title, x.trackNumber, x.trackTotal, x.discNumber,
          x.discTotal, x.artist, x.albumTitle, x.year, x.genre),
        (x.comment, x.composer, x.originalArtist, x.remixer, x.conductor,
          x.bpm, x.grouping, x.isrc, x.recordLabel, x.encoder),
        (x.lyracist, x.lyrics, x.albumArtist, x.isCompilation, x.hasArtwork)
      )
    }
  }


}
