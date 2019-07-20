package view

import tornadofx.*

class RenderReadyEvent : FXEvent(EventBus.RunOn.BackgroundThread)
/**
 * Event signaling that the simulation render components should be updated.
 */
class UpdateRenderEvent : FXEvent(EventBus.RunOn.ApplicationThread)