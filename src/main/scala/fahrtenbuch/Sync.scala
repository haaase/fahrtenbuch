package fahrtenbuch
import com.raquo.laminar.api.L.*
import fahrtenbuch.model.Entry

object Sync:
  val entrySyncOut =
    Observer[Entry](onNext = Actions.sendEntry(_))
