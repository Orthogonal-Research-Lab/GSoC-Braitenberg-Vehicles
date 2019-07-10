package presenter

import tornadofx.*

/**
 * Event signaling that the simulation render components should be updated.
 */
class UpdateRenderEvent : FXEvent(EventBus.RunOn.ApplicationThread)
