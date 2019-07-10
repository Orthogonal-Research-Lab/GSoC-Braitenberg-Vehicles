package view

import javafx.scene.Group
import javafx.scene.Node

class WorldObjectGroup(vararg children: Node) : Group(*children)
class VehicleGroup(children: List<Node>) : Group(children)