package fahrtenbuch
import com.raquo.laminar.api.L.*
import fahrtenbuch.model.Entry
import fahrtenbuch.DexieDB.upsertEntry
import fahrtenbuch.Main.allEntriesVar

object Sync:
  val entrySyncOut =
    Observer[Entry](onNext = Actions.sendEntry(_))

  val entrySyncIn =
    Actions.receiveEntry(received => upsertEntry(received))

  // sync all entries on initial connection
  Trystero.room.onPeerJoin(peerId =>
    Trystero.updatePeers()
    allEntriesVar
      .now()
      .foreach(entry => Actions.sendEntry(entry, List(peerId)))
  )
