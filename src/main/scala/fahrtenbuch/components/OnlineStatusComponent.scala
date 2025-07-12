package fahrtenbuch.components

import com.raquo.laminar.api.L.*
import com.raquo.airstream.core.Signal

class OnlineStatusComponent(online: Signal[Boolean]):
  def render(): HtmlElement = {
    val status = online.map {
      case true  => "Online"
      case false => "Offline"
    }

    span(
      cls := "tag",
      text <-- status
    )
  }
