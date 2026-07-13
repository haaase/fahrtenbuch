package fahrtenbuch.components
import com.raquo.laminar.api.L.*
import com.raquo.laminar.api.features.unitArrows
import fahrtenbuch.DexieDB
import org.scalajs.dom

class RoomIDComponent():
  // tracks whenever a user clicks on an edit button
  val roomIDInput =
    input(
      cls := "input",
      placeholder := "#roomId",
      onKeyDown.filter(_.key == "Enter") --> { _ => setRoomID() }
    )

  def setRoomID(): Unit =
    val id = roomIDInput.ref.value
    DexieDB.setRoomId(id)
    dom.window.location.hash = id
    dom.window.location.reload()

  def render(): HtmlElement =
    div(
      span("Bitte eine Raum-ID eingeben:"),
      roomIDInput,
      button(
        cls := "button is-success",
        "Absenden",
        onClick --> {
          setRoomID()
        }
      )
    )
end RoomIDComponent
