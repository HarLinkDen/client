package me.zeroeightsix.kami.util.items

import me.zeroeightsix.kami.event.SafeClientEvent
import me.zeroeightsix.kami.util.TaskState
import me.zeroeightsix.kami.util.threads.runSafe
import net.minecraft.inventory.ClickType
import net.minecraft.inventory.Slot

class InventoryTask(
    private val id: Int,
    private val priority: Int,
    private val infoArray: Array<out ClickInfo>,
) : Comparable<InventoryTask> {

    val taskState: TaskState = TaskState()
    val isDone get() = taskState.done

    private var index: Int = 0

    fun nextInfo() =
        infoArray.getOrNull(index++).also {
            if (it == null) taskState.done = true
        }

    override fun compareTo(other: InventoryTask): Int {
        val result = priority - other.priority
        return if (result != 0) result
        else other.id - id
    }

    override fun equals(other: Any?) = this === other
        || (other is InventoryTask
        && id == other.id)

    override fun hashCode() = id

}

class ClickInfo(
    private val windowID: Int = 0,
    private val slot: Slot,
    private val mouseButton: Int = 0,
    private val type: ClickType
) {
    fun runClick() {
        runSafe {
            clickSlot(windowID, slot, mouseButton, type)
            playerController.updateController()
        }
    }

    fun runClick(event: SafeClientEvent) {
        event.clickSlot(windowID, slot, mouseButton, type)
        event.playerController.updateController()
    }
}